package it.hurts.sskirillss.relics.utils;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RuneLoot;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
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
            return !uuid.equals("") ? world.getPlayerByUUID(UUID.fromString(uuid)) : null;
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

    public static class Crafting {
        public static HashMap<RuneItem, List<Item>> INGREDIENTS = new HashMap<RuneItem, List<Item>>();

        public static List<Item> getIngredients(Item item) {
            if (!(item instanceof RuneItem)) return Lists.newArrayList();
            return INGREDIENTS.get((RuneItem) item);
        }
    }

    public static class RunesWorldgen {
        public static HashMap<RuneItem, RuneLoot> LOOT = new HashMap<RuneItem, RuneLoot>();

        public static final List<ResourceLocation> CHESTS = Arrays.asList(
                LootTables.UNDERWATER_RUIN_BIG,
                LootTables.UNDERWATER_RUIN_SMALL,
                LootTables.SHIPWRECK_TREASURE,
                LootTables.NETHER_BRIDGE,
                LootTables.BASTION_BRIDGE,
                LootTables.BASTION_OTHER,
                LootTables.BASTION_TREASURE,
                LootTables.BASTION_HOGLIN_STABLE,
                LootTables.RUINED_PORTAL,
                LootTables.IGLOO_CHEST,
                LootTables.DESERT_PYRAMID,
                LootTables.STRONGHOLD_CORRIDOR,
                LootTables.STRONGHOLD_CROSSING,
                LootTables.STRONGHOLD_LIBRARY,
                LootTables.ABANDONED_MINESHAFT
        );
    }
}