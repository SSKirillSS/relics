package it.hurts.sskirillss.relics.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class NBTUtils {
    public static void setBoolean(ItemStack stack, String tag, boolean value) {
        stack.getOrCreateTag().putBoolean(tag, value);
    }

    public static void setInt(ItemStack stack, String tag, int value) {
        stack.getOrCreateTag().putInt(tag, value);
    }

    public static void setFloat(ItemStack stack, String tag, float value) {
        stack.getOrCreateTag().putFloat(tag, value);
    }

    public static void setDouble(ItemStack stack, String tag, double value) {
        stack.getOrCreateTag().putDouble(tag, value);
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

    public static float getFloat(ItemStack stack, String tag, float defaultValue) {
        return safeCheck(stack, tag) ? stack.getOrCreateTag().getFloat(tag) : defaultValue;
    }

    public static double getDouble(ItemStack stack, String tag, double defaultValue) {
        return safeCheck(stack, tag) ? stack.getOrCreateTag().getInt(tag) : defaultValue;
    }

    public static String getString(ItemStack stack, String tag, String defaultValue) {
        return safeCheck(stack, tag) ? stack.getOrCreateTag().getString(tag) : defaultValue;
    }

    public static boolean safeCheck(ItemStack stack, String tag) {
        return !stack.isEmpty() && stack.getOrCreateTag().contains(tag);
    }

    public static String writePosition(Vector3d vec) {
        return (Math.round(vec.getX() * 10F) / 10F) + "," + (Math.round(vec.getY() * 10F) / 10F) + "," + (Math.round(vec.getZ() * 10F) / 10F);
    }

    @Nullable
    public static Vector3d parsePosition(String value) {
        if (value != null && !value.equals("")) {
            String[] pos = value.split(",");
            return new Vector3d(Double.parseDouble(pos[0]), Double.parseDouble(pos[1]), Double.parseDouble(pos[2]));
        }
        return null;
    }

    @Nullable
    public static ServerWorld parseWorld(World world, String value) {
        return world.getServer().getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(value)));
    }
}