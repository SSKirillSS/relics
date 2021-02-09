package it.hurts.sskirillss.relics.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class NBTUtils {
    public static void setBoolean(ItemStack stack, String tag, boolean value) {
        stack.getOrCreateTag().putBoolean(tag, value);
    }

    public static void setInt(ItemStack stack, String tag, int value) {
        stack.getOrCreateTag().putInt(tag, value);
    }

    public static void setString(ItemStack stack, String tag, String value) {
        stack.getOrCreateTag().putString(tag, value);
    }

    public static boolean getBoolean(ItemStack stack, String tag, boolean defaultValue) {
        return safeCheck(stack, tag) ? stack.getOrCreateTag().getBoolean(tag) : defaultValue;
    }

    public static int getInt(ItemStack stack, String tag, int defaultValue) {
        return safeCheck(stack, tag) ? stack.getOrCreateTag().getInt(tag) : defaultValue;
    }

    public static String getString(ItemStack stack, String tag, String defaultValue) {
        return safeCheck(stack, tag) ? stack.getOrCreateTag().getString(tag) : defaultValue;
    }

    public static boolean safeCheck(ItemStack stack, String tag) {
        return !stack.isEmpty() && stack.getOrCreateTag().contains(tag);
    }

    public static String writePosition(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static BlockPos parsePosition(String value) {
        if (value != null && !value.equals("")) {
            String[] pos = value.split(",");
            return new BlockPos(Float.parseFloat(pos[0]), Float.parseFloat(pos[1]), Float.parseFloat(pos[2]));
        }
        return null;
    }
}