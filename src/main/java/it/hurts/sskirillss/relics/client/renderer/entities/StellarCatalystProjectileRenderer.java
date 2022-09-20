package it.hurts.sskirillss.relics.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.entities.FallingStarEntity;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class StellarCatalystProjectileRenderer extends EntityRenderer<FallingStarEntity> {
    protected StellarCatalystProjectileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(FallingStarEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        float time = entityIn.tickCount + (Minecraft.getInstance().isPaused() ? 0 : partialTicks);

        RenderUtils.renderBeams(matrixStackIn, bufferIn, time, 50, time * 0.035F, new Color(255, 0, 255));
    }

    @Override
    public ResourceLocation getTextureLocation(FallingStarEntity entity) {
        return new ResourceLocation(Reference.MODID, "textures/item/stellar_catalyst.png");
    }

    public static class RenderFactory implements EntityRendererProvider {
        @Override
        public EntityRenderer<? super FallingStarEntity> create(Context manager) {
            return new StellarCatalystProjectileRenderer(manager);
        }
    }
}