package it.hurts.sskirillss.relics.items.relics.base;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.hurts.sskirillss.relics.api.events.leveling.ExperienceAddEvent;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.ResearchUtils;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;

public abstract class RelicItem extends Item implements ICurioItem {
    public RelicItem(Item.Properties properties) {
        super(properties);
    }

    public RelicItem() {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .rarity(Rarity.RARE)
                .stacksTo(1));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();

        if (DurabilityUtils.isBroken(stack))
            return modifiers;

        RelicAttributeModifier attributes = getAttributeModifiers(stack);
        RelicSlotModifier slots = getSlotModifiers(stack);

        if (attributes != null)
            attributes.getAttributes().forEach(attribute ->
                    modifiers.put(attribute.getAttribute(), new AttributeModifier(uuid,
                            ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + "_" + ForgeRegistries.ATTRIBUTES.getKey(attribute.getAttribute()).getPath(),
                            attribute.getMultiplier(), attribute.getOperation())));

        if (slots != null)
            slots.getModifiers().forEach(slot ->
                    CuriosApi.getCuriosHelper().addSlotModifier(modifiers, slot.getLeft(), uuid, slot.getRight(), AttributeModifier.Operation.ADDITION));

        return modifiers;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!DurabilityUtils.isBroken(stack)) {
            Vec3 pos = entity.position();
            RandomSource random = entity.getCommandSenderWorld().getRandom();

            if (getStyle(stack) != null) {
                String hex = getStyle(stack).getParticles();

                Color color = hex == null || hex.isEmpty() ? new Color(stack.getRarity().color.getColor()) : Color.decode(hex);

                entity.getCommandSenderWorld().addParticle(new CircleTintData(color, random.nextFloat() * 0.025F + 0.04F, 25, 0.97F, true),
                        pos.x() + MathUtils.randomFloat(random) * 0.25F, pos.y() + 0.1F,
                        pos.z() + MathUtils.randomFloat(random) * 0.25F, 0, random.nextFloat() * 0.05D, 0);
            }
        }

        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public boolean showAttributesTooltip(String identifier, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity, ItemStack stack) {
        return !DurabilityUtils.isBroken(stack);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return !DurabilityUtils.isBroken(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return switch (stack.getRarity()) {
            case COMMON -> 100;
            case UNCOMMON -> 150;
            case RARE -> 200;
            case EPIC -> 250;
        };
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return stack.getMaxDamage() > 0;
    }

    // Mojank moment

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F - (float) stack.getDamageValue() * 13.0F / (float) getMaxDamage(stack));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
//        Triple<String, String, String> color = getStyle(stack).getDurability();
//
//        if (color == null)
        return Mth.hsvToRgb(Math.max(0F, ((float) getMaxDamage(stack) - (float) stack.getDamageValue()) / (float) getMaxDamage(stack)) / 3F, 1F, 1F);
