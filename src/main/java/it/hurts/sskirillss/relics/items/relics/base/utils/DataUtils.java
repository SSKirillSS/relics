package it.hurts.sskirillss.relics.items.relics.base.utils;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

public class DataUtils {
    @Nullable
    public static RelicData getRelicData(Item item) {
        if (!(item instanceof RelicItem relic))
            return null;

        return relic.getRelicData();
    }

    @Nullable
    public static RelicAbilityData getRelicAbilityData(Item item) {
        RelicData relicData = getRelicData(item);

        if (relicData == null)
            return null;

        return relicData.getAbilityData();
    }
}