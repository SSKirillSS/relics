package it.hurts.sskirillss.relics.items.relics.base.utils;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class DataUtils {
    @Nullable
    public static RelicData getRelicData(RelicItem relic) {
        return relic.getRelicData();
    }

    @Nullable
    public static RelicData getRelicData(Item item) {
        if (!(item instanceof RelicItem relic))
            return null;

        return getRelicData(relic);
    }

    @Nullable
    public static RelicData getRelicData(ItemStack stack) {
        return getRelicData(stack.getItem());
    }

    @Nullable
    public static RelicAbilityData getRelicAbilityData(RelicItem relic) {
        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return null;

        return relicData.getAbilityData();
    }

    @Nullable
    public static RelicAbilityData getRelicAbilityData(Item item) {
        if (!(item instanceof RelicItem relic))
            return null;

        return getRelicAbilityData(relic);
    }

    @Nullable
    public static RelicAbilityData getRelicAbilityData(ItemStack stack) {
        return getRelicAbilityData(stack.getItem());
    }
}