package it.hurts.sskirillss.relics.client.screen.description.data;

import lombok.Data;

@Data
public class BurnPoint {
    public BurnPoint(int x, int y, float deltaX, float deltaY, int lifeTime, float scale) {
        setXO(x);
        setYO(y);

        setX(x);
        setY(y);

        setDeltaX(deltaX);
        setDeltaY(deltaY);

        setMaxLifeTime(lifeTime);
        setLifeTime(lifeTime);

        setScale(scale);
    }

    private float xO;
    private float yO;

    private float x;
    private float y;

    private float deltaX;
    private float deltaY;

    private int maxLifeTime;
    private int lifeTime;

    private float scale;

    public BurnPoint set(BurnPoint other) {
        setXO(other.getX());
        setYO(other.getY());

        setX(other.getX());
        setY(other.getY());

        setDeltaX(other.getDeltaX());
        setDeltaY(other.getDeltaY());

        setMaxLifeTime(other.getMaxLifeTime());
        setLifeTime(other.getLifeTime());

        setScale(other.getScale());

        return this;
    }
}