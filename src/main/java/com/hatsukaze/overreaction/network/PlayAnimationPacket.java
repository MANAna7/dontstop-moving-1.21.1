package com.hatsukaze.overreaction.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PlayAnimationPacket(int entityId, String animationName)
        implements CustomPacketPayload {

    //PlayAnimationPacketのタイプ（種類？）を名前空間とそのパスで作成、ネットワーク上でどのパケットか識別するために使用
    public static final Type<PlayAnimationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("overreaction", "play_animation"));

    //ストリームコーデック、ネットワークのパケット通信用に型を指定するクラス
    //バイト列に変換する/バイト列から復元する をしてくれる
    public static final StreamCodec<ByteBuf, PlayAnimationPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, PlayAnimationPacket::entityId,
                    ByteBufCodecs.STRING_UTF8, PlayAnimationPacket::animationName,
                    PlayAnimationPacket::new
            );

    //作ったやつの返答用？これってどのタイプ？ってときに返事するメソッド
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}