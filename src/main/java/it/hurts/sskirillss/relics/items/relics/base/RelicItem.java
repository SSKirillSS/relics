package it.hurts.sskirillss.relics.items.relics.base;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.base.handlers.TooltipHandler;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.awt.*;
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
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!isBroken(stack)) {
            Vector3d pos = entity.position();

            entity.getCommandSenderWorld().addParticle(new CircleTintData(stack.getRarity().color.getColor() != null
                            ? new Color(stack.getRarity().color.getColor(), false) : new Color(255, 255, 255),
                            random.nextFloat() * 0.025F + 0.04F, 25, 0.95F, true),
                    pos.x() + MathUtils.randomFloat(random) * 0.25F, pos.y() + 0.1F,
                    pos.z() + MathUtils.randomFloat(random) * 0.25F, 0, random.nextFloat() * 0.05D, 0);
        }

        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof PlayerEntity && !isBroken(stack))
            super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn == null)
            return;

        TooltipHandler.setupTooltip(stack, worldIn, tooltip);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity) {
        return !isBroken(stack);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return !isBroken(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return data.getDurability().getMaxDurability();
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return getMaxDamage(stack) > 0;
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

    public static boolean isBroken(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue() <= 0;
    }

    public void castAbility(PlayerEntity player, ItemStack stack) {

    }
}