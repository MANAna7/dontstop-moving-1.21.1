package com.hatsukaze.overreaction.network;

import com.hatsukaze.overreaction.ExampleMod;
import com.hatsukaze.overreaction.ExampleModClient;
import com.hatsukaze.overreaction.manager.ClientHitStopManager;
import com.hatsukaze.overreaction.network.PlayAnimationPacket;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.layered.modifier.SpeedModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class AnimationPacketHandler {

    public static void handlePlayAnimation(
            PlayAnimationPacket packet,
            IPayloadContext context) {

        context.enqueueWork(() -> {
            // クライアント処理はここで分岐
            if (context.flow() == PacketFlow.CLIENTBOUND) {
                handleClient(packet);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(PlayAnimationPacket packet) {
        // 元のクライアント処理をここに移動
            Minecraft.getInstance().player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("パケット受信: " + packet.animationName())
            );
            Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId());
            if (!(entity instanceof AbstractClientPlayer player)) return;

            // PALのコントローラー取得
            PlayerAnimationController controller = (PlayerAnimationController)
                    //指定したプレイヤーの, 攻撃用のレイヤーIDを取得
                    PlayerAnimationAccess.getPlayerAnimationLayer(player, ExampleModClient.ATTACK_LAYER_ID);
            if (controller == null) return;

        float speed = packet.animationNaturalTime() / packet.totalAttackingTime();

        // アニメーション再生、名前空間の設定したアニメーションを起動
            //Jsonから取得するから、アニメーション名とあってないといけない
        controller.removeModifierIf(m -> m instanceof SpeedModifier);
        controller.addModifier(new SpeedModifier(speed), 0);
        controller.triggerAnimation(
                    ResourceLocation.fromNamespaceAndPath("overreaction", packet.animationName())
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleHitStop(HitStopPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> handleHitStopClient(packet));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleHitStopClient(HitStopPacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId());
        if (!(entity instanceof AbstractClientPlayer player)) return;

        PlayerAnimationController controller = (PlayerAnimationController)
                PlayerAnimationAccess.getPlayerAnimationLayer(player, ExampleModClient.ATTACK_LAYER_ID);
        if (controller == null) return;

        ClientHitStopManager.start(controller, packet.duration());
    }
}