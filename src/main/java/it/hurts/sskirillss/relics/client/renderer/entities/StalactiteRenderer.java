package it.hurts.sskirillss.relics.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.client.models.entities.StalactiteModel;
import it.hurts.sskirillss.relics.entities.StalactiteEntity;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StalactiteRenderer extends EntityRenderer<StalactiteEntity> {
    public StalactiteRenderer(Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(StalactiteEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        float time = entityIn.tickCount + (Minecraft.getInstance().isPaused() ? 0 : partialTicks);

        matrixStackIn.pushPose();

        matrixStackIn.translate(0, 0.25, 0.25);

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot()) + 90.0F));

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(time * 10F));

        matrixStackIn.scale(0.35F, 0.35F, 0.35F);

        new StalactiteModel<>().renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutout(ResourceLocation.fromNamespaceAndPath(Reference.MODID,
                "textures/entities/stalactite.png"))), packedLightIn, OverlayTexture.NO_OVERLAY);

        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(StalactiteEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/entities/stalactite.png");
    }
}