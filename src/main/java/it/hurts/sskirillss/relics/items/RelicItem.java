package it.hurts.sskirillss.relics.items;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.RelicsTab;
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
        if (RelicUtils.Durability.getDurability(stack) == -1) RelicUtils.Durability.setDurability(stack, RelicUtils.Durability.getMaxDurability(this));
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn == null) return;
        PlayerEntity owner = RelicUtils.Owner.getOwner(stack, worldIn);
        if (RelicsConfig.RelicsGeneral.STORE_RELIC_OWNER.get()) tooltip.add(new TranslationTextComponent("tooltip.relics.owner",
                owner != null ? owner.getDisplayName() : new TranslationTextComponent("tooltip.relics.owner.unknown")));

        int level = RelicUtils.Level.getLevel(stack);
        int prevExp = RelicUtils.Level.getTotalExperienceForLevel(stack, Math.max(level, level - 1));
        tooltip.add(new TranslationTextComponent("tooltip.relics.level", level, RelicUtils.Level.getExperience(stack) - prevExp,
                RelicUtils.Level.getTotalExperienceForLevel(stack, level + 1) - prevExp));
        float percentage = (RelicUtils.Level.getExperience(stack) - prevExp) * 1.0F / (RelicUtils.Level.getTotalExperienceForLevel(stack,
                RelicUtils.Level.getLevel(stack) + 1) - prevExp) * 100;
        StringBuilder string = new StringBuilder(RelicsConfig.RelicsGeneral.LEVELING_BAR_STYLE.get());
        int offset = (int) Math.min(100, Math.floor(string.length() * percentage / 100));
        Color color = Color.parseColor(percentage > 33.3 ? percentage > 66.6 ? RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_HIGH.get()
                : RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_MEDIUM.get() : RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_LOW.get());
        StringTextComponent component = new StringTextComponent("");
        component.append(new StringTextComponent(string.substring(0, offset)).setStyle(Style.EMPTY.withColor(color)));
        component.append(new StringTextComponent(string.substring(offset, string.length())).setStyle(Style.EMPTY
                .withColor(Color.parseColor(RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_NEUTRAL.get()))));
        component.append(new StringTextComponent(" " + Math.round(percentage * 10.0F) / 10.0F + "%").setStyle(Style.EMPTY.withColor(color)));
        tooltip.add(component);
        tooltip.add(new StringTextComponent("Durability: " + RelicUtils.Durability.getDurability(stack)));
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
        return 0.2F;
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