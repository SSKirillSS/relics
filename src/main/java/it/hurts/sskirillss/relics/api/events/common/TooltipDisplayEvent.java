package it.hurts.sskirillss.relics.api.events.common;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class TooltipDisplayEvent extends Event {
    @Getter
    private final ItemStack stack;
    @Getter
    private final PoseStack poseStack;
    @Getter
    private final int width;
    @Getter
    private final int height;
    @Getter
    private final int x;
    @Getter
    private final int y;

    public TooltipDisplayEvent(ItemStack stack, PoseStack poseStack, int width, int height, int x, int y) {
        this.stack = stack;
        this.poseStack = poseStack;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }
}