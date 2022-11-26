package it.hurts.sskirillss.relics.tiles.base;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IHasHUDInfo {
    void renderHUDInfo(PoseStack poseStack, Window window);
}