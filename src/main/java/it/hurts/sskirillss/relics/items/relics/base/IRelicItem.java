package it.hurts.sskirillss.relics.items.relics.base;

import it.hurts.sskirillss.octolib.config.data.ConfigContext;
import it.hurts.sskirillss.octolib.config.data.OctoConfig;
import it.hurts.sskirillss.relics.api.events.leveling.ExperienceAddEvent;
import it.hurts.sskirillss.relics.capability.utils.CapabilityUtils;
import it.hurts.sskirillss.relics.components.*;
import it.hurts.sskirillss.relics.config.ConfigHelper;
import it.hurts.sskirillss.relics.entities.RelicExperienceOrbEntity;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStorage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.RelicContainer;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.capability.CapabilitySyncPacket;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
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
    default RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
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

    default AbilitiesData getAbilitiesData() {
        return getRelicData().getAbilities();
    }

    default AbilityData getAbilityData(String ability) {
        return getAbilitiesData().getAbilities().get(ability);
    }

    default StatData getStatData(String ability, String stat) {
        return getAbilityData(ability).getStats().get(stat);
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

        return item != null && CapabilityUtils.getRelicsCapability(player).getResearchData().getBoolean(BuiltInRegistries.ITEM.getKey(item).getPath() + "_researched");
    }

    default void setItemResearched(Player player, boolean researched) {
        Item item = getItem();

        if (item == null)
            return;

        CapabilityUtils.getRelicsCapability(player).getResearchData().putBoolean(BuiltInRegistries.ITEM.getKey(item).getPath() + "_researched", researched);

        if (!player.level().isClientSide())
            NetworkHandler.sendToClient(new CapabilitySyncPacket(CapabilityUtils.getRelicsCapability(player).serializeNBT(player.registryAccess())), (ServerPlayer) player);
    }

    default int getMaxQuality() {
        return 10;
    }

    default int getStatQuality(ItemStack stack, String ability, String stat) {
        StatData statData = getStatData(ability, stat);

        if (statData == null)
            return 0;

        Function<Double, ? extends Number> format = statData.getFormatValue();

        double initial = format.apply(getStatInitialValue(stack, ability, stat)).doubleValue();

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

    default int getPoints(ItemStack stack) {
        return getLevelingComponent(stack).getPoints();
    }

    default void setPoints(ItemStack stack, int amount) {
        LevelingComponent levelingComponent = getLevelingComponent(stack);

        levelingComponent.setPoints(Math.max(0, amount));

        setLevelingComponent(stack, levelingComponent);
    }

    default void addPoints(ItemStack stack, int amount) {
        setPoints(stack, getPoints(stack) + amount);
    }

    default int getLevel(ItemStack stack) {
        return getLevelingComponent(stack).getLevel();
    }

    default void setLevel(ItemStack stack, int level) {
        LevelingComponent levelingComponent = getLevelingComponent(stack);

        levelingComponent.setLevel(Math.max(0, level));

        setLevelingComponent(stack, levelingComponent);
    }

    default void addLevel(ItemStack stack, int amount) {
        if (amount > 0)
            addPoints(stack, Mth.clamp(amount, 0, getLevelingData().getMaxLevel() - getLevel(stack)));

        setLevel(stack, getLevel(stack) + amount);
    }

    default int getExperience(ItemStack stack) {
        return getLevelingComponent(stack).getExperience();
    }

    default void setExperience(ItemStack stack, int experience) {
        int level = getLevel(stack);

        if (level >= getLevelingData().getMaxLevel())
            return;

        int requiredExp = getExperienceBetweenLevels(stack, level, level + 1);

        LevelingComponent levelingComponent = getLevelingComponent(stack);

        if (experience >= requiredExp) {
            int sumExp = getTotalExperienceForLevel(stack, level) + experience;
            int resultLevel = getLevelFromExperience(stack, sumExp);

            levelingComponent.setExperience(Math.max(0, sumExp - getTotalExperienceForLevel(stack, resultLevel)));

            setLevelingComponent(stack, levelingComponent);
            addPoints(stack, resultLevel - level);
            setLevel(stack, resultLevel);
        } else {
            levelingComponent.setExperience(Mth.clamp(experience, 0, requiredExp));

            setLevelingComponent(stack, levelingComponent);
        }
    }

    default boolean addExperience(ItemStack stack, int amount) {
        return addExperience(null, stack, amount);
    }

    default boolean addExperience(@Nullable LivingEntity entity, ItemStack stack, int amount) {
        ExperienceAddEvent event = new ExperienceAddEvent(entity instanceof LivingEntity ? entity : null, stack, amount);

        NeoForge.EVENT_BUS.post(event);

        if (!event.isCanceled()) {
            setExperience(stack, getExperience(stack) + event.getAmount());

            return true;
        }

        return false;
    }

    default void spreadExperience(@Nullable LivingEntity entity, ItemStack stack, int experience) {
        spreadExperience(entity, stack, experience, 0.25D);
    }

    default void spreadExperience(@Nullable LivingEntity entity, ItemStack stack, int experience, double percentage) {
        boolean isMaxLevel = isMaxLevel(stack);

        int toSpread = isMaxLevel ? experience : (int) Math.ceil(experience * percentage);

        if (!isMaxLevel)
            addExperience(entity, stack, experience);

        if (toSpread <= 0 || entity == null)
            return;

        List<ItemStack> relics = new ArrayList<>();

        for (RelicContainer source : RelicContainer.values())
            relics.addAll(source.gatherRelics().apply(entity).stream().filter(entry -> !isMaxLevel(entry) && !stack.equals(entry)).toList());

        if (relics.isEmpty())
            return;

        ItemStack relicStack = relics.get(entity.level().getRandom().nextInt(relics.size()));

        if (relicStack.getItem() instanceof IRelicItem relic)
            relic.addExperience(entity, relicStack, toSpread);
    }

    default void dropExperience(Level level, Vec3 pos, int amount) {
        if (amount <= 0)
            return;

        RandomSource random = level.getRandom();

        int orbs = Math.max(amount / RelicExperienceOrbEntity.getMaxExperience(), random.nextInt(amount) + 1);

        for (int i = 0; i < orbs; i++) {
            RelicExperienceOrbEntity orb = new RelicExperienceOrbEntity(EntityRegistry.RELIC_EXPERIENCE_ORB.get(), level);

            orb.setPos(pos);
            orb.setExperience(amount / orbs);

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

    default CastData getAbilityCastData(String ability) {
        return getAbilityData(ability).getCastData();
    }

    default boolean testAbilityCastPredicates(Player player, ItemStack stack, String ability) {
        CastData data = getAbilityCastData(ability);

        for (Map.Entry<String, BiFunction<Player, ItemStack, Boolean>> entry : data.getCastPredicates().entrySet()) {
            if (!entry.getValue().apply(player, stack))
                return false;
        }

        return true;
    }

    default DataComponent getDataComponent(ItemStack stack) {
        DataComponent dataComponent = stack.get(DataComponentRegistry.DATA);

        if (dataComponent == null) {
            dataComponent = new DataComponent();

            setDataComponent(stack, dataComponent);
        }

        return dataComponent;
    }

    default void setDataComponent(ItemStack stack, DataComponent component) {
        stack.set(DataComponentRegistry.DATA, component);
    }

    default LevelingComponent getLevelingComponent(ItemStack stack) {
        DataComponent dataComponent = getDataComponent(stack);
        LevelingComponent levelingComponent = dataComponent.getLeveling();

        if (levelingComponent == null) {
            levelingComponent = new LevelingComponent();

            dataComponent.setLeveling(levelingComponent);
        }

        return levelingComponent;
    }

    default void setLevelingComponent(ItemStack stack, LevelingComponent component) {
        DataComponent dataComponent = getDataComponent(stack);

        dataComponent.setLeveling(component);

        setDataComponent(stack, dataComponent);
    }

    default AbilitiesComponent getAbilitiesComponent(ItemStack stack) {
        DataComponent dataComponent = getDataComponent(stack);
        AbilitiesComponent abilitiesComponent = dataComponent.getAbilities();

        if (abilitiesComponent == null) {
            abilitiesComponent = new AbilitiesComponent();

            dataComponent.setAbilities(abilitiesComponent);
        }

        return abilitiesComponent;
    }

    default void setAbilitiesComponent(ItemStack stack, AbilitiesComponent component) {
        DataComponent dataComponent = getDataComponent(stack);

        dataComponent.setAbilities(component);

        setDataComponent(stack, dataComponent);
    }

    @Nullable
    default AbilityComponent getAbilityComponent(ItemStack stack, String ability) {
        AbilitiesComponent abilitiesComponent = getAbilitiesComponent(stack);

        Map<String, AbilityComponent> abilities = abilitiesComponent.getAbilities();

        AbilityComponent abilityComponent = abilities.get(ability);

        if (abilityComponent != null)
            return abilityComponent;
        else if (getAbilityData(ability) != null) {
            abilityComponent = new AbilityComponent();

            abilities.put(ability, abilityComponent);
            abilitiesComponent.setAbilities(abilities);

            return abilityComponent;
        } else
            return null;
    }

    default void setAbilityComponent(ItemStack stack, String ability, AbilityComponent component) {
        AbilitiesComponent abilitiesComponent = getAbilitiesComponent(stack);

        abilitiesComponent.getAbilities().put(ability, component);

        setAbilitiesComponent(stack, abilitiesComponent);
    }

    @Nullable
    default StatComponent getStatComponent(ItemStack stack, String ability, String stat) {
        AbilityComponent abilityComponent = getAbilityComponent(stack, ability);

        Map<String, StatComponent> stats = abilityComponent.getStats();

        StatComponent statComponent = stats.get(stat);

        StatData statData = getStatData(ability, stat);

        if (statComponent != null)
            return statComponent;
        else if (statData != null) {
            statComponent = new StatComponent();

            statComponent.setInitialValue(MathUtils.round(MathUtils.randomBetween(new Random(), statData.getInitialValue().getKey(), statData.getInitialValue().getValue()), 5));

            stats.put(stat, statComponent);
            abilityComponent.setStats(stats);

            return statComponent;
        } else
            return null;
    }

    default void setStatComponent(ItemStack stack, String ability, StatComponent component) {
        AbilityComponent abilityComponent = getAbilityComponent(stack, ability);

        abilityComponent.getStats().put(ability, component);

        setAbilityComponent(stack, ability, abilityComponent);
    }

    default double getStatInitialValue(ItemStack stack, String ability, String stat) {
        return getStatComponent(stack, ability, stat).getInitialValue();
    }

    default void setStatInitialValue(ItemStack stack, String ability, String stat, double value) {
        StatComponent statComponent = getStatComponent(stack, ability, stat);

        statComponent.setInitialValue(value);

        setStatComponent(stack, ability, statComponent);
    }

    default void addStatInitialValue(ItemStack stack, String ability, String stat, double value) {
        setStatInitialValue(stack, ability, stat, getStatInitialValue(stack, ability, stat) + value);
    }

    default int getAbilityPoints(ItemStack stack, String ability) {
        return getAbilityComponent(stack, ability).getPoints();
    }

    default void setAbilityPoints(ItemStack stack, String ability, int points) {
        AbilityComponent abilityComponent = getAbilityComponent(stack, ability);

        abilityComponent.setPoints(points);

        setAbilityComponent(stack, ability, abilityComponent);
    }

    default void addAbilityPoints(ItemStack stack, String ability, int points) {
        setAbilityPoints(stack, ability, getAbilityPoints(stack, ability) + points);
    }

    @Deprecated(forRemoval = true)
    default AbilityComponent randomizeAbility(ItemStack stack, String ability) {
        for (String stat : getAbilityData(ability).getStats().keySet())
            randomizeStat(stack, ability, stat);

        return getAbilityComponent(stack, ability);
    }

    @Deprecated(forRemoval = true)
    default StatComponent randomizeStat(ItemStack stack, String ability, String stat) {
        StatData entry = getStatData(ability, stat);

        double result = MathUtils.round(MathUtils.randomBetween(new Random(), entry.getInitialValue().getKey(), entry.getInitialValue().getValue()), 5);

        setStatInitialValue(stack, ability, stat, result);

        return getStatComponent(stack, ability, stat);
    }

    @Deprecated(forRemoval = true)
    default void randomizeStats(ItemStack stack, String ability) {
        AbilityData entry = getAbilityData(ability);

        for (String stat : entry.getStats().keySet())
            randomizeStat(stack, ability, stat);
    }

    default double getStatValue(ItemStack stack, String ability, String stat, int points) {
        StatData data = getStatData(ability, stat);

        double result = 0D;

        if (data == null)
            return result;

        double current = getStatInitialValue(stack, ability, stat);
        double step = data.getUpgradeModifier().getValue();

        switch (data.getUpgradeModifier().getKey()) {
            case ADD -> result = current + (points * step);
            case MULTIPLY_BASE -> result = current + ((current * step) * points);
            case MULTIPLY_TOTAL -> result = current * Math.pow(step + 1, points);
        }

        Pair<Double, Double> threshold = data.getThresholdValue();

        return MathUtils.round(Mth.clamp(result, threshold.getKey(), threshold.getValue()), 5);
    }

    default double getStatValue(ItemStack stack, String ability, String stat) {
        return getStatValue(stack, ability, stat, getAbilityPoints(stack, ability));
    }

    default boolean canUseAbility(ItemStack stack, String ability) {
        return getLevel(stack) >= getAbilityData(ability).getRequiredLevel();
    }

    default boolean canSeeAbility(Player player, ItemStack stack, String ability) {
        for (BiFunction<Player, ItemStack, Boolean> predicate : getAbilityCastData(ability).getVisibilityPredicates()) {
            if (!predicate.apply(player, stack))
                return false;
        }

        return true;
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
        return getAbilityComponent(stack, ability).getCooldownCap();
    }

    default void setAbilityCooldownCap(ItemStack stack, String ability, int amount) {
        AbilityComponent abilityComponent = getAbilityComponent(stack, ability);

        abilityComponent.setCooldownCap(amount);

        setAbilityComponent(stack, ability, abilityComponent);
    }

    default int getAbilityCooldown(ItemStack stack, String ability) {
        return getAbilityComponent(stack, ability).getCooldown();
    }

    default void setAbilityCooldown(ItemStack stack, String ability, int amount) {
        AbilityComponent abilityComponent = getAbilityComponent(stack, ability);

        abilityComponent.setCooldownCap(amount);
        abilityComponent.setCooldown(amount);

        setAbilityComponent(stack, ability, abilityComponent);
    }

    default void addAbilityCooldown(ItemStack stack, String ability, int amount) {
        AbilityComponent abilityComponent = getAbilityComponent(stack, ability);

        abilityComponent.setCooldown(getAbilityCooldown(stack, ability) + amount);

        setAbilityComponent(stack, ability, abilityComponent);
    }

    default void setAbilityTicking(ItemStack stack, String ability, boolean ticking) {
        AbilityComponent abilityComponent = getAbilityComponent(stack, ability);

        abilityComponent.setTicking(ticking);

        setAbilityComponent(stack, ability, abilityComponent);
    }

    default boolean isAbilityTicking(ItemStack stack, String ability) {
        return getAbilityComponent(stack, ability).isTicking();
    }

    default boolean isAbilityOnCooldown(ItemStack stack, String ability) {
        return getAbilityCooldown(stack, ability) > 0;
    }

    default boolean canPlayerUseActiveAbility(Player player, ItemStack stack, String ability) {
        return canUseAbility(stack, ability) && !isAbilityOnCooldown(stack, ability) && testAbilityCastPredicates(player, stack, ability);
    }
}