//
//        float percentage = stack.getDamageValue() * 100F / getMaxDamage(stack);
//
//        return Color.decode(percentage < 33.3F ? color.getLeft() : percentage < 66.6F ? color.getMiddle() : color.getRight()).getRGB();
    }

    public RelicStyleData getStyle(ItemStack stack) {
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        if (level == null || !level.isClientSide())
            return;

        LocalPlayer player = Minecraft.getInstance().player;

        tooltip.add(Component.literal(" "));

        if (ResearchUtils.isItemResearched(player, stack)) {
            if (Screen.hasShiftDown()) {
                RelicData relicData = getRelicData();

                if (relicData == null)
                    return;

                RelicAbilityData abilityData = relicData.getAbilityData();

                if (abilityData == null)
                    return;

                Map<String, RelicAbilityEntry> abilities = abilityData.getAbilities();

                tooltip.add(Component.literal("▶ ").withStyle(ChatFormatting.DARK_GREEN)
                        .append(Component.translatable("tooltip.relics.relic.tooltip.abilities").withStyle(ChatFormatting.GREEN)));

                for (Map.Entry<String, RelicAbilityEntry> entry : abilities.entrySet()) {
                    String item = ForgeRegistries.ITEMS.getKey(this).getPath();
                    String name = entry.getKey();

                    if (!canUseAbility(stack, name))
                        continue;

                    tooltip.add(Component.literal("   ◆ ").withStyle(ChatFormatting.GREEN)
                            .append(Component.translatable("tooltip.relics." + item + ".ability." + name).withStyle(ChatFormatting.YELLOW))
                            .append(Component.literal(" - ").withStyle(ChatFormatting.WHITE))
                            .append(Component.translatable("tooltip.relics." + item + ".ability." + name + ".description").withStyle(ChatFormatting.GRAY)));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.relics.relic.tooltip.shift").withStyle(ChatFormatting.GRAY));
            }
        } else
            tooltip.add(Component.translatable("tooltip.relics.relic.tooltip.table").withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.literal(" "));
    }

    public void castActiveAbility(ItemStack stack, Player player, String ability) {

    }

    public void tickActiveAbilitySelection(ItemStack stack, Player player, String ability) {

    }

    public RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
        return null;
    }

    public RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return null;
    }

    @Nullable
    public RelicData getRelicData() {
        return null;
    }

    /*
    =================================================
                          Base
    =================================================
    */

    public static RelicData getRelicData(ItemStack stack) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return null;

        return relic.getRelicData();
    }

    /*
    =================================================
                        Abilities
    =================================================
    */

    public static final String TAG_ABILITIES = "abilities";
    public static final String TAG_STATS = "stats";

    @Nullable
    public static RelicAbilityData getAbilityData(ItemStack stack) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return null;

        return getRelicData(stack).getAbilityData();
    }

    @Nullable
    public static RelicAbilityData getAbilityData(RelicItem relic) {
        return relic.getRelicData().getAbilityData();
    }

    @Nullable
    public static RelicAbilityEntry getAbilityEntryData(ItemStack stack, String ability) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return null;

        return getAbilityEntryData(relic, ability);
    }

    @Nullable
    public static RelicAbilityEntry getAbilityEntryData(RelicItem relic, String ability) {
        return getAbilityData(relic).getAbilities().get(ability);
    }

    @Nullable
    public static RelicAbilityStat getAbilityStat(ItemStack stack, String ability, String stat) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return null;

        return getAbilityStat(relic, ability, stat);
    }

    @Nullable
    public static RelicAbilityStat getAbilityStat(RelicItem relic, String ability, String stat) {
        return getAbilityEntryData(relic, ability).getStats().get(stat);
    }

    public static CompoundTag getAbilitiesTag(ItemStack stack) {
        return stack.getOrCreateTag().getCompound(TAG_ABILITIES);
    }

    public static CompoundTag getAbilityTag(ItemStack stack, String ability) {
        CompoundTag data = getAbilitiesTag(stack);

        if (data.isEmpty())
            return new CompoundTag();

        return data.getCompound(ability);
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
        return getAbilityInitialValues(stack, ability).getOrDefault(stat, 0D);
    }

    public static double getAbilityValue(ItemStack stack, String ability, String stat, int points) {
        RelicAbilityStat data = getAbilityStat(stack, ability, stat);

        double result = 0D;

        if (data == null)
            return result;

        double current = getAbilityInitialValue(stack, ability, stat);
        double step = data.getUpgradeModifier().value();

        switch (data.getUpgradeModifier().first()) {
            case ADD -> result = current + (points * step);
            case MULTIPLY_BASE -> result = current + ((current * step) * points);
            case MULTIPLY_TOTAL -> result = current * Math.pow(step + 1, points);
        }

        Pair<Double, Double> threshold = data.getThresholdValue();

        return threshold == null ? MathUtils.round(result, 5)
                : MathUtils.round(Math.max(threshold.first(), Math.min(threshold.second(), result)), 5);
    }

    public static double getAbilityValue(ItemStack stack, String ability, String stat) {
        return getAbilityValue(stack, ability, stat, getAbilityPoints(stack, ability));
    }

    public static void setAbilityValue(ItemStack stack, String ability, String stat, double value) {
        CompoundTag data = getAbilitiesTag(stack);
        CompoundTag abilityTag = getAbilityTag(stack, ability);
        CompoundTag statTag = abilityTag.getCompound(TAG_STATS);

        statTag.putDouble(stat, value);
        abilityTag.put(TAG_STATS, statTag);
        data.put(ability, abilityTag);

        setAbilitiesData(stack, data);
    }

    public static int getAbilityPoints(ItemStack stack, String ability) {
        CompoundTag tag = getAbilityTag(stack, ability);

        if (tag.isEmpty())
            return 0;

        return tag.getInt(TAG_POINTS);
    }

    public static void setAbilityPoints(ItemStack stack, String ability, int amount) {
        getAbilityTag(stack, ability).putInt(TAG_POINTS, amount);
    }

    public static void addAbilityPoints(ItemStack stack, String ability, int amount) {
        getAbilityTag(stack, ability).putInt(TAG_POINTS, Math.max(0, getAbilityPoints(stack, ability) + amount));
    }

    public static void setAbilitiesData(ItemStack stack, CompoundTag nbt) {
        stack.getOrCreateTag().put(TAG_ABILITIES, nbt);
    }

    public static boolean canUseAbility(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getAbilityEntryData(stack, ability);

        return entry != null && getLevel(stack) >= entry.getRequiredLevel();
    }

    public static boolean randomizeStats(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getAbilityEntryData(stack, ability);

        if (entry == null)
            return false;

        for (Map.Entry<String, RelicAbilityStat> stats : entry.stats.entrySet()) {
            RelicAbilityStat stat = stats.getValue();

            double result = MathUtils.round(MathUtils.randomBetween(new Random(), stat.getInitialValue().first(), stat.getInitialValue().second()), 5);

            setAbilityValue(stack, ability, stats.getKey(), result);
        }

        return true;
    }

    public static int getUpgradeRequiredExperience(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getAbilityEntryData(stack, ability);

        if (entry == null)
            return 0;

        int count = entry.getStats().size();

        if (count == 0)
            return 0;

        return (RelicItem.getAbilityPoints(stack, ability) + 1) * entry.getRequiredPoints() * count * 15;
    }

    public static boolean isAbilityMaxLevel(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getAbilityEntryData(stack, ability);

        if (entry == null)
            return false;

        return entry.getStats().size() == 0 || RelicItem.getAbilityPoints(stack, ability) >= (entry.getMaxLevel() == -1 ? (getLevelingData(stack).getMaxLevel() / entry.getRequiredPoints()) : entry.getMaxLevel());
    }

    public static boolean mayUpgrade(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getAbilityEntryData(stack, ability);

        if (entry == null)
            return false;

        return entry.getStats().size() > 0 && !isAbilityMaxLevel(stack, ability) && RelicItem.getPoints(stack) >= entry.getRequiredPoints() && canUseAbility(stack, ability);
    }

    public static boolean mayPlayerUpgrade(Player player, ItemStack stack, String ability) {
        return mayUpgrade(stack, ability) && player.totalExperience >= getUpgradeRequiredExperience(stack, ability);
    }

    public static int getRerollRequiredExperience(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getAbilityEntryData(stack, ability);

        int count = entry.getStats().size();

        if (count == 0)
            return 0;

        return 100 / count;
    }

    public static boolean mayReroll(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getAbilityEntryData(stack, ability);

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
        RelicAbilityEntry entry = getAbilityEntryData(stack, ability);

        if (entry == null)
            return false;

        return entry.getStats().size() > 0 && mayReset(stack, ability) && player.totalExperience >= getResetRequiredExperience(stack, ability);
    }

    /*
    =================================================
                        Leveling
    =================================================
    */

    public static final String TAG_EXPERIENCE = "experience";
    public static final String TAG_LEVELING = "leveling";
    public static final String TAG_POINTS = "points";
    public static final String TAG_LEVEL = "level";

    @Nullable
    public static RelicLevelingData getLevelingData(ItemStack stack) {
        return getRelicData(stack).getLevelingData();
    }

    @Nullable
    public static RelicLevelingData getLevelingData(RelicItem relic) {
        return relic.getRelicData().getLevelingData();
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

    public static void setPoints(ItemStack stack, int level) {
        CompoundTag tag = getLevelingTag(stack);

        tag.putInt(TAG_POINTS, level);

        setLevelingTag(stack, tag);
    }

    public static void addPoints(ItemStack stack, int amount) {
        setPoints(stack, getPoints(stack) + amount);
    }

    public static int getLevel(ItemStack stack) {
        return getLevelingTag(stack).getInt(TAG_LEVEL);
    }

    public static void setLevel(ItemStack stack, int level) {
        RelicLevelingData levelingData = getLevelingData(stack);

        if (levelingData == null || level > levelingData.getMaxLevel())
            return;

        CompoundTag tag = getLevelingTag(stack);

        tag.putInt(TAG_LEVEL, Math.min(((RelicItem) stack.getItem()).getRelicData().getLevelingData().getMaxLevel(), level));

        setLevelingTag(stack, tag);
    }

    public static void addLevel(ItemStack stack, int amount) {
        addPoints(stack, amount);

        setLevel(stack, getLevel(stack) + amount);
    }

    public static int getExperience(ItemStack stack) {
        return getLevelingTag(stack).getInt(TAG_EXPERIENCE);
    }

    public static void setExperience(ItemStack stack, int experience) {
        int level = getLevel(stack);

        RelicLevelingData levelingData = getLevelingData(stack);

        if (levelingData == null || level >= levelingData.getMaxLevel())
            return;

        int requiredExp = getExperienceBetweenLevels(stack, level, level + 1);

        CompoundTag data = getLevelingTag(stack);

        if (experience >= requiredExp) {
            int sumExp = getTotalExperienceForLevel(stack, level) + experience;
            int resultLevel = getLevelFromExperience(stack, sumExp);

            data.putInt(TAG_EXPERIENCE, sumExp - getTotalExperienceForLevel(stack, resultLevel));

            setLevelingTag(stack, data);
            addPoints(stack, resultLevel - level);
            setLevel(stack, resultLevel);
        } else {
            data.putInt(TAG_EXPERIENCE, experience);

            setLevelingTag(stack, data);
        }
    }

    public static void addExperience(ItemStack stack, int amount) {
        ExperienceAddEvent event = new ExperienceAddEvent(null, stack, amount);

        MinecraftForge.EVENT_BUS.post(event);

        if (!event.isCanceled())
            setExperience(stack, getExperience(stack) + event.getAmount());
    }

    public static void addExperience(Entity entity, ItemStack stack, int amount) {
        if (!(entity instanceof Player player))
            return;

        ExperienceAddEvent event = new ExperienceAddEvent(player, stack, amount);

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

        RelicLevelingData data = ((RelicItem) stack.getItem()).getRelicData().getLevelingData();

        int result = data.getInitialCost();

        for (int i = 1; i < level; i++)
            result += data.getInitialCost() + (data.getStep() * i);

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

    /*
    =================================================
                        Quality
    =================================================
    */

    public static final int MAX_QUALITY = 10;

    public static int getStatQuality(ItemStack stack, String ability, String stat) {
        RelicAbilityStat statData = getAbilityStat(stack, ability, stat);

        if (statData == null)
            return 0;

        Function<Double, ? extends Number> format = statData.getFormatValue();

        double initial = format.apply(getAbilityInitialValue(stack, ability, stat)).doubleValue();

        double min = format.apply(statData.getInitialValue().first()).doubleValue();
        double max = format.apply(statData.getInitialValue().second()).doubleValue();

        if (min == max)
            return MAX_QUALITY;

        return (int) Math.round((initial - min) / ((max - min) / MAX_QUALITY));
    }

    public static int getAbilityQuality(ItemStack stack, String ability) {
        RelicAbilityEntry entry = getAbilityEntryData(stack, ability);
        Map<String, RelicAbilityStat> stats = entry.getStats();

        if (stats.isEmpty())
            return 0;

        int sum = 0;

        for (String stat : stats.keySet())
            sum += getStatQuality(stack, ability, stat);

        return sum / stats.size();
    }

    public static int getRelicQuality(ItemStack stack) {
        RelicAbilityData data = getAbilityData(stack);
        Map<String, RelicAbilityEntry> abilities = data.getAbilities();

        if (abilities.isEmpty())
            return 0;

        int size = abilities.size();
        int sum = 0;

        for (String ability : abilities.keySet()) {
            RelicAbilityEntry abilityData = getAbilityEntryData(stack, ability);

            if (abilityData == null || abilityData.getMaxLevel() == 0) {
                --size;

                continue;
            }

            sum += getAbilityQuality(stack, ability);
        }

        return sum / size;
    }
}