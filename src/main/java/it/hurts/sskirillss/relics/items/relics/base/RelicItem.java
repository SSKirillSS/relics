package it.hurts.sskirillss.relics.items.relics.base;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.Random;
import java.util.UUID;

public abstract class RelicItem<T extends RelicStats> extends Item implements ICurioItem, IRepairableItem {
    @Getter
    @Setter
    protected RelicData data;
    @Getter
    @Setter
    protected RelicLevelingData leveling;
    @Getter
    @Setter
    protected T stats;
    @Getter
    @Setter
    protected RelicConfigData<T> configData;

    @SneakyThrows
    public RelicItem(RelicData data) {
        super(data.getRarity() == null ? data.getProperties()
                : data.getProperties().rarity(data.getRarity()));

        setData(data);
        setConfig(getConfigData());
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

            entity.getCommandSenderWorld().addParticle(new CircleTintData(stack.getRarity().color.getColor() != null
                            ? new Color(stack.getRarity().color.getColor(), false) : new Color(255, 255, 255),
                            random.nextFloat() * 0.025F + 0.04F, 25, 0.95F, true),
                    pos.x() + MathUtils.randomFloat(random) * 0.25F, pos.y() + 0.1F,
                    pos.z() + MathUtils.randomFloat(random) * 0.25F, 0, random.nextFloat() * 0.05D, 0);
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
        float f = Math.max(0.0F, ((float) getMaxDamage(stack) - (float) stack.getDamageValue()) / (float) getMaxDamage(stack));
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    public RelicTooltip getTooltip(ItemStack stack) {
        return null;
    }

    public RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
        return null;
    }

    public RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return null;
    }

    public void castAbility(Player player, ItemStack stack) {

    }

    public void setConfig(RelicConfigData<?> data) {
        this.leveling = data.getLevel();
        this.stats = (T) data.getStats();
    }
}