package it.hurts.sskirillss.relics.items.relics.base;

import it.hurts.sskirillss.relics.api.events.leveling.ExperienceAddEvent;
import it.hurts.sskirillss.relics.capability.utils.CapabilityUtils;
import it.hurts.sskirillss.relics.components.*;
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
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface IRelicItem {
    @Nullable
    default Item getItem() {
        return this instanceof Item item ? item : null;
    }

    RelicData constructDefaultRelicData();

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

        if (initial == min)
            return 0;

        if (initial == max)
            return getMaxQuality();

        return Mth.clamp((int) Math.round((initial - min) / ((max - min) / getMaxQuality())), 1, getMaxQuality() - 1);
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

        double sum = 0;

        for (String stat : stats.keySet())
            sum += getStatQuality(stack, ability, stat);

        sum = (int) Math.floor(sum / stats.size());

        int min = 0;
        int max = getMaxQuality();

        if (sum == min)
            return min;

        if (sum == max)
            return max;

        return (int) Mth.clamp(sum, min + 1, max - 1);
    }

    default int getRelicQuality(ItemStack stack) {
        Map<String, AbilityData> abilities = getRelicData().getAbilities().getAbilities();

        if (abilities.isEmpty())
            return 0;

        int size = abilities.size();
        double sum = 0;

        for (Map.Entry<String, AbilityData> entry : abilities.entrySet()) {
            if (entry.getValue().getMaxLevel() == 0) {
                --size;

                continue;
            }

            sum += getAbilityQuality(stack, entry.getKey());
        }

        sum = (int) Math.floor(sum / size);

        int min = 0;
        int max = getMaxQuality();

        if (sum == min)
            return min;

        if (sum == max)
            return max;

        return (int) Mth.clamp(sum, min + 1, max - 1);
    }

    default int getPoints(ItemStack stack) {
        return getLevelingComponent(stack).points();
    }

    default void setPoints(ItemStack stack, int amount) {
        setLevelingComponent(stack, getLevelingComponent(stack).toBuilder().points(Math.max(0, amount)).build());
    }

    default void addPoints(ItemStack stack, int amount) {
        setPoints(stack, getPoints(stack) + amount);
    }

    default int getLevel(ItemStack stack) {
        return getLevelingComponent(stack).level();
    }

    default void setLevel(ItemStack stack, int level) {
        setLevelingComponent(stack, getLevelingComponent(stack).toBuilder().level(Math.max(0, level)).build());
    }

    default void addLevel(ItemStack stack, int amount) {
        if (amount > 0)
            addPoints(stack, Mth.clamp(amount, 0, getLevelingData().getMaxLevel() - getLevel(stack)));

        setLevel(stack, getLevel(stack) + amount);
    }

    default int getMaxLuck() {
        return 100;
    }

    default double getLuckModifier() {
        return 1.5D;
    }

    default int getLuck(ItemStack stack) {
        return getLevelingComponent(stack).luck();
    }

    default void setLuck(ItemStack stack, int amount) {
        setLevelingComponent(stack, getLevelingComponent(stack).toBuilder().luck(Mth.clamp(amount, 0, getMaxLuck())).build());
    }

    default void addLuck(ItemStack stack, int amount) {
        setLuck(stack, getLuck(stack) + amount);
    }

    default int getExperience(ItemStack stack) {
        return getLevelingComponent(stack).experience();
    }

    default void setExperience(ItemStack stack, int experience) {
        setLevelingComponent(stack, getLevelingComponent(stack).toBuilder()
                .experience(Math.clamp(experience, 0, getTotalExperienceForLevel(getLevel(stack) + 1)))
                .build());
    }

    default boolean addExperience(ItemStack stack, int amount) {
        return addExperience(null, stack, amount);
    }

    default boolean addExperience(@Nullable LivingEntity entity, ItemStack stack, int amount) {
        ExperienceAddEvent event = new ExperienceAddEvent(entity instanceof LivingEntity ? entity : null, stack, amount);

        NeoForge.EVENT_BUS.post(event);

        if (!event.isCanceled()) {
            int currentExperience = getExperience(stack);
            int currentLevel = getLevel(stack);

            int toAdd = event.getAmount();

            int resultLevel = currentLevel;
            int resultExperience = 0;

            while (toAdd > 0) {
                if (resultLevel >= getLevelingData().getMaxLevel())
                    break;

                int requiredExperience = getExperienceBetweenLevels(resultLevel, resultLevel + 1);

                int diff = requiredExperience - currentExperience;

                if (toAdd >= diff) {
                    toAdd -= diff;

                    resultLevel++;

                    currentExperience = 0;
                } else {
                    resultExperience = currentExperience + toAdd;

                    break;
                }
            }

            setExperience(stack, resultExperience);

            if (currentLevel != resultLevel) {
                setLevel(stack, resultLevel);

                addPoints(stack, resultLevel - currentLevel);
            }

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

        return getExperienceBetweenLevels(currentLevel, level) - getExperience(stack);
    }

    default int getExperienceBetweenLevels(int from, int to) {
        return getTotalExperienceForLevel(to) - getTotalExperienceForLevel(from);
    }

    default int getTotalExperienceForLevel(int level) {
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

            amount = getTotalExperienceForLevel(result);
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
        return stack.getOrDefault(DataComponentRegistry.DATA, DataComponent.EMPTY);
    }

    default void setDataComponent(ItemStack stack, DataComponent component) {
        stack.set(DataComponentRegistry.DATA, component);
    }

    default LevelingComponent getLevelingComponent(ItemStack stack) {
        return getDataComponent(stack).leveling();
    }

    default void setLevelingComponent(ItemStack stack, LevelingComponent component) {
        setDataComponent(stack, getDataComponent(stack).toBuilder().leveling(component).build());
    }

    default AbilitiesComponent getAbilitiesComponent(ItemStack stack) {
        return getDataComponent(stack).abilities();
    }

    default void setAbilitiesComponent(ItemStack stack, AbilitiesComponent component) {
        setDataComponent(stack, getDataComponent(stack).toBuilder().abilities(component).build());
    }

    default AbilityComponent getAbilityComponent(ItemStack stack, String ability) {
        AbilitiesComponent abilitiesComponent = getAbilitiesComponent(stack);

        @Nullable AbilityComponent abilityComponent = abilitiesComponent.abilities().get(ability);

        AbilityData abilityData = getAbilityData(ability);

        if (abilityComponent != null)
            return abilityComponent;
        else if (abilityData != null) {
            AbilityComponent.AbilityComponentBuilder builder = AbilityComponent.EMPTY.toBuilder();

            if (abilityData.getCastData().getType() == CastType.TOGGLEABLE)
                builder.ticking(true);

            abilityComponent = builder.build();

            setAbilitiesComponent(stack, abilitiesComponent.toBuilder()
                    .ability(ability, abilityComponent)
                    .build());

            return abilityComponent;
        } else
            return null;
    }

    default void setAbilityComponent(ItemStack stack, String ability, AbilityComponent component) {
        setAbilitiesComponent(stack, getAbilitiesComponent(stack).toBuilder().ability(ability, component).build());
    }

    default StatComponent getStatComponent(ItemStack stack, String ability, String stat) {
        AbilityComponent abilityComponent = getAbilityComponent(stack, ability);

        @Nullable StatComponent statComponent = abilityComponent.stats().get(stat);

        StatData statData = getStatData(ability, stat);

        if (statComponent != null)
            return statComponent;
        else if (statData != null) {
            statComponent = StatComponent.EMPTY.toBuilder()
                    .initialValue(MathUtils.round(MathUtils.randomBetween(new Random(), statData.getInitialValue().getKey(), statData.getInitialValue().getValue()), 5))
                    .build();

            setAbilityComponent(stack, ability, abilityComponent.toBuilder()
                    .stat(stat, statComponent)
                    .build());

            return statComponent;
        } else
            return null;
    }

    default void setStatComponent(ItemStack stack, String ability, String stat, StatComponent component) {
        setAbilityComponent(stack, ability, getAbilityComponent(stack, ability).toBuilder()
                .stat(stat, component)
                .build());
    }

    default double getStatInitialValue(ItemStack stack, String ability, String stat) {
        return getStatComponent(stack, ability, stat).initialValue();
    }

    default void setStatInitialValue(ItemStack stack, String ability, String stat, double value) {
        setStatComponent(stack, ability, stat, getStatComponent(stack, ability, stat).toBuilder()
                .initialValue(value)
                .build());
    }

    default void addStatInitialValue(ItemStack stack, String ability, String stat, double value) {
        setStatInitialValue(stack, ability, stat, getStatInitialValue(stack, ability, stat) + value);
    }

    default int getAbilityPoints(ItemStack stack, String ability) {
        return getAbilityComponent(stack, ability).points();
    }

    default void setAbilityPoints(ItemStack stack, String ability, int points) {
        setAbilityComponent(stack, ability, getAbilityComponent(stack, ability).toBuilder()
                .points(points)
                .build());
    }

    default void addAbilityPoints(ItemStack stack, String ability, int points) {
        setAbilityPoints(stack, ability, getAbilityPoints(stack, ability) + points);
    }

    default AbilityComponent randomizeAbility(ItemStack stack, String ability, int luck) {
        Map<String, StatData> stats = getAbilityData(ability).getStats();

        Random random = new Random();

        int maxQuality = getMaxQuality();
        int maxLuck = getMaxLuck();

        // Random value in the [-1, 1] range
        double randomValue = (random.nextDouble() * 2D) - 1D;

        // Luck effect modifier. Lower value = lower chance to get 5 stars
        double modifier = 1.25D;

        // Bias based on luck (ranging from -0.5 to 0.5), multiplied by the modifier
        double bias = ((luck - (maxLuck / 2D)) / maxLuck) * modifier;

        // Apply the bias to randomValue and limit the result within the range [-1, 1]
        double biasedValue = Math.tanh(randomValue + bias);

        // Convert the biased result to the range [0, maxQuality]
        double weightedRandom = Math.floor((biasedValue + 1D) / 2D * (maxQuality + 1D));

        // Clamping the value to avoid overflow
        double targetQuality = Mth.clamp(weightedRandom, 0, maxQuality);

        double sumQuality = 0;

        Map<String, Double> generatedQualities = new HashMap<>();

        for (String stat : stats.keySet()) {
            double randomQuality = MathUtils.randomBetween(random, 0, getMaxQuality());

            generatedQualities.put(stat, randomQuality);

            sumQuality += randomQuality;
        }

        double currentAverageQuality = sumQuality / stats.size();

        while (Math.abs(currentAverageQuality - targetQuality) > 0.01) {
            if (currentAverageQuality < targetQuality) {
                String minStat = generatedQualities.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();

                double increment = Math.min((targetQuality - currentAverageQuality) * stats.size(), getMaxQuality() - generatedQualities.get(minStat));

                generatedQualities.put(minStat, generatedQualities.get(minStat) + increment);
            } else if (currentAverageQuality > targetQuality) {
                String maxStat = generatedQualities.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();

                double decrement = Math.min((currentAverageQuality - targetQuality) * stats.size(), generatedQualities.get(maxStat));

                generatedQualities.put(maxStat, generatedQualities.get(maxStat) - decrement);
            }

            sumQuality = generatedQualities.values().stream().mapToDouble(Double::doubleValue).sum();

            currentAverageQuality = sumQuality / stats.size();
        }

        for (Map.Entry<String, Double> entry : generatedQualities.entrySet())
            randomizeStat(stack, ability, entry.getKey(), (int) Math.round(entry.getValue()));

        return getAbilityComponent(stack, ability);
    }

    default StatComponent randomizeStat(ItemStack stack, String ability, String stat, int quality) {
        StatData entry = getStatData(ability, stat);

        double minValue = entry.getInitialValue().getKey();
        double maxValue = entry.getInitialValue().getValue();

        double diff = maxValue - minValue;

        double result = minValue + (diff * ((double) quality / getMaxQuality()));

        setStatInitialValue(stack, ability, stat, result);

        return getStatComponent(stack, ability, stat);
    }

    default StatComponent randomizeStat(ItemStack stack, String ability, String stat) {
        return randomizeStat(stack, ability, stat, new Random().nextInt(getMaxQuality() + 1));
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

    default boolean isAbilityMaxLevel(ItemStack stack, String ability) {
        AbilityData entry = getAbilityData(ability);

        return entry.getStats().isEmpty() || getAbilityPoints(stack, ability) >= (entry.getMaxLevel() == -1 ? (getLevelingData().getMaxLevel() / entry.getRequiredPoints()) : entry.getMaxLevel());
    }

    default int getUpgradeRequiredLevel(ItemStack stack, String ability) {
        return (getAbilityPoints(stack, ability) * 2) + 5;
    }

    default boolean mayUpgrade(ItemStack stack, String ability) {
        AbilityData entry = getAbilityData(ability);

        return !entry.getStats().isEmpty() && !isAbilityMaxLevel(stack, ability) && getPoints(stack) >= entry.getRequiredPoints() && canUseAbility(stack, ability);
    }

    default boolean mayPlayerUpgrade(Player player, ItemStack stack, String ability) {
        return mayUpgrade(stack, ability) && player.experienceLevel >= getUpgradeRequiredLevel(stack, ability);
    }

    default int getRerollRequiredLevel(ItemStack stack, String ability) {
        return (int) Math.floor(getLuck(stack) / 25D) + 1;
    }

    default boolean mayReroll(ItemStack stack, String ability) {
        return !getAbilityData(ability).getStats().isEmpty() && canUseAbility(stack, ability);
    }

    default boolean mayPlayerReroll(Player player, ItemStack stack, String ability) {
        return mayReroll(stack, ability) && player.experienceLevel >= getRerollRequiredLevel(stack, ability);
    }

    default int getResetRequiredLevel(ItemStack stack, String ability) {
        return getAbilityPoints(stack, ability) * 5;
    }

    default boolean mayReset(ItemStack stack, String ability) {
        return getAbilityPoints(stack, ability) > 0 && canUseAbility(stack, ability);
    }

    default boolean mayPlayerReset(Player player, ItemStack stack, String ability) {
        return !getAbilityData(ability).getStats().isEmpty() && mayReset(stack, ability) && player.experienceLevel >= getResetRequiredLevel(stack, ability);
    }

    default int getAbilityCooldownCap(ItemStack stack, String ability) {
        return getAbilityComponent(stack, ability).cooldownCap();
    }

    default void setAbilityCooldownCap(ItemStack stack, String ability, int amount) {
        setAbilityComponent(stack, ability, getAbilityComponent(stack, ability).toBuilder()
                .cooldownCap(amount)
                .build());
    }

    default int getAbilityCooldown(ItemStack stack, String ability) {
        return getAbilityComponent(stack, ability).cooldown();
    }

    default void setAbilityCooldown(ItemStack stack, String ability, int amount) {
        setAbilityComponent(stack, ability, getAbilityComponent(stack, ability).toBuilder()
                .cooldownCap(amount)
                .cooldown(amount)
                .build());
    }

    default void addAbilityCooldown(ItemStack stack, String ability, int amount) {
        setAbilityComponent(stack, ability, getAbilityComponent(stack, ability).toBuilder()
                .cooldown(getAbilityCooldown(stack, ability) + amount)
                .build());
    }

    default void setAbilityTicking(ItemStack stack, String ability, boolean ticking) {
        setAbilityComponent(stack, ability, getAbilityComponent(stack, ability).toBuilder()
                .ticking(ticking)
                .build());
    }

    default boolean isAbilityTicking(ItemStack stack, String ability) {
        return getAbilityComponent(stack, ability).ticking();
    }

    default boolean isAbilityOnCooldown(ItemStack stack, String ability) {
        return getAbilityCooldown(stack, ability) > 0;
    }

    default boolean canPlayerUseActiveAbility(Player player, ItemStack stack, String ability) {
        return canUseAbility(stack, ability) && !isAbilityOnCooldown(stack, ability) && testAbilityCastPredicates(player, stack, ability);
    }
}