package it.hurts.sskirillss.relics.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.client.models.entities.ShadowSawModel;
import it.hurts.sskirillss.relics.entities.ShadowSawEntity;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShadowSawRenderer extends EntityRenderer<ShadowSawEntity> {
    protected ShadowSawRenderer(Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(ShadowSawEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        float time = entityIn.tickCount + (Minecraft.getInstance().isPaused() ? 0 : partialTicks);

        matrixStackIn.pushPose();

        matrixStackIn.translate(0, 1, 0);

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(time * 30F));

        matrixStackIn.mulPose(Axis.ZP.rotation((float) (Math.sin(time) * 0.3F) * 0.1F));

        matrixStackIn.translate(0, Math.sin(time * 0.1F) * 0.05F, 0);

        matrixStackIn.scale(1.5F, -1F, 1.5F);

        new ShadowSawModel<>().renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(new ResourceLocation(Reference.MODID,
                "textures/entities/shadow_saw.png"))), packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ShadowSawEntity entity) {
        return new ResourceLocation(Reference.MODID, "textures/entities/shadow_saw.png");
    }

    public static class RenderFactory implements EntityRendererProvider {
        @Override
        public EntityRenderer<? super ShadowSawEntity> create(Context manager) {
            return new ShadowSawRenderer(manager);
        }
    }
}