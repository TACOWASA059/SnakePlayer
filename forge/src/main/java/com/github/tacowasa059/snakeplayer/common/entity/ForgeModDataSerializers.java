package com.github.tacowasa059.snakeplayer.common.entity;

import com.github.tacowasa059.snakeplayer.SnakePlayer;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/**
 * Forge では独自の {@code forge:entity_data_serializers} レジストリ ID をネットワーク同期に使うため、
 * バニラの {@link net.minecraft.network.syncher.EntityDataSerializers#registerSerializer} ではなく
 * このレジストリへ登録する必要がある（NeoForge と同じ方式）。
 * これを怠ると PART_POSITIONS の同期 ID がクライアントとサーバーで一致せず、
 * マルチでセグメントの動きが飛び飛びになる。
 */
public final class ForgeModDataSerializers {
    private static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.ENTITY_DATA_SERIALIZERS, SnakePlayer.MODID);

    public static final RegistryObject<EntityDataSerializer<List<Vec3>>> VEC3_LIST =
            ENTITY_DATA_SERIALIZERS.register("vec3_list", () -> ModDataSerializers.VEC3_LIST);

    private static boolean registered;

    private ForgeModDataSerializers() {
    }

    public static void register(BusGroup modBusGroup) {
        if (registered) {
            return;
        }
        ENTITY_DATA_SERIALIZERS.register(modBusGroup);
        registered = true;
    }
}
