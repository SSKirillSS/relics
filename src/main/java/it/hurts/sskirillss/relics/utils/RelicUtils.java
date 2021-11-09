package it.hurts.sskirillss.relics.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RelicUtils {
    public static class Owner {
        private static final String TAG_OWNER = "owner";

        public static String getOwnerUUID(ItemStack stack) {
            return NBTUtils.getString(stack, TAG_OWNER, "");
        }

        @Nullable
        public static PlayerEntity getOwner(ItemStack stack, World world) {
            String uuid = getOwnerUUID(stack);

            try {
                return world.getPlayerByUUID(UUID.fromString(uuid));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public static void setOwnerUUID(ItemStack stack, String uuid) {
            NBTUtils.setString(stack, TAG_OWNER, uuid);
        }
    }

    public static class Worldgen {
        public static final List<String> AQUATIC = Arrays.asList(
                LootTables.UNDERWATER_RUIN_BIG.toString(),
                LootTables.UNDERWATER_RUIN_SMALL.toString(),
                LootTables.SHIPWRECK_TREASURE.toString()
        );

        public static final List<String> NETHER = Arrays.asList(
                LootTables.NETHER_BRIDGE.toString(),
                LootTables.BASTION_BRIDGE.toString(),
                LootTables.BASTION_OTHER.toString(),
                LootTables.BASTION_TREASURE.toString(),
                LootTables.BASTION_HOGLIN_STABLE.toString(),
                LootTables.RUINED_PORTAL.toString()
        );

        public static final List<String> COLD = Arrays.asList(
                LootTables.IGLOO_CHEST.toString(),
                LootTables.VILLAGE_SNOWY_HOUSE.toString(),
                LootTables.VILLAGE_TAIGA_HOUSE.toString()
        );

        public static final List<String> DESERT = Arrays.asList(
                LootTables.DESERT_PYRAMID.toString(),
                LootTables.VILLAGE_DESERT_HOUSE.toString()
        );

        public static final List<String> CAVE = Arrays.asList(
                LootTables.STRONGHOLD_CORRIDOR.toString(),
                LootTables.STRONGHOLD_CROSSING.toString(),
                LootTables.STRONGHOLD_LIBRARY.toString(),
                LootTables.ABANDONED_MINESHAFT.toString()
        );

        public static final List<String> VILLAGE = Arrays.asList(
                LootTables.VILLAGE_DESERT_HOUSE.toString(),
                LootTables.VILLAGE_SNOWY_HOUSE.toString(),
                LootTables.VILLAGE_TAIGA_HOUSE.toString(),
                LootTables.VILLAGE_PLAINS_HOUSE.toString(),
                LootTables.VILLAGE_SAVANNA_HOUSE.toString()
        );
    }
}