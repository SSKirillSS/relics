package it.hurts.sskirillss.relics.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import it.hurts.sskirillss.relics.client.models.entities.SolidSnowballModel;
import it.hurts.sskirillss.relics.entities.SolidSnowballEntity;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SolidSnowballRenderer extends EntityRenderer<SolidSnowballEntity> {
    public SolidSnowballRenderer(Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(SolidSnowballEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        float time = entityIn.tickCount + (Minecraft.getInstance().isPaused() ? 0 : partialTicks);

        matrixStackIn.pushPose();

        matrixStackIn.translate(0F, 0.25F, 0F);

        float speed = 20F;

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(time * speed));
        matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(time * speed));

        float scale = 0.1F + entityIn.getSize() * 0.009F;

        matrixStackIn.scale(scale, scale, scale);

        new SolidSnowballModel<>().renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutout(new ResourceLocation(Reference.MODID,
                "textures/entities/solid_snowball.png"))), packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SolidSnowballEntity entity) {
        return new ResourceLocation(Reference.MODID, "textures/entities/solid_snowball.png");
    }
}