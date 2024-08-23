package it.hurts.sskirillss.relics.client.screen.description.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BurnPoint {
    private float x;
    private float y;

    private float deltaX;
    private float deltaY;

    private int lifeTime;

    private float scale;

    private float scaleMultiplier;
}