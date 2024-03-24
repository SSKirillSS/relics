package it.hurts.sskirillss.relics.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import it.hurts.sskirillss.relics.client.models.entities.DissectionModel;
import it.hurts.sskirillss.relics.entities.DissectionEntity;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DissectionRenderer extends EntityRenderer<DissectionEntity> {
    public DissectionRenderer(Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(DissectionEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (entityIn.tickCount < 5)
            return;

        matrixStackIn.pushPose();

        matrixStackIn.translate(0, 1.5, 0);

        float ticks = (Minecraft.getInstance().isPaused() ? 0 : partialTicks);
        float scale = Mth.clamp((entityIn.getLifeTime() > 20F ? (entityIn.tickCount - 5F + ticks) * 0.075F : (entityIn.getLifeTime() - ticks) * 0.075F), 0F, 1F) + (Math.abs(Mth.sin((entityIn.tickCount + ticks) * 0.01F)) * 0.5F);

        matrixStackIn.scale(scale, scale, scale);

        Vec3 angle = entityIn.getLookAngle();

        double angleY = Math.toDegrees(Math.atan2(angle.x, angle.z));
        double angleZ = Math.toDegrees(Math.atan2(Math.sqrt(angle.x * angle.x + angle.z * angle.z), angle.y));

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float) angleY));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees((float) angleZ + 90F));

        new DissectionModel<>().renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutout(new ResourceLocation(Reference.MODID,
                "textures/entities/dissection.png"))), packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DissectionEntity entity) {
        return new ResourceLocation(Reference.MODID, "textures/entities/dissection.png");
    }
}