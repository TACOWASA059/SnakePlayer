package com.github.tacowasa059.snakeplayer.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Vec3ListDataSerializer implements EntityDataSerializer<List<Vec3>> {
    private static final StreamCodec<ByteBuf, List<Vec3>> CODEC = Vec3.STREAM_CODEC.apply(ByteBufCodecs.list());

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, List<Vec3>> codec() {
        return CODEC;
    }

    @Override
    public List<Vec3> copy(List<Vec3> value) {
        return value.stream().map(v -> new Vec3(v.x, v.y, v.z)).toList();
    }
}
