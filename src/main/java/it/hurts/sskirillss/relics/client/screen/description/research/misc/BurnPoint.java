package it.hurts.sskirillss.relics.client.screen.description.research.misc;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Data
@Builder
public class BurnPoint {
    public static BurnPointBuilder builder(int x, int y, float scale) {
        BurnPointBuilder builder = new BurnPointBuilder();

        builder.x(x);
        builder.y(y);

        builder.maxScale(scale);
        builder.scale(scale);

        builder.link(null);

        return builder;
    }

    @Nullable
    private Pair<Integer, Integer> link;

    private float x;
    private float y;

    private int maxLifeTime;
    private int lifeTime;

    private float maxScale;
    private float scale;
    private float scaleO;

    @Builder.Default
    private Consumer<BurnPoint> ticker = (point) -> {

    };

    public final void tick() {
        getTicker().accept(this);
    }

    public BurnPoint set(BurnPoint other) {
        setLink(other.getLink());

        setX(other.getX());
        setY(other.getY());

        setMaxLifeTime(other.getMaxLifeTime());
        setLifeTime(other.getLifeTime());

        setMaxScale(other.getMaxScale());
        setScaleO(other.getScale());
        setScale(other.getScale());

        setTicker(other.getTicker());

        return this;
    }

    public static class BurnPointBuilder {
        public BurnPointBuilder scale(float scale) {
            this.scaleO = scale;
            this.scale = scale;

            return this;
        }
    }
}