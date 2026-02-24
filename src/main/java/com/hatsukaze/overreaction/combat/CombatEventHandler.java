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
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        event.setCanceled(true);
    }
}