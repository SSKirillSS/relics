package it.hurts.sskirillss.relics.items.relics.base.data.cast;

import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.IRelicContainer;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.RelicContainer;
import lombok.Builder;
import lombok.Data;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.BiFunction;

@Data
@Builder
public class CastData {
    @Builder.Default
    private List<IRelicContainer> container;

    @Builder.Default
    private CastType type = CastType.NONE;

    @Builder.Default
    private Map<String, BiFunction<Player, ItemStack, Boolean>> castPredicates;

    @Builder.Default
    private List<BiFunction<Player, ItemStack, Boolean>> visibilityPredicates;

    public static class CastDataBuilder {
        private Map<String, BiFunction<Player, ItemStack, Boolean>> castPredicates = new HashMap<>();
        List<BiFunction<Player, ItemStack, Boolean>> visibilityPredicates = new ArrayList<>();

        private List<IRelicContainer> container = List.of(RelicContainer.CURIOS);

        public CastDataBuilder castPredicate(String id, BiFunction<Player, ItemStack, Boolean> predicate) {
            castPredicates.put(id, predicate);

            return this;
        }

        public CastDataBuilder visibilityPredicate(BiFunction<Player, ItemStack, Boolean> predicate) {
            visibilityPredicates.add(predicate);

            return this;
        }

        public CastDataBuilder container(RelicContainer... container) {
            this.container = Arrays.asList(container);

            return this;
        }
    }
}