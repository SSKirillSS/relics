package it.hurts.sskirillss.relics.api.events.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.api.events.common.ContainerSlotClickEvent;
import it.hurts.sskirillss.relics.api.events.common.FluidCollisionEvent;
import it.hurts.sskirillss.relics.api.events.common.LivingSlippingEvent;
import it.hurts.sskirillss.relics.api.events.common.TooltipDisplayEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.MinecraftForge;

public class EventDispatcher {
    public static boolean onFluidCollision(LivingEntity entity, FluidState fluid) {
        FluidCollisionEvent event = new FluidCollisionEvent(entity, fluid);

        MinecraftForge.EVENT_BUS.post(event);

        return event.isCanceled();
    }

    public static float onLivingSlipping(LivingEntity entity, BlockState state, float friction) {
        LivingSlippingEvent event = new LivingSlippingEvent(entity, state, friction);

        MinecraftForge.EVENT_BUS.post(event);

        return event.getFriction();
    }

    public static boolean onSlotClick(Player player, AbstractContainerMenu container, Slot slot, ClickAction action, ItemStack heldStack, ItemStack slotStack) {
        ContainerSlotClickEvent event = new ContainerSlotClickEvent(player, container, slot, action, heldStack, slotStack);

        MinecraftForge.EVENT_BUS.post(event);

        return event.isCanceled();
    }

    public static void onRenderTooltip(ItemStack stack, PoseStack poseStack, int width, int height, int x, int y) {
        TooltipDisplayEvent event = new TooltipDisplayEvent(stack, poseStack, width, height, x, y);

        MinecraftForge.EVENT_BUS.post(event);
    }
}