package com.hatsukaze.overreaction.combat;

import com.hatsukaze.overreaction.attachment.CombatStateAttachment;
import com.hatsukaze.overreaction.data.AttackDefinition;
import com.hatsukaze.overreaction.data.ComboDefinition;
import com.hatsukaze.overreaction.data.ComboNode;
import com.hatsukaze.overreaction.network.PlayAnimationPacket;
import com.hatsukaze.overreaction.registry.ComboRegistry;
import com.hatsukaze.overreaction.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.neoforged.neoforge.network.PacketDistributor;

public class CombatProcessor {

    public static void processAttack(ServerPlayer player) {
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) return;

        CombatStateAttachment state = player.getData(ModAttachments.COMBAT_STATE);
        CombatStateAttachment.CombatPhase phase = state.getPhase();

        // ATTACKING中は新しい攻撃を受け付けない
        // ATTACKING中は先行入力としてバッファ
        if (phase == CombatStateAttachment.CombatPhase.ATTACKING) {
            state.setBufferedInput(true);
            return;
        }
        // RECOVERY中はcancelable判定はTickHandlerに任せる
        if (phase == CombatStateAttachment.CombatPhase.RECOVERY) {
            state.setBufferedInput(true);
            return;
        }

        ComboDefinition combo = ComboRegistry.get("one_handed_sword_basic");
//        System.out.println("combo: " + combo); //DEBUG:
        if (combo == null) return;

        // 次のノードを決定
        ComboNode nextNode;
        String currentNodeId = state.getCurrentNodeId();

        if (state.getPhase() == CombatStateAttachment.CombatPhase.IDLE || currentNodeId == null) {
            // IDLE → エントリーノードから開始
            nextNode = combo.getEntryNode();
        } else {
            // 現在のノードのleftClickNextへ遷移
            ComboNode currentNode = combo.getNode(currentNodeId);
            if (currentNode == null || currentNode.leftClickNext == null) {
                // 終端 → エントリーに戻る
                nextNode = combo.getEntryNode();
            } else {
                nextNode = combo.getNode(currentNode.leftClickNext);
            }
        }

        if (nextNode == null) return;

        AttackDefinition attackDef = nextNode.attackDef;

        // attackingTime を計算して state に保存
        double atkSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED);
        // attackingTime = attackingTimeBase × (1.0 / ATTACK_SPEED)
        // ex: Sword (ATTACK_SPEED=4.0)、attackingTimeBase=1.0 → 0.25s
        //     GreatSword (ATTACK_SPEED=1.0)、attackingTimeBase=1.0 → 1.0s
        float totalAttackingTime = (float)(attackDef.attackingTimeBase / atkSpeed);
        state.setTotalAttackingTime(totalAttackingTime);

        state.setCurrentNodeId(attackDef.id);
        state.setPhase(CombatStateAttachment.CombatPhase.ATTACKING);
        //DEBUG:
        System.out.println("Phase set to ATTACKING, nodeId: " + attackDef.id);

        state.setCurrentAttackDef(attackDef);
        state.setAttackTimer(0f);
        state.setAnimationPlayed(false);

        if (!attackDef.playOnHit) {
            PlayAnimationPacket packet = new PlayAnimationPacket(player.getId(), attackDef.animationId);
            PacketDistributor.sendToPlayer(player, packet);
        }
    }
}