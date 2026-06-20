package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.client.duck.ISnakeRenderState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class PlayerRenderStateMixin implements ISnakeRenderState {
    @Unique
    private AbstractClientPlayer snakePlayer$player;
    @Unique
    private float snakePlayer$partialTick;

    @Override
    public void snakePlayer$setPlayer(AbstractClientPlayer player) {
        this.snakePlayer$player = player;
    }

    @Override
    public AbstractClientPlayer snakePlayer$getPlayer() {
        return this.snakePlayer$player;
    }

    @Override
    public void snakePlayer$setPartialTick(float partialTick) {
        this.snakePlayer$partialTick = partialTick;
    }

    @Override
    public float snakePlayer$getPartialTick() {
        return this.snakePlayer$partialTick;
    }
}
