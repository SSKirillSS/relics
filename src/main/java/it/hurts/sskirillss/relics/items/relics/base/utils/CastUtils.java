package it.hurts.sskirillss.relics.items.relics.base.utils;

import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;

public class CastUtils {
    @Nullable
    public static AbilityCastPredicate getAbilityCastPredicates(Item item, String ability) {
        RelicAbilityEntry entry = AbilityUtils.getRelicAbilityEntry(item, ability);

        if (entry == null)
            return null;

        return entry.getCastData().getValue();
    }

    @Nullable
    public static PredicateEntry getAbilityCastPredicate(Item item, String ability, String predicate) {
        AbilityCastPredicate predicates = getAbilityCastPredicates(item, ability);

        if (predicates == null)
            return null;

        return predicates.getPredicates().getOrDefault(predicate, null);
    }

    public static boolean testAbilityCastPredicate(Player player, ItemStack stack, String ability, String predicate) {
        PredicateEntry entry = getAbilityCastPredicate(stack.getItem(), ability, predicate);

        if (entry == null)
            return false;

        return entry.getPredicate().apply(new PredicateData(player, stack)).getCondition();
    }

    public static boolean testAbilityCastPredicates(Player player, ItemStack stack, String ability) {
        AbilityCastPredicate predicates = getAbilityCastPredicates(stack.getItem(), ability);

        if (predicates == null)
            return false;

        for (Map.Entry<String, PredicateEntry> entry : predicates.getPredicates().entrySet()) {
            if (!entry.getValue().getPredicate().apply(new PredicateData(player, stack)).getCondition())
                return false;
        }

        return true;
    }
}