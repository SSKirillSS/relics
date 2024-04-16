package it.hurts.sskirillss.relics.items.relics.base.data.cast.misc;

import it.hurts.sskirillss.relics.system.casts.abilities.AbilityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Function;

public interface IRelicContainer {
    Function<LivingEntity, List<ItemStack>> gatherRelics();

    Function<LivingEntity, List<AbilityReference>> gatherAbilities();
}