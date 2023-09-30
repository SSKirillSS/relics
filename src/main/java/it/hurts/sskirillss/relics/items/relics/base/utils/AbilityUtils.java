package it.hurts.sskirillss.relics.items.relics.base.utils;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AbilityUtils {
    public static final String TAG_COOLDOWN_CAP = "cooldown_cap";
    public static final String TAG_COOLDOWN = "cooldown";
    public static final String TAG_TICKING = "ticking";

    public static final String TAG_ABILITIES = "abilities";
    public static final String TAG_POINTS = "points";
    public static final String TAG_STATS = "stats";
    public static final String TAG_TEMP = "temp";

    @Nullable
    public static RelicAbilityData getRelicAbilityData(Item item) {
        RelicData relicData = DataUtils.getRelicData(item);

        if (relicData == null)
            return null;

        return relicData.getAbilityData();
    }

    @Nullable
    public static RelicAbilityEntry getRelicAbilityEntry(Item item, String ability) {
        if (!(item instanceof RelicItem))
            return null;

        RelicAbilityData abilityData = getRelicAbilityData(item);

        if (abilityData == null)
            return null;

        return abilityData.getAbilities().get(ability);
    }

    @Nullable
    public static RelicAbilityStat getRelicAbilityStat(Item item, String ability, String stat) {
        if (!(item instanceof RelicItem))
            return null;

        RelicAbilityEntry abilityEntry = getRelicAbilityEntry(item, ability);

        if (abilityEntry == null)
            return null;

        return abilityEntry.getStats().get(stat);
    }

    public static CompoundTag getAbilitiesTag(ItemStack stack) {
        return stack.getOrCreateTag().getCompound(TAG_ABILITIES);
    }

    public static void setAbilitiesTag(ItemStack stack, CompoundTag nbt) {
        stack.getOrCreateTag().put(TAG_ABILITIES, nbt);
    }

    public static CompoundTag getAbilityTag(ItemStack stack, String ability) {
        CompoundTag data = getAbilitiesTag(stack);

        if (data.isEmpty())
            return new CompoundTag();

        return data.getCompound(ability);
    }

    public static void setAbilityTag(ItemStack stack, String ability, CompoundTag nbt) {
        CompoundTag data = getAbilitiesTag(stack);

        data.put(ability, nbt);

        setAbilitiesTag(stack, data);
    }

    public static CompoundTag getAbilityTempTag(ItemStack stack, String ability) {
        CompoundTag data = getAbilityTag(stack, ability);

        if (data.isEmpty())
            return new CompoundTag();

        return data.getCompound(TAG_TEMP);
    }

    public static void setAbilityTempTag(ItemStack stack, String ability, CompoundTag nbt) {
        CompoundTag data = getAbilityTag(stack, ability);

        data.put(TAG_TEMP, nbt);

        setAbilityTag(stack, ability, data);
    }

    public static Map<String, Double> getAbilityInitialValues(ItemStack stack, String ability) {
        CompoundTag abilityTag = getAbilityTag(stack, ability);

        Map<String, Double> result = new HashMap<>();

        if (abilityTag.isEmpty())
            return result;

        CompoundTag statTag = abilityTag.getCompound(TAG_STATS);

        if (statTag.isEmpty())
            return result;

        statTag.getAllKeys().forEach(entry -> result.put(entry, statTag.getDouble(entry)));

        return result;
    }

    public static double getAbilityInitialValue(ItemStack stack, String ability, String stat) {
        double result;

        try {
            result = getAbilityInitialValues(stack, ability).get(stat);
        } catch (NullPointerException exception) {
            if (AbilityUtils.getRelicAbilityStat(stack.getItem(), ability, stat) != null) {
                randomizeStats(stack, ability);

                result = getAbilityInitialValues(stack, ability).get(stat);
            } else
                result = 0D;
        }

        return result;
    }

    public static double getAbilityValue(ItemStack stack, String ability, String stat, int points) {
        RelicAbilityStat data = getRelicAbilityStat(stack.getItem(), ability, stat);

        double result = 0D;

        if (data == null)
            return result;

        double current = getAbilityInitialValue(stack, ability, stat);
        double step = data.getUpgradeModifier().getValue();

        switch (data.getUpgradeModifier().getKey()) {
            case ADD -> result = current + (points * step);
            case MULTIPLY_BASE -> result = current + ((current * step) * points);
            case MULTIPLY_TOTAL -> result = current * Math.pow(step + 1, points);
        }

        Pair<Double, Double> threshold = data.getThresholdValue();

        return threshold == null ? MathUtils.round(result, 5)
                : MathUtils.round(Math.max(threshold.getKey(), Math.min(threshold.getValue(), result)), 5);
    }

    public static double getAbilityValue(ItemStack stack, String ability, String stat) {
        return AbilityUtils.getAbilityValue(stack, ability, stat, getAbilityPoints(stack, ability));
    }

    public static void setAbilityValue(ItemStack stack, String ability, String stat, double value) {
        CompoundTag data = getAbilitiesTag(stack);
        CompoundTag abilityTag = getAbilityTag(stack, ability);
        CompoundTag statTag = abilityTag.getCompound(TAG_STATS);

        statTag.putDouble(stat, value);
        abilityTag.put(TAG_STATS, statTag);
        data.put(ability, abilityTag);

        setAbilitiesTag(stack, data);
    }

    public static void addAbilityValue(ItemStack stack, String ability, String stat, double value) {
        setAbilityValue(stack, ability, stat, getAbilityValue(stack, ability, stat) + value);
    }

    public static int getAbilityPoints(ItemStack stack, String ability) {
        CompoundTag tag = getAbilityTag(stack, ability);

        if (tag.isEmpty())
            return 0;

        return tag.getInt(TAG_POINTS);
    }

    public static void setAbilityPoints(ItemStack stack, String ability, int amount) {
        getAbilityTag(stack, ability).putInt(TAG_POINTS, Math.max(0, amount));
    }

    public static void addAbilityPoints(ItemStack stack, String ability, int amount) {
        getAbilityTag(stack, ability).putInt(TAG_POINTS, Math.max(0, getAbilityPoints(stack, ability) + amount));
    }

    public static boolean canUseAbility(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(stack.getItem(), ability);

        return entry != null && LevelingUtils.getLevel(stack) >= entry.getRequiredLevel();
    }

    public static boolean randomizeStat(ItemStack stack, String ability, String stat) {
        RelicAbilityStat entry = getRelicAbilityStat(stack.getItem(), ability, stat);

        if (entry == null)
            return false;

        double result = MathUtils.round(MathUtils.randomBetween(new Random(), entry.getInitialValue().getKey(), entry.getInitialValue().getValue()), 5);

        setAbilityValue(stack, ability, stat, result);

        return true;
    }

    public static boolean randomizeStats(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(stack.getItem(), ability);

        if (entry == null)
            return false;

        for (String stat : entry.stats.keySet())
            randomizeStat(stack, ability, stat);

        return true;
    }

    public static int getUpgradeRequiredExperience(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(stack.getItem(), ability);

        if (entry == null)
            return 0;

        int count = entry.getStats().size();

        if (count == 0)
            return 0;

        return (getAbilityPoints(stack, ability) + 1) * entry.getRequiredPoints() * count * 15;
    }

    public static boolean isAbilityMaxLevel(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(stack.getItem(), ability);

        if (entry == null)
            return false;

        return entry.getStats().size() == 0 || getAbilityPoints(stack, ability) >= (entry.getMaxLevel() == -1 ? (LevelingUtils.getRelicLevelingData(stack.getItem()).getMaxLevel() / entry.getRequiredPoints()) : entry.getMaxLevel());
    }

    public static boolean mayUpgrade(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(stack.getItem(), ability);

        if (entry == null)
            return false;

        return entry.getStats().size() > 0 && !isAbilityMaxLevel(stack, ability) && LevelingUtils.getPoints(stack) >= entry.getRequiredPoints() && canUseAbility(stack, ability);
    }

    public static boolean mayPlayerUpgrade(Player player, ItemStack stack, String ability) {
        return mayUpgrade(stack, ability) && player.totalExperience >= getUpgradeRequiredExperience(stack, ability);
    }

    public static int getRerollRequiredExperience(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(stack.getItem(), ability);

        int count = entry.getStats().size();

        if (count == 0)
            return 0;

        return 100 / count;
    }

    public static boolean mayReroll(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(stack.getItem(), ability);

        if (entry == null)
            return false;

        return entry.getStats().size() > 0 && getRerollRequiredExperience(stack, ability) > 0 && canUseAbility(stack, ability);
    }

    public static boolean mayPlayerReroll(Player player, ItemStack stack, String ability) {
        return mayReroll(stack, ability) && player.totalExperience >= getRerollRequiredExperience(stack, ability);
    }

    public static int getResetRequiredExperience(ItemStack stack, String ability) {
        return getAbilityPoints(stack, ability) * 50;
    }

    public static boolean mayReset(ItemStack stack, String ability) {
        return getResetRequiredExperience(stack, ability) > 0 && canUseAbility(stack, ability);
    }

    public static boolean mayPlayerReset(Player player, ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(stack.getItem(), ability);

        if (entry == null)
            return false;

        return entry.getStats().size() > 0 && mayReset(stack, ability) && player.totalExperience >= getResetRequiredExperience(stack, ability);
    }

    public static int getAbilityCooldownCap(ItemStack stack, String ability) {
        return getAbilityTempTag(stack, ability).getInt(TAG_COOLDOWN_CAP);
    }

    public static void setAbilityCooldownCap(ItemStack stack, String ability, int amount) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putInt(TAG_COOLDOWN_CAP, amount);

        setAbilityTempTag(stack, ability, data);
    }

    public static int getAbilityCooldown(ItemStack stack, String ability) {
        return getAbilityTempTag(stack, ability).getInt(TAG_COOLDOWN);
    }

    public static void setAbilityCooldown(ItemStack stack, String ability, int amount) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putInt(TAG_COOLDOWN, amount);
        data.putInt(TAG_COOLDOWN_CAP, amount);

        setAbilityTempTag(stack, ability, data);
    }

    public static void addAbilityCooldown(ItemStack stack, String ability, int amount) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putInt(TAG_COOLDOWN, getAbilityCooldown(stack, ability) + amount);

        setAbilityTempTag(stack, ability, data);
    }

    public static void setAbilityTicking(ItemStack stack, String ability, boolean ticking) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putBoolean(TAG_TICKING, ticking);

        setAbilityTempTag(stack, ability, data);
    }

    public static boolean isAbilityTicking(ItemStack stack, String ability) {
        return getAbilityTempTag(stack, ability).getBoolean(TAG_TICKING);
    }

    public static boolean isAbilityOnCooldown(ItemStack stack, String ability) {
        return getAbilityCooldown(stack, ability) > 0;
    }

    public static boolean canPlayerUseActiveAbility(Player player, ItemStack stack, String ability) {
        return canUseAbility(stack, ability) && !isAbilityOnCooldown(stack, ability)
                && CastUtils.testAbilityCastPredicates(player, stack, ability);
    }
}