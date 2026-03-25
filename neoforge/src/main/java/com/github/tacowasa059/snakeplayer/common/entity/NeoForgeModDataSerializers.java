package com.github.tacowasa059.snakeplayer.common.entity;

import com.github.tacowasa059.snakeplayer.SnakePlayer;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public final class NeoForgeModDataSerializers {
    private static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, SnakePlayer.MODID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<List<Vec3>>> VEC3_LIST =
            ENTITY_DATA_SERIALIZERS.register("vec3_list", () -> ModDataSerializers.VEC3_LIST);

    private static boolean registered;

    private NeoForgeModDataSerializers() {
    }

    public static void register(IEventBus modEventBus) {
        if (registered) {
            return;
        }
        ENTITY_DATA_SERIALIZERS.register(modEventBus);
        registered = true;
    }
}
