package it.hurts.sskirillss.relics.items.relics.base.utils;

import it.hurts.sskirillss.relics.api.events.leveling.ExperienceAddEvent;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class LevelingUtils {
    public static final String TAG_EXPERIENCE = "experience";
    public static final String TAG_LEVELING = "leveling";
    public static final String TAG_POINTS = "points";
    public static final String TAG_LEVEL = "level";

    @Nullable
    public static RelicLevelingData getRelicLevelingData(Item item) {
        RelicData relicData = DataUtils.getRelicData(item);

        if (relicData == null)
            return null;

        return relicData.getLevelingData();
    }

    public static CompoundTag getLevelingTag(ItemStack stack) {
        return NBTUtils.getCompound(stack, TAG_LEVELING, new CompoundTag());
    }

    public static void setLevelingTag(ItemStack stack, CompoundTag data) {
        NBTUtils.setCompound(stack, TAG_LEVELING, data);
    }

    public static int getPoints(ItemStack stack) {
        return getLevelingTag(stack).getInt(TAG_POINTS);
    }

    public static void setPoints(ItemStack stack, int amount) {
        CompoundTag tag = getLevelingTag(stack);

        tag.putInt(TAG_POINTS, Math.max(0, amount));

        setLevelingTag(stack, tag);
    }

    public static void addPoints(ItemStack stack, int amount) {
        setPoints(stack, getPoints(stack) + amount);
    }

    public static int getLevel(ItemStack stack) {
        return getLevelingTag(stack).getInt(TAG_LEVEL);
    }

    public static void setLevel(ItemStack stack, int level) {
        RelicLevelingData levelingData = getRelicLevelingData(stack.getItem());

        if (levelingData == null)
            return;

        CompoundTag tag = getLevelingTag(stack);

        tag.putInt(TAG_LEVEL, Mth.clamp(level, 0, levelingData.getMaxLevel()));

        setLevelingTag(stack, tag);
    }

    public static void addLevel(ItemStack stack, int amount) {
        RelicLevelingData levelingData = getRelicLevelingData(stack.getItem());

        if (levelingData == null)
            return;

        if (amount > 0)
            addPoints(stack, Mth.clamp(amount, 0, levelingData.getMaxLevel() - getLevel(stack)));

        setLevel(stack, getLevel(stack) + amount);
    }

    public static int getExperience(ItemStack stack) {
        return getLevelingTag(stack).getInt(TAG_EXPERIENCE);
    }

    public static void setExperience(ItemStack stack, int experience) {
        int level = getLevel(stack);

        RelicLevelingData levelingData = getRelicLevelingData(stack.getItem());

        if (levelingData == null || level >= levelingData.getMaxLevel())
            return;

        int requiredExp = getExperienceBetweenLevels(stack, level, level + 1);

        CompoundTag data = getLevelingTag(stack);

        if (experience >= requiredExp) {
            int sumExp = getTotalExperienceForLevel(stack, level) + experience;
            int resultLevel = getLevelFromExperience(stack, sumExp);

            data.putInt(TAG_EXPERIENCE, Math.max(0, sumExp - getTotalExperienceForLevel(stack, resultLevel)));

            setLevelingTag(stack, data);
            addPoints(stack, resultLevel - level);
            setLevel(stack, resultLevel);
        } else {
            data.putInt(TAG_EXPERIENCE, Math.max(0, experience));

            setLevelingTag(stack, data);
        }
    }

    public static void addExperience(ItemStack stack, int amount) {
        addExperience(null, stack, amount);
    }

    public static void addExperience(Entity entity, ItemStack stack, int amount) {
        ExperienceAddEvent event = new ExperienceAddEvent(entity instanceof Player ? (Player) entity : null, stack, amount);

        MinecraftForge.EVENT_BUS.post(event);

        if (!event.isCanceled())
            setExperience(stack, getExperience(stack) + event.getAmount());
    }

    public static int getExperienceLeftForLevel(ItemStack stack, int level) {
        int currentLevel = getLevel(stack);

        return getExperienceBetweenLevels(stack, currentLevel, level) - getExperience(stack);
    }

    public static int getExperienceBetweenLevels(ItemStack stack, int from, int to) {
        return getTotalExperienceForLevel(stack, to) - getTotalExperienceForLevel(stack, from);
    }

    public static int getTotalExperienceForLevel(ItemStack stack, int level) {
        if (level <= 0)
            return 0;

        RelicLevelingData levelingData = getRelicLevelingData(stack.getItem());

        if (levelingData == null)
            return 0;

        int result = levelingData.getInitialCost();

        for (int i = 1; i < level; i++)
            result += levelingData.getInitialCost() + (levelingData.getStep() * i);

        return result;
    }

    public static int getLevelFromExperience(ItemStack stack, int experience) {
        int result = 0;
        int amount;

        do {
            ++result;

            amount = getTotalExperienceForLevel(stack, result);
        } while (amount <= experience);

        return result - 1;
    }
}