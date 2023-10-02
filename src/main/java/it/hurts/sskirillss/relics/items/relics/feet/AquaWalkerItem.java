package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.api.events.common.FluidCollisionEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
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
    public RelicData constructRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("walking", RelicAbilityEntry.builder()
                                .stat("time", RelicAbilityStat.builder()
                                        .initialValue(30D, 60D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 0)))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#ff6900", "#ff2e00")
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
        int drench = NBTUtils.getInt(stack, TAG_DRENCH, 0);

        if (!(event.getEntity() instanceof Player player) || stack.isEmpty() || drench > AbilityUtils.getAbilityValue(stack, "walking", "time")
                || !event.getFluid().is(FluidTags.WATER) || player.isShiftKeyDown())
            return;

        if (player.tickCount % 20 == 0) {
            NBTUtils.setInt(stack, TAG_DRENCH, ++drench);

            if (drench % 5 == 0)
                LevelingUtils.addExperience(player, stack, 1);
        }

        event.setCanceled(true);
    }
}