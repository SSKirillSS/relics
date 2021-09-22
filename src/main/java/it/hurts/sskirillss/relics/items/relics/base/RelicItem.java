package it.hurts.sskirillss.relics.items.relics.base;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicDurability;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.base.handlers.DurabilityHandler;
import it.hurts.sskirillss.relics.items.relics.base.handlers.TooltipHandler;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class RelicItem<T extends RelicStats> extends Item implements ICurioItem {
    @Getter
    @Setter
    protected RelicData data;
    @Getter
    @Setter
    protected T config;

    @SneakyThrows
    public RelicItem(RelicData data) {
        super(data.getRarity() == null ? data.getProperties()
                : data.getProperties().rarity(data.getRarity()));

        setData(data);
        setConfig((T) data.getConfig().newInstance());
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!(entityIn instanceof PlayerEntity))
            return;

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip();
    }

    public List<ITextComponent> getAltTooltip(ItemStack stack) {
        return new ArrayList<>();
    }

    public List<ITextComponent> getControlTooltip(ItemStack stack) {
        return new ArrayList<>();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn == null)
            return;

        TooltipHandler.setupTooltip(stack, worldIn, tooltip);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        RelicDurability durability = DurabilityHandler.DURABILITY.get(this);
        int value = data.getDurability().getMaxDurability();

        if (durability != null)
            value = durability.getMaxDurability();

        return value;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return getMaxDamage(stack) > 0;
    }

    public boolean hasAbility() {
        return false;
    }

    public void castAbility(PlayerEntity player, ItemStack stack) {

    }
}