package com.github.tacowasa059.snakeplayer.Interface;

import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;

import java.util.List;

public interface IPlayerData {
    List<PlayerPart> getPlayerParts();

    boolean getIsSnake();
    void setIsSnake(boolean value);
    float getHeadSize();
    void setHeadSize(float value);

    float getBodySegmentSize();
    void setBodySegmentSize(float value);

    float getSnakeDamage();
    void setSnakeDamage(float value);

    float getSnakeSpeed();
    void setSnakeSpeed(float value);

}
