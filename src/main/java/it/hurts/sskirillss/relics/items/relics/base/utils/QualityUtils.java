package it.hurts.sskirillss.relics.items.relics.base.utils;

import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.Function;

public class QualityUtils {
    public static final int MAX_QUALITY = 10;

    public static int getStatQuality(ItemStack stack, String ability, String stat) {
        RelicAbilityStat statData = AbilityUtils.getRelicAbilityStat(stack.getItem(), ability, stat);

        if (statData == null)
            return 0;

        Function<Double, ? extends Number> format = statData.getFormatValue();

        double initial = format.apply(AbilityUtils.getAbilityInitialValue(stack, ability, stat)).doubleValue();

        double min = format.apply(statData.getInitialValue().getKey()).doubleValue();
        double max = format.apply(statData.getInitialValue().getValue()).doubleValue();

        if (min == max)
            return MAX_QUALITY;

        return (int) Math.round((initial - min) / ((max - min) / MAX_QUALITY));
    }

    public static int getAbilityQuality(ItemStack stack, String ability) {
        RelicAbilityEntry entry = AbilityUtils.getRelicAbilityEntry(stack.getItem(), ability);

        if (entry == null)
            return 0;

        Map<String, RelicAbilityStat> stats = entry.getStats();

        if (stats.isEmpty())
            return 0;

        int sum = 0;

        for (String stat : stats.keySet())
            sum += getStatQuality(stack, ability, stat);

        return sum / stats.size();
    }

    public static int getRelicQuality(ItemStack stack) {
        RelicAbilityData data = AbilityUtils.getRelicAbilityData(stack.getItem());

        if (data == null)
            return 0;

        Map<String, RelicAbilityEntry> abilities = data.getAbilities();

        if (abilities.isEmpty())
            return 0;

        int size = abilities.size();
        int sum = 0;

        for (String ability : abilities.keySet()) {
            RelicAbilityEntry abilityData = AbilityUtils.getRelicAbilityEntry(stack.getItem(), ability);

            if (abilityData == null || abilityData.getMaxLevel() == 0) {
                --size;

                continue;
            }

            sum += getAbilityQuality(stack, ability);
        }

        return sum / size;
    }
}