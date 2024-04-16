package it.hurts.sskirillss.relics.items.relics.base.data.cast;

import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.RelicContainer;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.IRelicContainer;
import lombok.Builder;
import lombok.Data;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Data
@Builder
public class CastData {
    @Builder.Default
    private List<IRelicContainer> container;

    @Builder.Default
    private CastType type = CastType.NONE;

    @Builder.Default
    private Map<String, BiFunction<Player, ItemStack, Boolean>> predicates;

    public static class CastDataBuilder {
        private Map<String, BiFunction<Player, ItemStack, Boolean>> predicates = new HashMap<>();

        private List<IRelicContainer> container = List.of(RelicContainer.CURIOS);

        public CastDataBuilder predicate(String id, BiFunction<Player, ItemStack, Boolean> predicate) {
            predicates.put(id, predicate);

            return this;
        }

        public CastDataBuilder container(RelicContainer... container) {
            this.container = Arrays.asList(container);

            return this;
        }
    }
}