package it.hurts.sskirillss.relics.tiles.base;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;

public interface IHasHUDInfo {
    void renderHUDInfo(PoseStack poseStack, Window window);
}