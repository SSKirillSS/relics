package it.hurts.sskirillss.relics.items.relics.base;

import it.hurts.sskirillss.octolib.config.data.ConfigContext;
import it.hurts.sskirillss.octolib.config.data.OctoConfig;
import it.hurts.sskirillss.relics.api.events.leveling.ExperienceAddEvent;
import it.hurts.sskirillss.relics.capability.utils.CapabilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.config.ConfigHelper;
import it.hurts.sskirillss.relics.entities.RelicExperienceOrbEntity;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStorage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.predicate.PredicateEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.predicate.misc.PredicateData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

    @Nullable
    default OctoConfig getConfig() {
        return ConfigHelper.getRelicConfig(this);
    }

    default void appendConfig(ConfigContext context) {

    }

    default void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {

    }

    default void tickActiveAbilitySelection(ItemStack stack, Player player, String ability) {

    }

    @Nullable
    default RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
        return RelicAttributeModifier.builder().build();
    }

    @Nullable
    default RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return RelicSlotModifier.builder().build();
    }


    default RelicData getRelicData() {
        if (!RelicStorage.RELICS.containsKey(this))
            RelicStorage.RELICS.put(this, constructDefaultRelicData());

        return RelicStorage.RELICS.get(this);
    }

    default void setRelicData(RelicData data) {
        RelicStorage.RELICS.put(this, data);
    }

    default AbilityData getAbilityData(String ability) {
        return getRelicData().getAbilities().getAbilities().get(ability);
    }

    default StatData getStatData(String ability, String stat) {
        return getRelicData().getAbilities().getAbilities().get(ability).getStats().get(stat);
    }

    default LevelingData getLevelingData() {
        return getRelicData().getLeveling();
    }

    default LootData getLootData() {
        return getRelicData().getLoot();
    }

    default StyleData getStyleData() {
        return getRelicData().getStyle();
    }

    default boolean isItemResearched(Player player) {
        Item item = getItem();

        return item != null && CapabilityUtils.getRelicsCapability(player).getResearchData().getBoolean(ForgeRegistries.ITEMS.getKey(item).getPath() + "_researched");
    }

    default void setItemResearched(Player player, boolean researched) {
        Item item = getItem();

        if (item == null)
            return;

        CapabilityUtils.getRelicsCapability(player).getResearchData().putBoolean(ForgeRegistries.ITEMS.getKey(item).getPath() + "_researched", researched);

        if (!player.getLevel().isClientSide())
            NetworkHandler.sendToClient(new CapabilitySyncPacket(CapabilityUtils.getRelicsCapability(player).serializeNBT()), (ServerPlayer) player);
    }

    default int getMaxQuality() {
        return 10;
    }

    default int getStatQuality(ItemStack stack, String ability, String stat) {
        StatData statData = getStatData(ability, stat);

        if (statData == null)
            return 0;

        Function<Double, ? extends Number> format = statData.getFormatValue();

        double initial = format.apply(getAbilityInitialValue(stack, ability, stat)).doubleValue();

        double min = format.apply(statData.getInitialValue().getKey()).doubleValue();
        double max = format.apply(statData.getInitialValue().getValue()).doubleValue();

        if (min == max)
            return getMaxQuality();

        return Mth.clamp((int) Math.round((initial - min) / ((max - min) / getMaxQuality())), 0, getMaxQuality());
    }

    default double getStatByQuality(String ability, String stat, int quality) {
        StatData statData = getStatData(ability, stat);

        if (statData == null)
            return 0;

        double min = statData.getInitialValue().getKey();
        double max = statData.getInitialValue().getValue();

        if (min == max)
            return getMaxQuality();

        return MathUtils.round(min + (((max - min) / getMaxQuality()) * quality), 5);
    }

    default int getAbilityQuality(ItemStack stack, String ability) {
        Map<String, StatData> stats = getAbilityData(ability).getStats();

        if (stats.isEmpty())
            return 0;

        int sum = 0;

        for (String stat : stats.keySet())
            sum += getStatQuality(stack, ability, stat);

        return Mth.clamp(sum / stats.size(), 0, getMaxQuality());
    }

    default int getRelicQuality(ItemStack stack) {
        Map<String, AbilityData> abilities = getRelicData().getAbilities().getAbilities();

        if (abilities.isEmpty())
            return 0;

        int size = abilities.size();
        int sum = 0;

        for (Map.Entry<String, AbilityData> entry : abilities.entrySet()) {
            if (entry.getValue().getMaxLevel() == 0) {
                --size;

                continue;
            }

            sum += getAbilityQuality(stack, entry.getKey());
        }

        return Mth.clamp(sum / size, 0, getMaxQuality());
    }

    default CompoundTag getLevelingTag(ItemStack stack) {
        return NBTUtils.getCompound(stack, "leveling", new CompoundTag());
    }

    default void setLevelingTag(ItemStack stack, CompoundTag data) {
        NBTUtils.setCompound(stack, "leveling", data);
    }

    default int getPoints(ItemStack stack) {
        return getLevelingTag(stack).getInt("points");
    }

    default void setPoints(ItemStack stack, int amount) {
        CompoundTag tag = getLevelingTag(stack);

        tag.putInt("points", Math.max(0, amount));

        setLevelingTag(stack, tag);
    }

    default void addPoints(ItemStack stack, int amount) {
        setPoints(stack, getPoints(stack) + amount);
    }

    default int getLevel(ItemStack stack) {
        return getLevelingTag(stack).getInt("level");
    }

    default void setLevel(ItemStack stack, int level) {
        CompoundTag tag = getLevelingTag(stack);

        tag.putInt("level", Mth.clamp(level, 0, getLevelingData().getMaxLevel()));

        setLevelingTag(stack, tag);
    }

    default void addLevel(ItemStack stack, int amount) {
        if (amount > 0)
            addPoints(stack, Mth.clamp(amount, 0, getLevelingData().getMaxLevel() - getLevel(stack)));

        setLevel(stack, getLevel(stack) + amount);
    }

    default int getExperience(ItemStack stack) {
        return getLevelingTag(stack).getInt("experience");
    }

    default void setExperience(ItemStack stack, int experience) {
        int level = getLevel(stack);

        if (level >= getLevelingData().getMaxLevel())
            return;

        int requiredExp = getExperienceBetweenLevels(stack, level, level + 1);

        CompoundTag data = getLevelingTag(stack);

        if (experience >= requiredExp) {
            int sumExp = getTotalExperienceForLevel(stack, level) + experience;
            int resultLevel = getLevelFromExperience(stack, sumExp);

            data.putInt("experience", Math.max(0, sumExp - getTotalExperienceForLevel(stack, resultLevel)));

            setLevelingTag(stack, data);
            addPoints(stack, resultLevel - level);
            setLevel(stack, resultLevel);
        } else {
            data.putInt("experience", Mth.clamp(experience, 0, requiredExp));

            setLevelingTag(stack, data);
        }
    }

    default boolean addExperience(ItemStack stack, int amount) {
        return addExperience(null, stack, amount);
    }

    default boolean addExperience(Entity entity, ItemStack stack, int amount) {
        ExperienceAddEvent event = new ExperienceAddEvent(entity instanceof Player ? (Player) entity : null, stack, amount);

        MinecraftForge.EVENT_BUS.post(event);

        if (!event.isCanceled()) {
            setExperience(stack, getExperience(stack) + event.getAmount());

            return true;
        }

        return false;
    }

    default void dropAllocableExperience(Level level, Vec3 pos, ItemStack stack, int amount) {
        dropAllocableExperience(level, pos, stack, amount, 0.75F);
    }

    default void dropAllocableExperience(Level level, Vec3 pos, ItemStack stack, int amount, float priority) {
        dropExperience(level, pos, level.getRandom().nextFloat() <= priority ? stack : ItemStack.EMPTY, amount);
    }

    default void dropExperience(Level level, Vec3 pos, int amount) {
        dropExperience(level, pos, ItemStack.EMPTY, amount);
    }

    default void dropExperience(Level level, Vec3 pos, ItemStack stack, int amount) {
        if (amount <= 0)
            return;

        Random random = level.getRandom();

        int orbs = Math.max(amount / RelicExperienceOrbEntity.getMaxExperience(), random.nextInt(amount) + 1);

        for (int i = 0; i < orbs; i++) {
            RelicExperienceOrbEntity orb = new RelicExperienceOrbEntity(EntityRegistry.RELIC_EXPERIENCE_ORB.get(), level);

            orb.setPos(pos);
            orb.setExperience(amount / orbs);

            if (stack != null && !stack.isEmpty())
                orb.setStack(stack);

            orb.setDeltaMovement(
                    (-1 + 2 * random.nextFloat()) * 0.15F,
                    0.1F + random.nextFloat() * 0.2F,
                    (-1 + 2 * random.nextFloat()) * 0.15F
            );

            level.addFreshEntity(orb);
        }
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

        LevelingData levelingData = getLevelingData();

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
        return getLevel(stack) >= getLevelingData().getMaxLevel();
    }

    default int getExchanges(ItemStack stack) {
        return NBTUtils.getInt(stack, "exchanges", 0);
    }

    default void setExchanges(ItemStack stack, int amount) {
        NBTUtils.setInt(stack, "exchanges", Math.max(0, amount));
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

    default CastPredicate getAbilityCastPredicates(String ability) {
        return getAbilityData(ability).getCastData().getValue();
    }

    default PredicateEntry getAbilityCastPredicate(String ability, String predicate) {
        return getAbilityCastPredicates(ability).getPredicates().get(predicate);
    }

    default boolean testAbilityCastPredicate(Player player, ItemStack stack, String ability, String predicate) {
        return getAbilityCastPredicate(ability, predicate).getPredicate().apply(new PredicateData(player, stack));
    }

    default boolean testAbilityCastPredicates(Player player, ItemStack stack, String ability) {
        CastPredicate predicates = getAbilityCastPredicates(ability);

        for (Map.Entry<String, PredicateEntry> entry : predicates.getPredicates().entrySet()) {
            if (!entry.getValue().getPredicate().apply(new PredicateData(player, stack)))
                return false;
        }

        return true;
    }

    default CompoundTag getAbilitiesTag(ItemStack stack) {
        return stack.getOrCreateTag().getCompound("abilities");
    }

    default void setAbilitiesTag(ItemStack stack, CompoundTag nbt) {
        stack.getOrCreateTag().put("abilities", nbt);
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

        return data.getCompound("temp");
    }

    default void setAbilityTempTag(ItemStack stack, String ability, CompoundTag nbt) {
        CompoundTag data = getAbilityTag(stack, ability);

        data.put("temp", nbt);

        setAbilityTag(stack, ability, data);
    }

    default Map<String, Double> getAbilityInitialValues(ItemStack stack, String ability) {
        CompoundTag abilityTag = getAbilityTag(stack, ability);

        Map<String, Double> result = new HashMap<>();

        if (abilityTag.isEmpty())
            return result;

        CompoundTag statTag = abilityTag.getCompound("stats");

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
            if (getStatData(ability, stat) != null) {
                randomizeStats(stack, ability);

                result = getAbilityInitialValues(stack, ability).get(stat);
            } else
                result = 0D;
        }

        return result;
    }

    default double getAbilityValue(ItemStack stack, String ability, String stat, int points) {
        StatData data = getStatData(ability, stat);

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
        CompoundTag statTag = abilityTag.getCompound("stats");

        statTag.putDouble(stat, value);
        abilityTag.put("stats", statTag);
        data.put(ability, abilityTag);

        setAbilitiesTag(stack, data);
    }

    default void addAbilityValue(ItemStack stack, String ability, String stat, double value) {
        setAbilityValue(stack, ability, stat, getAbilityValue(stack, ability, stat) + value);
    }

    default int getAbilityPoints(ItemStack stack, String ability) {
        return getAbilityTag(stack, ability).getInt("points");
    }

    default void setAbilityPoints(ItemStack stack, String ability, int amount) {
        getAbilityTag(stack, ability).putInt("points", Math.max(0, amount));
    }

    default void addAbilityPoints(ItemStack stack, String ability, int amount) {
        getAbilityTag(stack, ability).putInt("points", Math.max(0, getAbilityPoints(stack, ability) + amount));
    }

    default boolean canUseAbility(ItemStack stack, String ability) {
        return getLevel(stack) >= getAbilityData(ability).getRequiredLevel();
    }

    default void randomizeStat(ItemStack stack, String ability, String stat) {
        StatData entry = getStatData(ability, stat);

        double result = MathUtils.round(MathUtils.randomBetween(new Random(), entry.getInitialValue().getKey(), entry.getInitialValue().getValue()), 5);

        setAbilityValue(stack, ability, stat, result);
    }

    default void randomizeStats(ItemStack stack, String ability) {
        AbilityData entry = getAbilityData(ability);

        for (String stat : entry.getStats().keySet())
            randomizeStat(stack, ability, stat);
    }

    default int getUpgradeRequiredExperience(ItemStack stack, String ability) {
        AbilityData entry = getAbilityData(ability);

        int count = entry.getStats().size();

        if (count == 0)
            return 0;

        return (getAbilityPoints(stack, ability) + 1) * entry.getRequiredPoints() * count * 15;
    }

    default boolean isAbilityMaxLevel(ItemStack stack, String ability) {
        AbilityData entry = getAbilityData(ability);

        return entry.getStats().isEmpty() || getAbilityPoints(stack, ability) >= (entry.getMaxLevel() == -1 ? (getLevelingData().getMaxLevel() / entry.getRequiredPoints()) : entry.getMaxLevel());
    }

    default boolean mayUpgrade(ItemStack stack, String ability) {
        AbilityData entry = getAbilityData(ability);

        return !entry.getStats().isEmpty() && !isAbilityMaxLevel(stack, ability) && getPoints(stack) >= entry.getRequiredPoints() && canUseAbility(stack, ability);
    }

    default boolean mayPlayerUpgrade(Player player, ItemStack stack, String ability) {
        return mayUpgrade(stack, ability) && player.totalExperience >= getUpgradeRequiredExperience(stack, ability);
    }

    default int getRerollRequiredExperience(String ability) {
        AbilityData entry = getAbilityData(ability);

        int count = entry.getStats().size();

        if (count == 0)
            return 0;

        return 100 / count;
    }

    default boolean mayReroll(ItemStack stack, String ability) {
        return !getAbilityData(ability).getStats().isEmpty() && getRerollRequiredExperience(ability) > 0 && canUseAbility(stack, ability);
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
        return !getAbilityData(ability).getStats().isEmpty() && mayReset(stack, ability) && player.totalExperience >= getResetRequiredExperience(stack, ability);
    }

    default int getAbilityCooldownCap(ItemStack stack, String ability) {
        return getAbilityTempTag(stack, ability).getInt("cooldown_cap");
    }

    default void setAbilityCooldownCap(ItemStack stack, String ability, int amount) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putInt("cooldown_cap", amount);

        setAbilityTempTag(stack, ability, data);
    }

    default int getAbilityCooldown(ItemStack stack, String ability) {
        return getAbilityTempTag(stack, ability).getInt("cooldown");
    }

    default void setAbilityCooldown(ItemStack stack, String ability, int amount) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putInt("cooldown", amount);
        data.putInt("cooldown_cap", amount);

        setAbilityTempTag(stack, ability, data);
    }

    default void addAbilityCooldown(ItemStack stack, String ability, int amount) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putInt("cooldown", getAbilityCooldown(stack, ability) + amount);

        setAbilityTempTag(stack, ability, data);
    }

    default void setAbilityTicking(ItemStack stack, String ability, boolean ticking) {
        CompoundTag data = getAbilityTempTag(stack, ability);

        data.putBoolean("ticking", ticking);

        setAbilityTempTag(stack, ability, data);
    }

    default boolean isAbilityTicking(ItemStack stack, String ability) {
        return getAbilityTempTag(stack, ability).getBoolean("ticking");
    }

    default boolean isAbilityOnCooldown(ItemStack stack, String ability) {
        return getAbilityCooldown(stack, ability) > 0;
    }

    default boolean canPlayerUseActiveAbility(Player player, ItemStack stack, String ability) {
        return canUseAbility(stack, ability) && !isAbilityOnCooldown(stack, ability) && testAbilityCastPredicates(player, stack, ability);
    }
}