package it.hurts.sskirillss.relics.client.screen.description.data;

import lombok.Builder;
import lombok.Data;

import java.util.function.Consumer;

@Data
@Builder(toBuilder = true)
public class BurnPoint {
    public static BurnPointBuilder builder(int x, int y, int lifeTime, float scale) {
        BurnPointBuilder builder = new BurnPointBuilder();

        builder.xO(x);
        builder.yO(y);

        builder.x(x);
        builder.y(y);

        builder.maxLifeTime(lifeTime);
        builder.lifeTime(lifeTime);

        builder.scale(scale);

        return builder;
    }

    private float xO;
    private float yO;

    private float x;
    private float y;

    private int maxLifeTime;
    private int lifeTime;

    private float scale;

    @Builder.Default
    private Consumer<BurnPoint> ticker = (point) -> {

    };

    public BurnPoint set(BurnPoint other) {
        setXO(other.getX());
        setYO(other.getY());

        setX(other.getX());
        setY(other.getY());

        setMaxLifeTime(other.getMaxLifeTime());
        setLifeTime(other.getLifeTime());

        setScale(other.getScale());

        return this;
    }
}