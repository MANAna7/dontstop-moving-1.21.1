package com.hatsukaze.overreaction.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record HitStopPacket(int entityId, float duration)
        implements CustomPacketPayload {

    //type型、名前と送るパケットのパス
    public static final Type<HitStopPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("overreaction", "hit_stop"));

    //それをstreamcodec型に変更
    public static final StreamCodec<ByteBuf, HitStopPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, HitStopPacket::entityId,
                    ByteBufCodecs.FLOAT, HitStopPacket::duration,
                    HitStopPacket::new
            );

    //これは作ったやつの返答用？これってどのタイプ？ってときに返事するメソッド
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}