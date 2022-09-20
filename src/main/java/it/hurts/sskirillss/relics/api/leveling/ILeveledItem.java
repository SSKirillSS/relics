package it.hurts.sskirillss.relics.api.leveling;

import it.hurts.sskirillss.relics.configs.data.relics.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.world.item.ItemStack;

public interface ILeveledItem {
    String TAG_EXPERIENCE = "experience";
    String TAG_LEVEL = "level";

    RelicLevelingData getLevelingData();

    default int getLevel(ItemStack stack) {
        return NBTUtils.getInt(stack, TAG_LEVEL, 0);
    }

    default int getLevelFromExperience(int experience) {
        RelicLevelingData data = getLevelingData();

        int min = 0;
        int max = data.getMaxLevel();

        while (min <= max) {
            int mid = (min + max) / 2;
            int exp = getTotalExperienceForLevel(mid);

            if (exp > experience)
                max = mid - 1;
            else
                min = mid + 1;
        }

        return max;
    }

    default void setLevel(ItemStack stack, int level) {
        setExperience(stack, getTotalExperienceForLevel(level));
    }

    default void addLevel(ItemStack stack, int level) {
        setLevel(stack, getLevel(stack) + level);
    }

    default int getExperience(ItemStack stack) {
        return NBTUtils.getInt(stack, TAG_EXPERIENCE, 0);
    }

    default int getExperienceForLevel(int level) {
        return getTotalExperienceForLevel(level + 1) - getTotalExperienceForLevel(level);
    }

    default int getTotalExperienceForLevel(int level) {
        return getTotalExperienceForLevel(getLevelingData(), level);
    }

    default int getTotalExperienceForLevel(RelicLevelingData data, int level) {
        return (2 * data.getInitialCost() + data.getCostRatio() * (level - 1)) * level / 2;
    }

    default void setExperience(ItemStack stack, int experience) {
        RelicLevelingData data = getLevelingData();

        experience = Math.max(0, Math.min(getMaxExperience(), experience));

        NBTUtils.setInt(stack, TAG_LEVEL, Math.max(0, Math.min(data.getMaxLevel(), getLevelFromExperience(experience))));
        NBTUtils.setInt(stack, TAG_EXPERIENCE, experience);
    }

    default void addExperience(ItemStack stack, int experience) {
        setExperience(stack, getExperience(stack) + experience);
    }

    default void takeExperience(ItemStack stack, int experience) {
        setExperience(stack, getExperience(stack) - experience);
    }

    default int getMaxExperience() {
        return getTotalExperienceForLevel(getLevelingData().getMaxLevel());
    }
}