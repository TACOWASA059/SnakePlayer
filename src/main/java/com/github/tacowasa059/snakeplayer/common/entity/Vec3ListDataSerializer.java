package com.github.tacowasa059.snakeplayer.common.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Vec3ListDataSerializer implements EntityDataSerializer<List<Vec3>> {
    @Override
    public void write(FriendlyByteBuf buf, List<Vec3> value) {
        buf.writeInt(value.size());
        for (Vec3 pos : value) {
            buf.writeDouble(pos.x());
            buf.writeDouble(pos.y());
            buf.writeDouble(pos.z());
        }
    }

    @Override
    public @NotNull List<Vec3> read(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<Vec3> positions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            positions.add(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        return positions;
    }
    @Override
    public @NotNull List<Vec3> copy(List<Vec3> value) {
        return value.stream().map(v -> new Vec3(v.x, v.y, v.z)).toList();
    }
}
