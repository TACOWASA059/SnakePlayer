package com.github.tacowasa059.snakeplayer.mixin.common;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(Player.class)
public abstract class ForgePlayerMultipartMixin implements IForgeEntity {
    @Override
    public boolean isMultipartEntity() {
        IPlayerData playerData = (IPlayerData) this;
        return playerData.snakePlayer$getIsSnake() && !playerData.snakePlayer$getPlayerParts().isEmpty();
    }

    @Override
    public PartEntity<?>[] getParts() {
        List<PlayerPart> parts = ((IPlayerData) this).snakePlayer$getPlayerParts();
        return parts.stream()
                .filter(PartEntity.class::isInstance)
                .map(PartEntity.class::cast)
                .toArray(PartEntity[]::new);
    }
}