package com.hatsukaze.overreaction.combat;

import com.hatsukaze.overreaction.attachment.CombatStateAttachment;
import com.hatsukaze.overreaction.data.AttackDefinition;
import com.hatsukaze.overreaction.network.PlayAnimationPacket;
import com.hatsukaze.overreaction.registry.ModAttachments;
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

//EventBusSubscriber このクラスのstaticメソッドを自動でイベントバスに登録してくれる。ExampleModのコンストラクタでregisterする必要なし！
@EventBusSubscriber(modid = MODID)
public class CombatTickHandler {

    //onPlayerTick プレイヤーのtick処理が終わった後に発火される。unityでいうUpdate()みたいなやつ
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        //stateから今の状態を取得してくる。EventHandlerでstateは更新される。
        CombatStateAttachment state = player.getData(ModAttachments.COMBAT_STATE);
        AttackDefinition currentAttack = state.getCurrentAttackDef();

        if (currentAttack == null) return; // 攻撃中じゃなければ何もしない

        // タイマーを進める（1tick = 0.05秒）
        float timer = state.getAttackTimer() + 0.05f;
        state.setAttackTimer(timer);

        // hit_windowに入ったらヒット判定
        if (timer >= currentAttack.hitWindowStart && timer <= currentAttack.hitWindowEnd) {
            performHitDetection(player, currentAttack, state);
        }

        // cooldown超えたらリセット
        if (timer > currentAttack.cooldown) {
            state.setCurrentAttackDef(null);
            state.setAttackTimer(0f);
        }
    }

    //攻撃の当たり判定処理===================
    private static void performHitDetection(ServerPlayer player,
                                            AttackDefinition attackDef,
                                            CombatStateAttachment state) {
        // とりあえず簡易AABB（前方2x2x2ブロック固定）
        Vec3 playerPos = player.position();
        Vec3 lookVec = player.getLookAngle();
        Vec3 center = playerPos.add(lookVec.scale(1.5)); // 前方1.5ブロック

        AABB hitbox = new AABB(
                center.x - 1, center.y, center.z - 1,
                center.x + 1, center.y + 2, center.z + 1
        );

        // AABB内のエンティティ取得
        List<Entity> targets = player.level().getEntities(
                player, hitbox, e -> e instanceof LivingEntity && e != player
        );

        // ダメージ適用もここで===================
        for (Entity target : targets) {
            if (state.hasHit(target.getUUID())) continue; // 同一攻撃で2回当てない
            state.addHitEntity(target.getUUID());

            // とりあえず固定5ダメージ（後で武器の攻撃力×倍率に変える）
            float damage = 5.0f * attackDef.damageMultiplier;
            System.out.println("Damage: " + damage + " (multiplier: " + attackDef.damageMultiplier + ")"); // ←追加
            DamageSource damageSource = player.damageSources().playerAttack(player);
            target.hurt(damageSource, damage);
        }

        //EventHandlerでfalse-ifやってるからこっちはそれが再生されなかった場合、ヒット後に再生
        if (!targets.isEmpty() && attackDef.playOnHit && !state.hasAnimationPlayed()) {
            state.setAnimationPlayed(true);
            PlayAnimationPacket packet = new PlayAnimationPacket(
                    player.getId(),
                    attackDef.animationId
            );
            System.out.println("TickHandler送信");
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
        }

    }
}