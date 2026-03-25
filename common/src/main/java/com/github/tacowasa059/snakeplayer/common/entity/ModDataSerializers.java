package com.github.tacowasa059.snakeplayer.common.entity;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ModDataSerializers {
    public static final EntityDataSerializer<List<Vec3>> VEC3_LIST = new Vec3ListDataSerializer();

    public static void register() {
        // EntityDataSerializer 縺ｮ逋ｻ骭ｲ
        EntityDataSerializers.registerSerializer(VEC3_LIST);
    }
}
