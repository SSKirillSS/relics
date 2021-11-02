package it.hurts.sskirillss.relics.api.durability;

import net.minecraft.item.ItemStack;

public interface IRepairableItem {
    static boolean isBroken(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage();
    }

    default void hurt(ItemStack stack, int amount) {
        stack.setDamageValue(Math.min(stack.getDamageValue() + amount, stack.getMaxDamage()));
    }

    default void repair(ItemStack stack, int amount) {
        stack.setDamageValue(Math.max(stack.getDamageValue() - amount, 0));
    }

    default int getDurability(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }
}