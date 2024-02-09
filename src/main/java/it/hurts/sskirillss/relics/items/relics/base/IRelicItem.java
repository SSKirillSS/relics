package it.hurts.sskirillss.relics.items.relics.base;

import it.hurts.sskirillss.octolib.config.data.ConfigContext;
import it.hurts.sskirillss.octolib.config.data.OctoConfig;
import it.hurts.sskirillss.relics.api.events.leveling.ExperienceAddEvent;
import it.hurts.sskirillss.relics.capability.utils.CapabilityUtils;
import it.hurts.sskirillss.relics.config.ConfigHelper;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.capability.CapabilitySyncPacket;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public interface IRelicItem {
    @Nullable
    default Item getItem() {
        return this instanceof Item item ? item : null;
    }

    RelicData constructDefaultRelicData();

    RelicData getRelicData();

    void setRelicData(RelicData data);

    @Nullable
    default OctoConfig getConfig() {
        return ConfigHelper.getRelicConfig(this);
    }

    default void appendConfig(ConfigContext context) {

    }

    default void castActiveAbility(ItemStack stack, Player player, String ability, AbilityCastType type, AbilityCastStage stage) {

    }

    default void tickActiveAbilitySelection(ItemStack stack, Player player, String ability) {

    }

    @Nullable
    default RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
        return null;
    }

    @Nullable
    default RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return null;
    }

    // TODO: ================================
    // TODO:    Rework this piece of shit
    // TODO: ================================

    // RESEARCH_UTILS

    default CompoundTag getResearchData(Player player) {
        return CapabilityUtils.getRelicsCapability(player).getResearchData();
    }

    default void setResearchData(Player player, CompoundTag data) {
        CapabilityUtils.getRelicsCapability(player).setResearchData(data);

        if (!player.level().isClientSide())
            NetworkHandler.sendToClient(new CapabilitySyncPacket(CapabilityUtils.getRelicsCapability(player).serializeNBT()), (ServerPlayer) player);
    }

    default boolean isItemResearched(Player player) {
        Item item = getItem();

        return item != null && getResearchData(player).getBoolean(ForgeRegistries.ITEMS.getKey(item).getPath() + "_researched");
    }

    default void setItemResearched(Player player, boolean researched) {
        Item item = getItem();

        if (item == null)
            return;

        getResearchData(player).putBoolean(ForgeRegistries.ITEMS.getKey(item).getPath() + "_researched", researched);

        if (!player.level().isClientSide())
            NetworkHandler.sendToClient(new CapabilitySyncPacket(CapabilityUtils.getRelicsCapability(player).serializeNBT()), (ServerPlayer) player);
    }

    // QUALITY_UTILS

    int MAX_QUALITY = 10;

    default int getStatQuality(ItemStack stack, String ability, String stat) {
        RelicAbilityStat statData = getRelicAbilityStat(ability, stat);

        if (statData == null)
            return 0;

        Function<Double, ? extends Number> format = statData.getFormatValue();

        double initial = format.apply(getAbilityInitialValue(stack, ability, stat)).doubleValue();

        double min = format.apply(statData.getInitialValue().getKey()).doubleValue();
        double max = format.apply(statData.getInitialValue().getValue()).doubleValue();

        if (min == max)
            return MAX_QUALITY;

        return Mth.clamp((int) Math.round((initial - min) / ((max - min) / MAX_QUALITY)), 0, MAX_QUALITY);
    }

    default double getStatByQuality(String ability, String stat, int quality) {
        RelicAbilityStat statData = getRelicAbilityStat(ability, stat);

        if (statData == null)
            return 0;

        double min = statData.getInitialValue().getKey();
        double max = statData.getInitialValue().getValue();

        if (min == max)
            return MAX_QUALITY;

        return MathUtils.round(min + (((max - min) / MAX_QUALITY) * quality), 5);
    }

    default int getAbilityQuality(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(ability);

        if (entry == null)
            return 0;

        Map<String, RelicAbilityStat> stats = entry.getStats();

        if (stats.isEmpty())
            return 0;

        int sum = 0;

        for (String stat : stats.keySet())
            sum += getStatQuality(stack, ability, stat);

        return Mth.clamp(sum / stats.size(), 0, MAX_QUALITY);
    }

    default int getRelicQuality(ItemStack stack) {
        RelicAbilityData data = getRelicAbilityData();

        if (data == null)
            return 0;

        Map<String, RelicAbilityEntry> abilities = data.getAbilities();

        if (abilities.isEmpty())
            return 0;

        int size = abilities.size();
        int sum = 0;

        for (String ability : abilities.keySet()) {
            RelicAbilityEntry abilityData = getRelicAbilityEntry(ability);

            if (abilityData == null || abilityData.getMaxLevel() == 0) {
                --size;

                continue;
            }

            sum += getAbilityQuality(stack, ability);
        }

        return Mth.clamp(sum / size, 0, MAX_QUALITY);
    }

    // LEVELING_UTILS

    String TAG_EXPERIENCE = "experience";
    String TAG_EXCHANGES = "exchanges";
    String TAG_LEVELING = "leveling";
    String TAG_POINTS = "points";
    String TAG_LEVEL = "level";

    @Nullable
    default RelicLevelingData getRelicLevelingData() {
        RelicData relicData = getRelicData();

        if (relicData == null)
            return null;

        return relicData.getLevelingData();
    }

    default CompoundTag getLevelingTag(ItemStack stack) {
        return NBTUtils.getCompound(stack, TAG_LEVELING, new CompoundTag());
    }

    default void setLevelingTag(ItemStack stack, CompoundTag data) {
        NBTUtils.setCompound(stack, TAG_LEVELING, data);
    }

    default int getPoints(ItemStack stack) {
        return getLevelingTag(stack).getInt(TAG_POINTS);
    }

    default void setPoints(ItemStack stack, int amount) {
        CompoundTag tag = getLevelingTag(stack);

        tag.putInt(TAG_POINTS, Math.max(0, amount));

        setLevelingTag(stack, tag);
    }

    default void addPoints(ItemStack stack, int amount) {
        setPoints(stack, getPoints(stack) + amount);
    }

    default int getLevel(ItemStack stack) {
        return getLevelingTag(stack).getInt(TAG_LEVEL);
    }

    default void setLevel(ItemStack stack, int level) {
        RelicLevelingData levelingData = getRelicLevelingData();

        if (levelingData == null)
            return;

        CompoundTag tag = getLevelingTag(stack);

        tag.putInt(TAG_LEVEL, Mth.clamp(level, 0, levelingData.getMaxLevel()));

        setLevelingTag(stack, tag);
    }

    default void addLevel(ItemStack stack, int amount) {
        RelicLevelingData levelingData = getRelicLevelingData();

        if (levelingData == null)
            return;

        if (amount > 0)
            addPoints(stack, Mth.clamp(amount, 0, levelingData.getMaxLevel() - getLevel(stack)));

        setLevel(stack, getLevel(stack) + amount);
    }

    default int getExperience(ItemStack stack) {
        return getLevelingTag(stack).getInt(TAG_EXPERIENCE);
    }

    default void setExperience(ItemStack stack, int experience) {
        int level = getLevel(stack);

        RelicLevelingData levelingData = getRelicLevelingData();

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
            data.putInt(TAG_EXPERIENCE, Mth.clamp(experience, 0, requiredExp));

            setLevelingTag(stack, data);
        }
    }

    default void addExperience(ItemStack stack, int amount) {
        addExperience(null, stack, amount);
    }

    default void addExperience(Entity entity, ItemStack stack, int amount) {
        ExperienceAddEvent event = new ExperienceAddEvent(entity instanceof Player ? (Player) entity : null, stack, amount);

        MinecraftForge.EVENT_BUS.post(event);

        if (!event.isCanceled())
            setExperience(stack, getExperience(stack) + event.getAmount());
    }

    default int getExperienceLeftForLevel(ItemStack stack, int level) {
        int currentLevel = getLevel(stack);

        return getExperienceBetweenLevels(stack, currentLevel, level) - getExperience(stack);
    }

    default int getExperienceBetweenLevels(ItemStack stack, int from, int to) {
        return getTotalExperienceForLevel(stack, to) - getTotalExperienceForLevel(stack, from);
    }

    default int getTotalExperienceForLevel(ItemStack stack, int level) {
        if (level <= 0)
            return 0;

        RelicLevelingData levelingData = getRelicLevelingData();

        if (levelingData == null)
            return 0;

        int result = levelingData.getInitialCost();

        for (int i = 1; i < level; i++)
            result += levelingData.getInitialCost() + (levelingData.getStep() * i);

        return result;
    }

    default int getLevelFromExperience(ItemStack stack, int experience) {
        int result = 0;
        int amount;

        do {
            ++result;

            amount = getTotalExperienceForLevel(stack, result);
        } while (amount <= experience);

        return result - 1;
    }

    default boolean isMaxLevel(ItemStack stack) {
        return getLevel(stack) >= getRelicLevelingData().getMaxLevel();
    }

    default int getExchanges(ItemStack stack) {
        return NBTUtils.getInt(stack, TAG_EXCHANGES, 0);
    }

    default void setExchanges(ItemStack stack, int amount) {
        NBTUtils.setInt(stack, TAG_EXCHANGES, Math.max(0, amount));
    }

    default void addExchanges(ItemStack stack, int amount) {
        setExchanges(stack, getExchanges(stack) + amount);
    }

    default int getExchangeCost(ItemStack stack) {
        return (int) (5 + (5 * ((getExchanges(stack)) * 0.01F)));
    }

    default boolean isExchangeAvailable(Player player, ItemStack stack) {
        return getExchangeCost(stack) <= EntityUtils.getPlayerTotalExperience(player);
    }

    // DATA_UTILS

    @Nullable
    default RelicAbilityData getRelicAbilityData() {
        RelicData relicData = getRelicData();

        if (relicData == null)
            return null;

        return relicData.getAbilityData();
    }

    // CAST_UTILS

    @Nullable
    default AbilityCastPredicate getAbilityCastPredicates(String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(ability);

        if (entry == null)
            return null;

        return entry.getCastData().getValue();
    }

    @Nullable
    default PredicateEntry getAbilityCastPredicate(String ability, String predicate) {
        AbilityCastPredicate predicates = getAbilityCastPredicates(ability);

        if (predicates == null)
            return null;

        return predicates.getPredicates().getOrDefault(predicate, null);
    }

    default boolean testAbilityCastPredicate(Player player, ItemStack stack, String ability, String predicate) {
        PredicateEntry entry = getAbilityCastPredicate(ability, predicate);

        if (entry == null)
            return false;

        return entry.getPredicate().apply(new PredicateData(player, stack)).getCondition();
    }

    default boolean testAbilityCastPredicates(Player player, ItemStack stack, String ability) {
        AbilityCastPredicate predicates = getAbilityCastPredicates(ability);

        if (predicates == null)
            return false;

        for (Map.Entry<String, PredicateEntry> entry : predicates.getPredicates().entrySet()) {
            if (!entry.getValue().getPredicate().apply(new PredicateData(player, stack)).getCondition())
                return false;
        }

        return true;
    }

    // ABILITY_UTILS

    String TAG_COOLDOWN_CAP = "cooldown_cap";
    String TAG_COOLDOWN = "cooldown";
    String TAG_TICKING = "ticking";

    String TAG_ABILITIES = "abilities";
    // TODO: Duplicate: String TAG_POINTS = "points";
    String TAG_STATS = "stats";
    String TAG_TEMP = "temp";

    @Nullable
    default RelicAbilityEntry getRelicAbilityEntry(String ability) {
        RelicAbilityData abilityData = getRelicAbilityData();

        if (abilityData == null)
            return null;

        return abilityData.getAbilities().get(ability);
    }

    @Nullable
    default RelicAbilityStat getRelicAbilityStat(String ability, String stat) {
        RelicAbilityEntry abilityEntry = getRelicAbilityEntry(ability);

        if (abilityEntry == null)
            return null;

        return abilityEntry.getStats().get(stat);
    }

    default CompoundTag getAbilitiesTag(ItemStack stack) {
        return stack.getOrCreateTag().getCompound(TAG_ABILITIES);
    }

    default void setAbilitiesTag(ItemStack stack, CompoundTag nbt) {
        stack.getOrCreateTag().put(TAG_ABILITIES, nbt);
    }

    default CompoundTag getAbilityTag(ItemStack stack, String ability) {
        CompoundTag data = getAbilitiesTag(stack);

        if (data.isEmpty())
            return new CompoundTag();

        return data.getCompound(ability);
    }

    default void setAbilityTag(ItemStack stack, String ability, CompoundTag nbt) {
        CompoundTag data = getAbilitiesTag(stack);

        data.put(ability, nbt);

        setAbilitiesTag(stack, data);
    }

    default CompoundTag getAbilityTempTag(ItemStack stack, String ability) {
        CompoundTag data = getAbilityTag(stack, ability);

        if (data.isEmpty())
            return new CompoundTag();

        return data.getCompound(TAG_TEMP);
    }

    default void setAbilityTempTag(ItemStack stack, String ability, CompoundTag nbt) {
        CompoundTag data = getAbilityTag(stack, ability);

        data.put(TAG_TEMP, nbt);

        setAbilityTag(stack, ability, data);
    }

    default Map<String, Double> getAbilityInitialValues(ItemStack stack, String ability) {
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

    default double getAbilityInitialValue(ItemStack stack, String ability, String stat) {
        double result;

        try {
            result = getAbilityInitialValues(stack, ability).get(stat);
        } catch (NullPointerException exception) {
            if (getRelicAbilityStat(ability, stat) != null) {
                randomizeStats(stack, ability);

                result = getAbilityInitialValues(stack, ability).get(stat);
            } else
                result = 0D;
        }

        return result;
    }

    default double getAbilityValue(ItemStack stack, String ability, String stat, int points) {
        RelicAbilityStat data = getRelicAbilityStat(ability, stat);

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

        return MathUtils.round(Mth.clamp(result, threshold.getKey(), threshold.getValue()), 5);
    }

    default double getAbilityValue(ItemStack stack, String ability, String stat) {
        return getAbilityValue(stack, ability, stat, getAbilityPoints(stack, ability));
    }

    default void setAbilityValue(ItemStack stack, String ability, String stat, double value) {
        CompoundTag data = getAbilitiesTag(stack);
        CompoundTag abilityTag = getAbilityTag(stack, ability);
        CompoundTag statTag = abilityTag.getCompound(TAG_STATS);

        statTag.putDouble(stat, value);
        abilityTag.put(TAG_STATS, statTag);
        data.put(ability, abilityTag);

        setAbilitiesTag(stack, data);
    }

    default void addAbilityValue(ItemStack stack, String ability, String stat, double value) {
        setAbilityValue(stack, ability, stat, getAbilityValue(stack, ability, stat) + value);
    }

    default int getAbilityPoints(ItemStack stack, String ability) {
        CompoundTag tag = getAbilityTag(stack, ability);

        if (tag.isEmpty())
            return 0;

        return tag.getInt(TAG_POINTS);
    }

    default void setAbilityPoints(ItemStack stack, String ability, int amount) {
        getAbilityTag(stack, ability).putInt(TAG_POINTS, Math.max(0, amount));
    }

    default void addAbilityPoints(ItemStack stack, String ability, int amount) {
        getAbilityTag(stack, ability).putInt(TAG_POINTS, Math.max(0, getAbilityPoints(stack, ability) + amount));
    }

    default boolean canUseAbility(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(ability);

        return entry != null && getLevel(stack) >= entry.getRequiredLevel();
    }

    default boolean randomizeStat(ItemStack stack, String ability, String stat) {
        RelicAbilityStat entry = getRelicAbilityStat(ability, stat);

        if (entry == null)
            return false;

        double result = MathUtils.round(MathUtils.randomBetween(new Random(), entry.getInitialValue().getKey(), entry.getInitialValue().getValue()), 5);

        setAbilityValue(stack, ability, stat, result);

        return true;
    }

    default boolean randomizeStats(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(ability);

        if (entry == null)
            return false;

        for (String stat : entry.getStats().keySet())
            randomizeStat(stack, ability, stat);

        return true;
    }

    default int getUpgradeRequiredExperience(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(ability);

        if (entry == null)
            return 0;

        int count = entry.getStats().size();

        if (count == 0)
            return 0;

        return (getAbilityPoints(stack, ability) + 1) * entry.getRequiredPoints() * count * 15;
    }

    default boolean isAbilityMaxLevel(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(ability);

        if (entry == null)
            return false;

        return entry.getStats().isEmpty() || getAbilityPoints(stack, ability) >= (entry.getMaxLevel() == -1 ? (getRelicLevelingData().getMaxLevel() / entry.getRequiredPoints()) : entry.getMaxLevel());
    }

    default boolean mayUpgrade(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(ability);

        if (entry == null)
            return false;

        return !entry.getStats().isEmpty() && !isAbilityMaxLevel(stack, ability) && getPoints(stack) >= entry.getRequiredPoints() && canUseAbility(stack, ability);
    }

    default boolean mayPlayerUpgrade(Player player, ItemStack stack, String ability) {
        return mayUpgrade(stack, ability) && player.totalExperience >= getUpgradeRequiredExperience(stack, ability);
    }

    default int getRerollRequiredExperience(String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(ability);

        int count = entry.getStats().size();

        if (count == 0)
            return 0;

        return 100 / count;
    }

    default boolean mayReroll(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(ability);

        if (entry == null)
            return false;

        return !entry.getStats().isEmpty() && getRerollRequiredExperience(ability) > 0 && canUseAbility(stack, ability);
    }

    default boolean mayPlayerReroll(Player player, ItemStack stack, String ability) {
        return mayReroll(stack, ability) && player.totalExperience >= getRerollRequiredExperience(ability);
    }

    default int getResetRequiredExperience(ItemStack stack, String ability) {
        return getAbilityPoints(stack, ability) * 50;
    }

    default boolean mayReset(ItemStack stack, String ability) {
        return getResetRequiredExperience(stack, ability) > 0 && canUseAbility(stack, ability);
    }

    default boolean mayPlayerReset(Player player, ItemStack stack, String ability) {
        RelicAbilityEntry entry = getRelicAbilityEntry(ability);

        if (entry == null)
            return false;

        return !entry.getStats().isEmpty() && mayReset(stack, ability) && player.totalExperience >= getResetRequiredExperience(stack, ability);
    }

    default int getAbilityCooldownCap(ItemStack stack, String ability) {
        return getAbilityTempTag(stack, ability).getInt(TAG_COOLDOWN_CAP);
    }

    default void setAbilityCooldownCap(ItemStack stack, String ability, int amount) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putInt(TAG_COOLDOWN_CAP, amount);

        setAbilityTempTag(stack, ability, data);
    }

    default int getAbilityCooldown(ItemStack stack, String ability) {
        return getAbilityTempTag(stack, ability).getInt(TAG_COOLDOWN);
    }

    default void setAbilityCooldown(ItemStack stack, String ability, int amount) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putInt(TAG_COOLDOWN, amount);
        data.putInt(TAG_COOLDOWN_CAP, amount);

        setAbilityTempTag(stack, ability, data);
    }

    default void addAbilityCooldown(ItemStack stack, String ability, int amount) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putInt(TAG_COOLDOWN, getAbilityCooldown(stack, ability) + amount);

        setAbilityTempTag(stack, ability, data);
    }

    default void setAbilityTicking(ItemStack stack, String ability, boolean ticking) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putBoolean(TAG_TICKING, ticking);

        setAbilityTempTag(stack, ability, data);
    }

    default boolean isAbilityTicking(ItemStack stack, String ability) {
        return getAbilityTempTag(stack, ability).getBoolean(TAG_TICKING);
    }

    default boolean isAbilityOnCooldown(ItemStack stack, String ability) {
        return getAbilityCooldown(stack, ability) > 0;
    }

    default boolean canPlayerUseActiveAbility(Player player, ItemStack stack, String ability) {
        return canUseAbility(stack, ability) && !isAbilityOnCooldown(stack, ability)
                && testAbilityCastPredicates(player, stack, ability);
    }
}