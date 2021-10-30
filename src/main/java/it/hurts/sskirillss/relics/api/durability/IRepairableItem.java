package it.hurts.sskirillss.relics.api.durability;

import net.minecraft.item.ItemStack;

public interface IRepairableItem {
    static boolean isBroken(ItemStack stack) {
        return getDurability(stack) <= 0;
    }

    static void hurt(ItemStack stack, int amount) {
        stack.setDamageValue(Math.min(stack.getDamageValue() + amount, stack.getMaxDamage()));
    }

    static void repair(ItemStack stack, int amount) {
        stack.setDamageValue(Math.max(stack.getDamageValue() - amount, 0));
    }

    static int getDurability(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }
}