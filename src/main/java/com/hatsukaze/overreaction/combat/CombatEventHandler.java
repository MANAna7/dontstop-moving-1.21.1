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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.hatsukaze.overreaction.ExampleMod.MODID;

//EventBusSubscriber このクラスのstaticメソッドを自動でイベントバスに登録してくれる。ExampleModのコンストラクタでregisterする必要なし！
@EventBusSubscriber(modid = MODID)
public class CombatEventHandler {

    //AttackEntityEvent 敵を殴ったときに発火されるイベント。これで敵を殴ったときに処理起動するMODとかも作れるね
    //殴るたびに発火するから、殴り終わった後に次の攻撃段を取得しておくことで、次殴るときはn+1段目からスタートする(結果的にコンボにつながる)
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        // サーバー側のみ処理
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        //メインハンドに持ってる武器を登録する、
        ItemStack weapon = player.getMainHandItem();

        // そしてその登録された武器を判定。今は剣以外はスルー（とりあえず剣だけ対応）
        if (!(weapon.getItem() instanceof SwordItem)) return;

        // バニラの攻撃処理をキャンセル。通常の攻撃を発生させず、独自攻撃の発生。
        event.setCanceled(true);

        // 戦闘ステートを一回取得
        CombatStateAttachment state = player.getData(ModAttachments.COMBAT_STATE);

        // とりあえず固定でone_handed_sword_basicを使う
        ComboDefinition combo = ComboRegistry.get("one_handed_sword_basic");
        if (combo == null) return; // JSON読み込み失敗してたらスルー

        // 次の攻撃段を取得
        int nextIndex = state.getAttackIndex();
        AttackDefinition attackDef = combo.getAttack(nextIndex);

        // さっき取得したステートを更新
        state.setCurrentAttackDef(attackDef);
        state.setAttackTimer(0f);
        state.setAttackIndex((nextIndex + 1) % combo.size());

        // クライアントにアニメーション再生パケット送る
        PlayAnimationPacket packet = new PlayAnimationPacket(
                player.getId(),
                attackDef.animationId
        );

        if (!attackDef.playOnHit) {
            PacketDistributor.sendToPlayer(player, packet);
            PacketDistributor.sendToPlayersTrackingEntity(player, packet);
        }
    }
}