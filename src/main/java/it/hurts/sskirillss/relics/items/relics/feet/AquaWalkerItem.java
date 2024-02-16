package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.api.events.common.FluidCollisionEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class AquaWalkerItem extends RelicItem {
    public static final String TAG_DRENCH = "drench";

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("walking")
                                .stat(StatData.builder("time")
                                        .initialValue(30D, 60D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 0)))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .style(RelicStyleData.builder()
                        .borders("#ff6900", "#ff2e00")
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.AQUATIC)
                        .build())
                .build();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        int drench = NBTUtils.getInt(stack, TAG_DRENCH, 0);

        if (!(entity instanceof Player player) || player.tickCount % 20 != 0)
            return;

        if (drench > 0 && !player.isInWater() && !player.level().getFluidState(player.blockPosition().below()).is(FluidTags.WATER))
            NBTUtils.setInt(stack, TAG_DRENCH, --drench);
    }

    @SubscribeEvent
    public static void onFluidCollide(FluidCollisionEvent event) {
        ItemStack stack = EntityUtils.findEquippedCurio(event.getEntity(), ItemRegistry.AQUA_WALKER.get());

        if (!(stack.getItem() instanceof IRelicItem relic))
            return;

        int drench = NBTUtils.getInt(stack, TAG_DRENCH, 0);

        if (!(event.getEntity() instanceof Player player) || drench > relic.getAbilityValue(stack, "walking", "time")
                || !event.getFluid().is(FluidTags.WATER) || player.isShiftKeyDown())
            return;

        if (player.tickCount % 20 == 0) {
            NBTUtils.setInt(stack, TAG_DRENCH, ++drench);

            if (drench % 5 == 0)
                relic.addExperience(player, stack, 1);
        }

        event.setCanceled(true);
    }
}