package com.github.tacowasa059.snakeplayer.Interface;

import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;

import java.util.List;

public interface IPlayerData {
    List<PlayerPart> snakePlayer$getPlayerParts();

    boolean snakePlayer$getIsSnake();
    void snakePlayer$setIsSnake(boolean value);
    float snakePlayer$getHeadSize();
    void snakePlayer$setHeadSize(float value);

    float snakePlayer$getBodySegmentSize();
    void snakePlayer$setBodySegmentSize(float value);

    float snakePlayer$getSnakeDamage();
    void snakePlayer$setSnakeDamage(float value);

    float snakePlayer$getSnakeSpeed();
    void snakePlayer$setSnakeSpeed(float value);

    void snakePlayer$setSnakeExperience(int experience);

}
