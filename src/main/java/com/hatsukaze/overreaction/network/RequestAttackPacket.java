package com.hatsukaze.overreaction.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

//攻撃用のパケット
public record RequestAttackPacket() implements CustomPacketPayload {

    public static final Type<RequestAttackPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("overreaction", "request_attack"));

    public static final StreamCodec<ByteBuf, RequestAttackPacket> STREAM_CODEC =
            StreamCodec.unit(new RequestAttackPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}