package it.hurts.sskirillss.relics.items;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class RelicItem<T extends RelicStats> extends Item {
    protected T config;

    public RelicItem(Rarity rarity) {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .stacksTo(1)
                .rarity(rarity));
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!(entityIn instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) entityIn;
        if (RelicsConfig.RelicsGeneral.STORE_RELIC_OWNER.get()) {
            if (RelicUtils.Owner.getOwner(stack, worldIn) == null) RelicUtils.Owner.setOwnerUUID(stack, player.getUUID());
            PlayerEntity owner = RelicUtils.Owner.getOwner(stack, worldIn);
            if (RelicsConfig.RelicsGeneral.DAMAGE_NON_RELIC_OWNER_AMOUNT.get() > 0 && player.tickCount % 20 == 0 && owner != player)
                entityIn.hurt(owner != null ? DamageSource.playerAttack(owner) : DamageSource.GENERIC,
                        RelicsConfig.RelicsGeneral.DAMAGE_NON_RELIC_OWNER_AMOUNT.get().floatValue());
        }
        if (RelicUtils.Durability.getDurability(stack) == -1) RelicUtils.Durability.setDurability(stack, RelicUtils.Durability.getMaxDurability(stack.getItem()));
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        return Lists.newArrayList();
    }

    public List<ITextComponent> getAltTooltip(ItemStack stack) {
        return Lists.newArrayList();
    }

    public List<ITextComponent> getControlTooltip(ItemStack stack) {
        return Lists.newArrayList();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn == null) return;
        PlayerEntity owner = RelicUtils.Owner.getOwner(stack, worldIn);
        if (RelicsConfig.RelicsGeneral.STORE_RELIC_OWNER.get()) tooltip.add(new TranslationTextComponent("tooltip.relics.owner",
                owner != null ? owner.getDisplayName() : new TranslationTextComponent("tooltip.relics.owner.unknown")));
        int durability = RelicUtils.Durability.getDurability(stack);
        tooltip.add(new TranslationTextComponent("tooltip.relics.durability",
                durability == -1 ? 0 : durability, RelicUtils.Durability.getMaxDurability(stack.getItem())));
//        int level = RelicUtils.Level.getLevel(stack);
//        int prevExp = RelicUtils.Level.getTotalExperienceForLevel(stack, Math.max(level, level - 1));
//        tooltip.add(new TranslationTextComponent("tooltip.relics.level", level, RelicUtils.Level.getExperience(stack) - prevExp,
//                RelicUtils.Level.getTotalExperienceForLevel(stack, level + 1) - prevExp));
//        float percentage = (RelicUtils.Level.getExperience(stack) - prevExp) * 1.0F / (RelicUtils.Level.getTotalExperienceForLevel(stack,
//                RelicUtils.Level.getLevel(stack) + 1) - prevExp) * 100;
//        tooltip.add(TooltipUtils.drawProgressBar(percentage, RelicsConfig.RelicsGeneral.LEVELING_BAR_STYLE.get(),
//                RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_LOW.get(), RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_MEDIUM.get(),
//                RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_HIGH.get(), RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_NEUTRAL.get(), true));
        if (!getShiftTooltip(stack).isEmpty() && Screen.hasShiftDown()) {
            tooltip.add(new StringTextComponent(" "));
            tooltip.add(new TranslationTextComponent("tooltip.relics.shift.divider_up"));
            tooltip.addAll(getShiftTooltip(stack));
            tooltip.add(new TranslationTextComponent("tooltip.relics.shift.divider_down"));
        }
        if (!getAltTooltip(stack).isEmpty() && Screen.hasAltDown()) {
            tooltip.add(new StringTextComponent(" "));
            tooltip.add(new TranslationTextComponent("tooltip.relics.alt.divider_up"));
            tooltip.addAll(getAltTooltip(stack));
            tooltip.add(new TranslationTextComponent("tooltip.relics.alt.divider_sown"));
        }
        if (!getControlTooltip(stack).isEmpty() && Screen.hasControlDown()) {
            tooltip.add(new StringTextComponent(" "));
            tooltip.add(new TranslationTextComponent("tooltip.relics.control.divider_up"));
            tooltip.addAll(getControlTooltip(stack));
            tooltip.add(new TranslationTextComponent("tooltip.relics.control.divider_down"));
        }
        if ((!getShiftTooltip(stack).isEmpty() && !Screen.hasShiftDown()) || (!getAltTooltip(stack).isEmpty() && !Screen.hasAltDown())
                || (!getControlTooltip(stack).isEmpty() && !Screen.hasControlDown())) tooltip.add(new StringTextComponent(" "));
        if (!Screen.hasShiftDown() && !getShiftTooltip(stack).isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.shift.tooltip"));
        if (!Screen.hasAltDown() && !getAltTooltip(stack).isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.alt.tooltip"));
        if (!Screen.hasControlDown() && !getControlTooltip(stack).isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.ctrl.tooltip"));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public int getMaxLevel() {
        return 10;
    }

    public int getInitialExp() {
        return 100;
    }

    public int getExpRatio() {
        return 250;
    }

    public float getWorldgenChance() {
        float chance = 0F;
        switch (new ItemStack(this).getRarity()) {
            case COMMON:
                chance = 0.15F;
                break;
            case UNCOMMON:
                chance = 0.125F;
                break;
            case RARE:
                chance = 0.1F;
                break;
            case EPIC:
                chance = 0.075F;
                break;
        }
        return chance;
    }

    public List<ResourceLocation> getLootChests() {
        return Lists.newArrayList();
    }

    public int getDurability() {
        return 100;
    }

    public Class<T> getConfigClass() {
        return (Class<T>) RelicStats.class;
    }

    public T getConfig() {
        return this.config;
    }

    public void setConfig(T config) {
        this.config = config;
    }
}