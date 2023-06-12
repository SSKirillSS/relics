package it.hurts.sskirillss.relics.items.relics.hands;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.SolidSnowballItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.Optional;

public class WoolMittenItem extends RelicItem {
    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("mold", RelicAbilityEntry.builder()
                                .stat("size", RelicAbilityStat.builder()
                                        .initialValue(12D, 32D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .stat("damage", RelicAbilityStat.builder()
                                        .initialValue(0.05D, 0.25D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat("stun", RelicAbilityStat.builder()
                                        .initialValue(0.025D, 0.05D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 3))
                                        .build())
                                .stat("freeze", RelicAbilityStat.builder()
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.3D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#eed551", "#dcbe1d")
                        .build())
                .build();
    }

    @Mod.EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
            Player player = event.getEntity();

            ItemStack relic = EntityUtils.findEquippedCurio(player, ItemRegistry.WOOL_MITTEN.get());

            if (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty() || relic.isEmpty())
                return;

            Level level = player.level();
            BlockPos pos = event.getPos();
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            int layers = block == Blocks.SNOW ? state.getValue(SnowLayerBlock.LAYERS) : block == Blocks.SNOW_BLOCK ? 9 : 0;

            if (layers == 0)
                return;

            Inventory inventory = player.getInventory();

            int size = (int) Math.round(AbilityUtils.getAbilityValue(relic, "mold", "size"));

            Optional<Integer> slot = EntityUtils.getSlotsWithItem(player, ItemRegistry.SOLID_SNOWBALL.get()).stream()
                    .filter(id -> NBTUtils.getInt(inventory.getItem(id), SolidSnowballItem.TAG_SNOW, 0) < size)
                    .max(Comparator.comparingInt(s -> NBTUtils.getInt(inventory.items.get(s), SolidSnowballItem.TAG_SNOW, 0)));

            if (slot.isEmpty()) {
                if (inventory.add(new ItemStack(ItemRegistry.SOLID_SNOWBALL.get()))) {
                    slot = EntityUtils.getSlotsWithItem(player, ItemRegistry.SOLID_SNOWBALL.get()).stream().findFirst();

                    if (slot.isEmpty())
                        return;
                } else
                    return;
            }

            ItemStack stack = inventory.getItem(slot.get());

            level.destroyBlock(pos, false);

            int snow = NBTUtils.getInt(stack, SolidSnowballItem.TAG_SNOW, 0);

            NBTUtils.setInt(stack, SolidSnowballItem.TAG_SNOW, Math.min(snow + layers, size));
        }
    }
}