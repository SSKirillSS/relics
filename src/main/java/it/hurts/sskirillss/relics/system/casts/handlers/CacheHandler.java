package it.hurts.sskirillss.relics.system.casts.handlers;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.RelicContainer;
import it.hurts.sskirillss.relics.system.casts.abilities.AbilityCache;
import it.hurts.sskirillss.relics.system.casts.abilities.AbilityReference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Mod.EventBusSubscriber
public class CacheHandler {
    public static final LinkedHashMap<AbilityReference, AbilityCache> REFERENCES = new LinkedHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != LogicalSide.CLIENT)
            return;

        Player player = event.player;

        LinkedHashMap<AbilityReference, AbilityCache> references = new LinkedHashMap<>();

        for (RelicContainer source : RelicContainer.values()) {
            for (AbilityReference reference : source.gatherAbilities().apply(player)) {
                ItemStack stack = reference.getSlot().gatherStack(player);

                if (!(stack.getItem() instanceof IRelicItem relic))
                    continue;

                AbilityCache cache = REFERENCES.getOrDefault(reference, new AbilityCache());

                Map<String, Boolean> predicates = cache.getPredicates();

                for (Map.Entry<String, BiFunction<Player, ItemStack, Boolean>> predicate : relic.getAbilityCastData(reference.getId()).getPredicates().entrySet())
                    predicates.put(predicate.getKey(), predicate.getValue().apply(player, stack));

                cache.setPredicates(predicates);

                if (cache.getIconShakeDelta() > 0)
                    cache.setIconShakeDelta(cache.getIconShakeDelta() - 1);

                references.put(reference, cache);
            }
        }

        REFERENCES.clear();
        REFERENCES.putAll(references);
    }
}