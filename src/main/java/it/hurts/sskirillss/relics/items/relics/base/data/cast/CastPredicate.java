package it.hurts.sskirillss.relics.items.relics.base.data.cast;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.BiFunction;

@Data
@Builder
public class CastPredicate {
    @Singular
    private Map<String, BiFunction<Player, ItemStack, Boolean>> predicates;
}