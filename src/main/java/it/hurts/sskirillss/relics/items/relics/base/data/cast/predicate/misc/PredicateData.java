package it.hurts.sskirillss.relics.items.relics.base.data.cast.predicate.misc;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Data
@AllArgsConstructor
public class PredicateData {
    private final Player player;
    private final ItemStack stack;
}