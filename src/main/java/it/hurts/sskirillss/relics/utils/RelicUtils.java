package it.hurts.sskirillss.relics.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class RelicUtils {
    public static class Owner {
        private static final String TAG_OWNER = "owner";

        @Nullable
        public static UUID getOwnerUUID(ItemStack stack) {
            String uuid = NBTUtils.getString(stack, TAG_OWNER, "");
            return uuid.equals("") ? null : UUID.fromString(uuid);
        }

        @Nullable
        public static PlayerEntity getOwner(ItemStack stack, World world) {
            UUID uuid = getOwnerUUID(stack);
            return uuid != null ? world.getPlayerByUUID(uuid) : null;
        }

        public static void setOwnerUUID(ItemStack stack, UUID uuid) {
            NBTUtils.setString(stack, TAG_OWNER, uuid.toString());
        }
    }
}