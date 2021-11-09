package it.hurts.sskirillss.relics.utils;

import net.minecraft.item.ItemStack;

public class DurabilityUtils {
    public static boolean isBroken(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage();
    }

    public static void hurt(ItemStack stack, int amount) {
        stack.setDamageValue(Math.min(stack.getDamageValue() + amount, stack.getMaxDamage()));
    }

    public static void repair(ItemStack stack, int amount) {
        stack.setDamageValue(Math.max(stack.getDamageValue() - amount, 0));
    }

    public static int getDurability(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }
}