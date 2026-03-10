package com.hatsukaze.overreaction.combat;

import com.hatsukaze.overreaction.attachment.CombatStateAttachment;
import com.hatsukaze.overreaction.data.AttackDefinition;
import com.hatsukaze.overreaction.network.HitStopPacket;
import com.hatsukaze.overreaction.network.PlayAnimationPacket;
import com.hatsukaze.overreaction.registry.ModAttachments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

import static com.hatsukaze.overreaction.ExampleMod.MODID;

@EventBusSubscriber(modid = MODID)
public class CombatTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        CombatStateAttachment state = player.getData(ModAttachments.COMBAT_STATE);
        CombatStateAttachment.CombatPhase phase = state.getPhase();

        System.out.println("Tick phase: " + phase); //DEBUG:

        // IDLEなら何もしない
        if (phase == CombatStateAttachment.CombatPhase.IDLE) return;

        AttackDefinition currentAttack = state.getCurrentAttackDef();
        if (currentAttack == null) return;

        float timer = state.getAttackTimer() + 0.05f;
        state.setAttackTimer(timer);

        switch (phase) {
            case ATTACKING -> {
                //割合で攻撃コライダーを生成するタイミングを変える。
                float total = state.getTotalAttackingTime();
                float hwStart = total * currentAttack.hitWindowStart;
                float hwEnd   = total * currentAttack.hitWindowEnd;

                //ヒット判定のウィンドウを開始
                if (timer >= hwStart && timer <= hwEnd) {
                    List<Entity> targets = performHitDetection(player, currentAttack, state);
                    if (!targets.isEmpty() && currentAttack.hitStop != null) {
                        state.setPhase(CombatStateAttachment.CombatPhase.HIT_STOP);
                        state.setAttackTimer(0f);
                        HitStopPacket packet = new HitStopPacket(
                                player.getId(),
                                currentAttack.hitStop.duration
                        );
                        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
                        return;
                    }
                }

                // ヒットウィンドウ終了 → RECOVERYへ（recoveryがなければIDLEへ）
                if (timer > total) {  // hitWindowEndじゃなくてtotalで終了
                    if (currentAttack.recovery != null) {
                        state.setPhase(CombatStateAttachment.CombatPhase.RECOVERY);
                        state.setAttackTimer(0f);
                    } else {
                        resetToIdle(state);
                    }
                }
            }

            case HIT_STOP -> {
                if (currentAttack.hitStop == null || timer > currentAttack.hitStop.duration) {
                    if (currentAttack.recovery != null) {
                        state.setPhase(CombatStateAttachment.CombatPhase.RECOVERY);
                        state.setAttackTimer(0f);
                    } else {
                        resetToIdle(state);
                    }
                }
            }

            case RECOVERY -> {
                if (currentAttack.recovery == null) {
                    startComboWindow(state);
                    return;
                }
                if (currentAttack.recovery.cancelable && state.hasBufferedInput()) {
                    state.setBufferedInput(false);
                    state.setPhase(CombatStateAttachment.CombatPhase.COMBO_WINDOW); //先にフェーズ変更
                    CombatProcessor.processAttack(player);
                    return;
                }
                if (timer > currentAttack.recovery.duration) {
                    startComboWindow(state);
                }
            }
            case COMBO_WINDOW -> {
                float windowTimer = state.getComboWindowTimer() + 0.05f;
                state.setComboWindowTimer(windowTimer);

                if (state.hasBufferedInput()) {
                    state.setBufferedInput(false);
                    CombatProcessor.processAttack(player);
                    return;
                }
                // 0.3秒でタイムアウト、入力受付時間をここで設定している
                if (windowTimer > 0.5f) {
                    resetToIdle(state);
                }
            }
        }
    }

    private static void resetToIdle(CombatStateAttachment state) {
        state.setPhase(CombatStateAttachment.CombatPhase.IDLE);
        state.setCurrentAttackDef(null);
        state.setCurrentNodeId(null);
        state.setAttackTimer(0f);
        state.setComboWindowTimer(-1f);
        state.setBufferedInput(false);
    }

    private static void startComboWindow(CombatStateAttachment state) {
        state.setPhase(CombatStateAttachment.CombatPhase.COMBO_WINDOW);
        state.setComboWindowTimer(0f);
        state.setAttackTimer(0f);
        state.setBufferedInput(false);
    }

    private static List performHitDetection(ServerPlayer player,
                                            AttackDefinition attackDef,
                                            CombatStateAttachment state) {
        Vec3 playerPos = player.position();
        Vec3 lookVec = player.getLookAngle();
        Vec3 center = playerPos.add(lookVec.scale(1.5));

        AABB hitbox = new AABB(
                center.x - 1, center.y, center.z - 1,
                center.x + 1, center.y + 2, center.z + 1
        );

        List<Entity> targets = player.level().getEntities(
                player, hitbox, e -> e instanceof LivingEntity && e != player
        );

        // DEBUG: particle
//        if (player.level() instanceof ServerLevel serverLevel) {
//            for (double x = hitbox.minX; x <= hitbox.maxX; x += 0.5) {
//                for (double y = hitbox.minY; y <= hitbox.maxY; y += 0.5) {
//                    for (double z = hitbox.minZ; z <= hitbox.maxZ; z += 0.5) {
//                        serverLevel.sendParticles(ParticleTypes.FLAME,
//                                x, y, z, 1, 0, 0, 0, 0);
//                    }
//                }
//            }
//        }

        for (Entity target : targets) {
            if (state.hasHit(target.getUUID())) continue;
            state.addHitEntity(target.getUUID());

            float damage = 5.0f * attackDef.damageMultiplier;
            DamageSource damageSource = player.damageSources().playerAttack(player);
            target.hurt(damageSource, damage);
        }

        if (!targets.isEmpty() && attackDef.playOnHit && !state.hasAnimationPlayed()) {
            state.setAnimationPlayed(true);
            PlayAnimationPacket packet = new PlayAnimationPacket(
                    player.getId(),
                    attackDef.animationId,
                    state.getTotalAttackingTime(),
                    attackDef.animationNaturalTime

            );
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
        }
        return targets;
    }
}