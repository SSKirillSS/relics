package it.hurts.sskirillss.relics.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import it.hurts.sskirillss.relics.entities.RelicExperienceOrbEntity;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RelicExperienceOrbRenderer extends EntityRenderer<RelicExperienceOrbEntity> {
    public RelicExperienceOrbRenderer(EntityRendererProvider.Context context) {
        super(context);

        this.shadowRadius = 0F;
        this.shadowStrength = 0.75F;
    }

    @Override
    protected int getBlockLightLevel(RelicExperienceOrbEntity entity, BlockPos pos) {
        return Mth.clamp(super.getBlockLightLevel(entity, pos) + 7, 0, 15);
    }

    @Override
    public void render(RelicExperienceOrbEntity entity, float yaw, float pitch, PoseStack poseStack, MultiBufferSource buffer, int light) {
        this.shadowRadius = 0.075F + (entity.getStage() * 0.025F);

        poseStack.pushPose();

        VertexConsumer consumer = buffer.getBuffer(RenderType.itemEntityTranslucentCull(getTextureLocation(entity)));
        PoseStack.Pose pose = poseStack.last();

        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        float scale = (float) (0.5F + Math.sin(entity.tickCount * 0.1F) * 0.05F);

        poseStack.scale(scale, scale, scale);

        poseStack.translate(0.0F, 0.2F + (entity.getStage() * 0.05F), 0.0F);

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));

        int alpha = (int) Math.min(255, 255 * (0.75F + Math.sin(entity.tickCount * 0.25F) * 0.1F));

        vertex(consumer, matrix4f, matrix3f, -0.5F, -0.5F, alpha, 0, 1);
        vertex(consumer, matrix4f, matrix3f, 0.5F, -0.5F, alpha, 1, 1);
        vertex(consumer, matrix4f, matrix3f, 0.5F, 0.5F, alpha, 1, 0);
        vertex(consumer, matrix4f, matrix3f, -0.5F, 0.5F, alpha, 0, 0);

        poseStack.popPose();

        super.render(entity, yaw, pitch, poseStack, buffer, light);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, int alpha, float u, float v) {
        consumer.vertex(matrix4f, x, y, 0F).color(255, 255, 255, alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(matrix3f, 0F, 1F, 0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(RelicExperienceOrbEntity entity) {
        return new ResourceLocation(Reference.MODID, "textures/entities/experience/relics/relic_experience_" + entity.getStage() + ".png");
    }
}