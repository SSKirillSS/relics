package it.hurts.sskirillss.relics.items.relics.base.data.cast.misc;

import it.hurts.sskirillss.relics.system.casts.abilities.AbilityReference;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Function;

public interface ICastSource {
    Function<Player, List<AbilityReference>> processInventory();
}