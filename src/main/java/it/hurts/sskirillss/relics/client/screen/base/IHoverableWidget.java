package it.hurts.sskirillss.relics.client.screen.base;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IHoverableWidget {
    void onHovered(PoseStack poseStack, int mouseX, int mouseY);
}