package com.github.tacowasa059.snakeplayer.common.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Vec3ListDataSerializer implements EntityDataSerializer<List<Vec3>> {
    private static final StreamCodec<RegistryFriendlyByteBuf, List<Vec3>> CODEC = StreamCodec.of(
            (buf, value) -> buf.writeCollection(value, FriendlyByteBuf::writeVec3),
            buf -> buf.readList(FriendlyByteBuf::readVec3)
    );

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, List<Vec3>> codec() {
        return CODEC;
    }

    @Override
    public List<Vec3> copy(List<Vec3> value) {
        return value.stream().map(v -> new Vec3(v.x, v.y, v.z)).toList();
    }
}
