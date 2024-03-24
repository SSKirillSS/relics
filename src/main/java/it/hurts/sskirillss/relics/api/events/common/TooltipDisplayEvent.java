package it.hurts.sskirillss.relics.api.events.common;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

@Data
@AllArgsConstructor
public class TooltipDisplayEvent extends Event {
    private final ItemStack stack;

    private final PoseStack pose;

    private final int width;
    private final int height;

    private final int x;
    private final int y;
}