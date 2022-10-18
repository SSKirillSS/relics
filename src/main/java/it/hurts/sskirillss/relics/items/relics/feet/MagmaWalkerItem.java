package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.api.events.FluidCollisionEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

public class MagmaWalkerItem extends RelicItem {
    public static final String TAG_HEAT = "heat";

    @Override
    public RelicStyleData getStyle(ItemStack stack) {
        return RelicStyleData.builder()
                .borders("#ff6900", "#ff2e00")
                .ability(AbilityTooltip.builder()
                        .build())
                .ability(AbilityTooltip.builder()
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        int heat = NBTUtils.getInt(stack, TAG_HEAT, 0);

        if (!(slotContext.getWearer() instanceof Player player) || player.tickCount % 20 != 0)
            return;

        if (heat > 0) {
            if (heat > 60)
                player.hurt(DamageSource.HOT_FLOOR, 1 + ((heat - 60) / 10F));

            Level level = player.level;

            if (!level.getFluidState(player.blockPosition().below()).is(FluidTags.LAVA)
                    && !level.getFluidState(player.blockPosition()).is(FluidTags.LAVA))
                NBTUtils.setInt(stack, TAG_HEAT, --heat);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.MAGMA_WALKER.get());

            if (!stack.isEmpty() && event.getSource() == DamageSource.HOT_FLOOR
                    && NBTUtils.getInt(stack, TAG_HEAT, 0) <= 60) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onFluidCollide(FluidCollisionEvent event) {
            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.MAGMA_WALKER.get());

            if (!(event.getEntityLiving() instanceof Player player) || stack.isEmpty()
                    || !event.getFluid().is(FluidTags.LAVA) || player.isShiftKeyDown())
                return;

            if (player.tickCount % 20 == 0)
                NBTUtils.setInt(stack, TAG_HEAT, NBTUtils.getInt(stack, TAG_HEAT, 0) + 1);

            event.setCanceled(true);
        }
    }
}