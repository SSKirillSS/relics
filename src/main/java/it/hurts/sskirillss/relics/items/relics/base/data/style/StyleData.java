package it.hurts.sskirillss.relics.items.relics.base.data.style;

import it.hurts.sskirillss.relics.items.relics.base.data.style.misc.Backgrounds;
import lombok.Builder;
import lombok.Data;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

@Data
@Builder
public class StyleData {
    @Builder.Default
    private ResourceLocation background = Backgrounds.DEFAULT;

    @Nullable
    private Pair<Integer, Integer> borders;

    public static class StyleDataBuilder {
        public StyleDataBuilder borders(Integer top, Integer bottom) {
            borders = Pair.of(top, bottom);

            return this;
        }
    }
}