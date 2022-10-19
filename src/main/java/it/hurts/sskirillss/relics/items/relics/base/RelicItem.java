package it.hurts.sskirillss.relics.items.relics.base;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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
                            stack.getItem().getRegistryName().getPath() + "_" + attribute.getAttribute().getRegistryName().getPath(),
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
            Random random = entity.getCommandSenderWorld().getRandom();

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
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 1;
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
                        Abilities
    =================================================
     */

    public static final String TAG_ABILITIES = "abilities";
    public static final String TAG_STATS = "stats";

    @Nullable
    public static RelicAbilityEntry getAbility(ItemStack stack, String ability) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return null;

        return getAbility(relic, ability);
    }

    @Nullable
    public static RelicAbilityEntry getAbility(RelicItem relic, String ability) {
        if (relic.getRelicData() == null)
            return null;

        RelicAbilityData data = relic.getRelicData().getAbilityData();

        return data == null ? null : data.getAbilities().get(ability);
    }

    @Nullable
    public static RelicAbilityStat getAbilityStat(ItemStack stack, String ability, String stat) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return null;

        return getAbilityStat(relic, ability, stat);
    }

    @Nullable
    public static RelicAbilityStat getAbilityStat(RelicItem relic, String ability, String stat) {
        RelicAbilityEntry entry = getAbility(relic, ability);

        return entry == null ? null : entry.getStats().get(stat);
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
            case MULTIPLY -> result = current * (points * step);
        }

        return MathUtils.round(result, 3);
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
        RelicAbilityEntry entry = getAbility(stack, ability);

        return entry != null && getLevel(stack) >= entry.getRequiredLevel();
    }

    public static boolean randomizeStats(ItemStack stack, String ability) {
        RelicAbilityEntry entry = RelicItem.getAbility(stack, ability);

        if (entry == null)
            return false;

        for (Map.Entry<String, RelicAbilityStat> stats : entry.stats.entrySet()) {
            RelicAbilityStat stat = stats.getValue();

            double result = MathUtils.round(MathUtils.randomBetween(new Random(), stat.getInitialValue().first(), stat.getInitialValue().second()), 3);

            RelicItem.setAbilityValue(stack, ability, stats.getKey(), result);
        }

        return true;
    }

    public static int getUpgradeRequiredExperience(ItemStack stack, String ability) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return 0;

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return 0;

        RelicAbilityData abilityData = relicData.getAbilityData();

        if (abilityData == null)
            return 0;

        RelicAbilityEntry entry = abilityData.getAbilities().get(ability);

        if (entry == null)
            return 0;

        return (RelicItem.getLevel(stack) + 1) * (RelicItem.getAbilityPoints(stack, ability) + 1) * entry.getRequiredPoints();
    }

    public static boolean isAbilityMaxLevel(ItemStack stack, String ability) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return false;

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return false;

        RelicAbilityData abilityData = relicData.getAbilityData();

        if (abilityData == null)
            return false;

        RelicAbilityEntry entry = abilityData.getAbilities().get(ability);

        if (entry == null)
            return false;

        return RelicItem.getAbilityPoints(stack, ability) >= (entry.getMaxLevel() == -1 ? ((relicData.getLevelingData().getMaxLevel() - entry.getRequiredLevel()) / entry.getRequiredPoints()) : entry.getMaxLevel());
    }

    public static boolean mayUpgrade(ItemStack stack, String ability) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return false;

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return false;

        RelicAbilityData abilityData = relicData.getAbilityData();

        if (abilityData == null)
            return false;

        RelicAbilityEntry entry = abilityData.getAbilities().get(ability);

        if (entry == null)
            return false;

        return !isAbilityMaxLevel(stack, ability) && RelicItem.getPoints(stack) >= entry.getRequiredPoints() && canUseAbility(stack, ability);
    }

    public static boolean mayPlayerUpgrade(Player player, ItemStack stack, String ability) {
        return mayUpgrade(stack, ability) && player.totalExperience >= getUpgradeRequiredExperience(stack, ability);
    }

    public static int getRerollRequiredExperience(ItemStack stack, String ability) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return 0;

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return 0;

        RelicAbilityData abilityData = relicData.getAbilityData();

        if (abilityData == null)
            return 0;

        RelicAbilityEntry entry = abilityData.getAbilities().get(ability);

        if (entry == null)
            return 0;

        int count = entry.getStats().size();

        if (count == 0)
            return 0;

        return 100 / count;
    }

    public static boolean mayReroll(ItemStack stack, String ability) {
        return getRerollRequiredExperience(stack, ability) > 0 && canUseAbility(stack, ability);
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
        return mayReset(stack, ability) && player.totalExperience >= getResetRequiredExperience(stack, ability);
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

    public static CompoundTag getLevelingData(ItemStack stack) {
        return NBTUtils.getCompound(stack, TAG_LEVELING, new CompoundTag());
    }

    public static void setLevelingData(ItemStack stack, CompoundTag data) {
        NBTUtils.setCompound(stack, TAG_LEVELING, data);
    }

    public static int getPoints(ItemStack stack) {
        return getLevelingData(stack).getInt(TAG_POINTS);
    }

    public static void setPoints(ItemStack stack, int level) {
        CompoundTag tag = getLevelingData(stack);

        tag.putInt(TAG_POINTS, level);

        setLevelingData(stack, tag);
    }

    public static void addPoints(ItemStack stack, int amount) {
        setPoints(stack, getPoints(stack) + amount);
    }

    public static int getLevel(ItemStack stack) {
        return getLevelingData(stack).getInt(TAG_LEVEL);
    }

    public static void setLevel(ItemStack stack, int level) {
        CompoundTag tag = getLevelingData(stack);

        tag.putInt(TAG_LEVEL, Math.min(((RelicItem) stack.getItem()).getRelicData().getLevelingData().getMaxLevel(), level));

        setLevelingData(stack, tag);
    }

    public static void addLevel(ItemStack stack, int amount) {
        setLevel(stack, getLevel(stack) + amount);
    }

    public static int getExperience(ItemStack stack) {
        return getLevelingData(stack).getInt(TAG_EXPERIENCE);
    }

    public static void setExperience(ItemStack stack, int experience) {
        CompoundTag data = getLevelingData(stack);

        while (getExperience(stack) + experience >= getExperienceLeftForLevel(stack, getLevel(stack) + 1)) {
            setExperience(stack, 0);

            experience -= getExperienceLeftForLevel(stack, getLevel(stack) + 1) - getExperience(stack);

            addLevel(stack, 1);
        }

        data.putInt(TAG_EXPERIENCE, experience);

        setLevelingData(stack, data);
    }

    public static void addExperience(ItemStack stack, int amount) {
        setExperience(stack, getExperience(stack) + amount);
    }

    public static int getExperienceLeftForLevel(ItemStack stack, int level) {
        int currentLevel = getLevel(stack);

        return getExperienceBetweenLevels(stack, currentLevel, level) - getExperience(stack);
    }

    public static int getExperienceBetweenLevels(ItemStack stack, int from, int to) {
        return getTotalExperienceForLevel(stack, to) - getTotalExperienceForLevel(stack, from);
    }

    public static int getTotalExperienceForLevel(ItemStack stack, int level) {
        RelicLevelingData data = ((RelicItem) stack.getItem()).getRelicData().getLevelingData();

        return data.getInitialCost() + (data.getStep() * (level - 1));
    }
}