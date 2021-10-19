package it.hurts.sskirillss.relics.utils;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RuneLoot;
import it.hurts.sskirillss.relics.items.RuneItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLevel;
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

    public static class Level {
        private static final String TAG_LEVEL = "level";
        private static final String TAG_EXPERIENCE = "experience";

        public static int getLevel(ItemStack stack) {
            if (!(stack.getItem() instanceof RelicItem)) return 0;
            return NBTUtils.getInt(stack, TAG_LEVEL, 0);
        }

        public static int getLevelFromExperience(ItemStack stack, int experience) {
            if (!(stack.getItem() instanceof RelicItem)) return 0;
            RelicLevel relicLevel = getRelicLevel((RelicItem) stack.getItem());
            int min = 0;
            int max = relicLevel.getMaxLevel();
            while (min <= max) {
                int mid = (min + max) / 2;
                int exp = getTotalExperienceForLevel(stack, mid);
                if (exp > experience) max = mid - 1;
                else min = mid + 1;
            }
            return max;
        }

        public static void setLevel(ItemStack stack, int level) {
            if (!(stack.getItem() instanceof RelicItem)) return;
            setExperience(stack, getTotalExperienceForLevel(stack, level));
        }

        public static void addLevel(ItemStack stack, int level) {
            if (!(stack.getItem() instanceof RelicItem)) return;
            setLevel(stack, getLevel(stack) + level);
        }

        public static void takeLevel(ItemStack stack, int level) {
            if (!(stack.getItem() instanceof RelicItem)) return;
            setLevel(stack, getLevel(stack) - level);
        }

        public static int getExperience(ItemStack stack) {
            if (!(stack.getItem() instanceof RelicItem)) return 0;
            return NBTUtils.getInt(stack, TAG_EXPERIENCE, 0);
        }

        public static int getExperienceForLevel(ItemStack stack, int level) {
            if (!(stack.getItem() instanceof RelicItem)) return 0;
            return getTotalExperienceForLevel(stack, level + 1) - getTotalExperienceForLevel(stack, level);
        }

        public static int getTotalExperienceForLevel(ItemStack stack, int level) {
            if (!(stack.getItem() instanceof RelicItem)) return 0;
            return getTotalExperienceForLevel(getRelicLevel((RelicItem) stack.getItem()), level);
        }

        public static int getTotalExperienceForLevel(RelicLevel relicLevel, int level) {
            return (2 * relicLevel.getInitialExp() + relicLevel.getExpRatio() * (level - 1)) * level / 2;
        }

        public static void setExperience(ItemStack stack, int experience) {
            if (!(stack.getItem() instanceof RelicItem)) return;
            RelicLevel relicLevel = getRelicLevel((RelicItem) stack.getItem());
            experience = Math.max(0, Math.min(relicLevel.getMaxExperience(), experience));
            NBTUtils.setInt(stack, TAG_LEVEL, Math.max(0, Math.min(relicLevel.getMaxLevel(), getLevelFromExperience(stack, experience))));
            NBTUtils.setInt(stack, TAG_EXPERIENCE, experience);
        }

        public static void addExperience(ItemStack stack, int experience) {
            if (!(stack.getItem() instanceof RelicItem)) return;
            setExperience(stack, getExperience(stack) + experience);
        }

        public static void takeExperience(ItemStack stack, int experience) {
            if (!(stack.getItem() instanceof RelicItem)) return;
            setExperience(stack, getExperience(stack) - experience);
        }

        protected static RelicLevel getRelicLevel(RelicItem relic) {
            return relic.getData().getLevel();
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