package com.hatsukaze.overreaction.combat;

import com.hatsukaze.overreaction.attachment.CombatStateAttachment;
import com.hatsukaze.overreaction.data.AttackDefinition;
import com.hatsukaze.overreaction.data.ComboDefinition;
import com.hatsukaze.overreaction.network.PlayAnimationPacket;
import com.hatsukaze.overreaction.registry.ComboRegistry;
import com.hatsukaze.overreaction.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.neoforged.neoforge.network.PacketDistributor;

public class CombatProcessor {

    public static void processAttack(ServerPlayer player) {
        //メインハンドに持ってる武器を登録する、
        ItemStack weapon = player.getMainHandItem();
        // そしてその登録された武器を判定。今は剣以外はスルー（とりあえず剣だけ対応）
        if (!(weapon.getItem() instanceof SwordItem)) return;

        //stateから今の状態を取得してくる。EventHandlerでstateは更新される。
        CombatStateAttachment state = player.getData(ModAttachments.COMBAT_STATE);
        ComboDefinition combo = ComboRegistry.get("one_handed_sword_basic");
        if (combo == null) return;

        // 次の攻撃段を取得
        int nextIndex = state.getAttackIndex();
        AttackDefinition attackDef = combo.getAttack(nextIndex);
        // さっき取得したステートを更新
        state.setCurrentAttackDef(attackDef);
        state.setAttackTimer(0f);
        state.setAttackIndex((nextIndex + 1) % combo.size());
        state.setAnimationPlayed(false); // ← 追加


        if (!attackDef.playOnHit) {
            PlayAnimationPacket packet = new PlayAnimationPacket(player.getId(), attackDef.animationId);
            PacketDistributor.sendToPlayer(player, packet);
        }
    }
}