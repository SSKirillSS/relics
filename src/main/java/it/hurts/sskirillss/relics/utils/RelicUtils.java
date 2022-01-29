package it.hurts.sskirillss.relics.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

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
        public static Player getOwner(ItemStack stack, Level world) {
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

    public static class Levelgen {
        public static final List<String> AQUATIC = Arrays.asList(
                BuiltInLootTables.UNDERWATER_RUIN_BIG.toString(),
                BuiltInLootTables.UNDERWATER_RUIN_SMALL.toString(),
                BuiltInLootTables.SHIPWRECK_TREASURE.toString()
        );

        public static final List<String> NETHER = Arrays.asList(
                BuiltInLootTables.NETHER_BRIDGE.toString(),
                BuiltInLootTables.BASTION_BRIDGE.toString(),
                BuiltInLootTables.BASTION_OTHER.toString(),
                BuiltInLootTables.BASTION_TREASURE.toString(),
                BuiltInLootTables.BASTION_HOGLIN_STABLE.toString(),
                BuiltInLootTables.RUINED_PORTAL.toString()
        );

        public static final List<String> COLD = Arrays.asList(
                BuiltInLootTables.IGLOO_CHEST.toString(),
                BuiltInLootTables.VILLAGE_SNOWY_HOUSE.toString(),
                BuiltInLootTables.VILLAGE_TAIGA_HOUSE.toString()
        );

        public static final List<String> DESERT = Arrays.asList(
                BuiltInLootTables.DESERT_PYRAMID.toString(),
                BuiltInLootTables.VILLAGE_DESERT_HOUSE.toString()
        );

        public static final List<String> CAVE = Arrays.asList(
                BuiltInLootTables.STRONGHOLD_CORRIDOR.toString(),
                BuiltInLootTables.STRONGHOLD_CROSSING.toString(),
                BuiltInLootTables.STRONGHOLD_LIBRARY.toString(),
                BuiltInLootTables.ABANDONED_MINESHAFT.toString()
        );

        public static final List<String> VILLAGE = Arrays.asList(
                BuiltInLootTables.VILLAGE_DESERT_HOUSE.toString(),
                BuiltInLootTables.VILLAGE_SNOWY_HOUSE.toString(),
                BuiltInLootTables.VILLAGE_TAIGA_HOUSE.toString(),
                BuiltInLootTables.VILLAGE_PLAINS_HOUSE.toString(),
                BuiltInLootTables.VILLAGE_SAVANNA_HOUSE.toString()
        );
    }
}