package it.hurts.sskirillss.relics.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.client.models.entities.SporeModel;
import it.hurts.sskirillss.relics.entities.SporeEntity;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SporeRenderer extends EntityRenderer<SporeEntity> {
    protected SporeRenderer(Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(SporeEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        float time = entityIn.tickCount + (Minecraft.getInstance().isPaused() ? 0 : partialTicks);

        matrixStackIn.pushPose();

        matrixStackIn.translate(0, ((Math.pow(Math.log10(1 + entityIn.getSize()), 1D / 3D))) / 4F, 0);

        if (!entityIn.isStuck()) {
            float speed = 15F;

            matrixStackIn.mulPose(Axis.YP.rotationDegrees(time * speed));
            matrixStackIn.mulPose(Axis.XN.rotationDegrees(time * speed));
        }

        ItemStack stack = entityIn.getStack();

        if (stack.getItem() instanceof IRelicItem relic) {
            double inlinedSize = Math.pow(Math.log10(1 + entityIn.getSize()), 1D / 3D);

            int maxLifetime = (int) Math.round(relic.getAbilityValue(entityIn.getStack(), "spore", "duration") * 20);
            int lifetime = entityIn.getLifetime();

            float scale = (float) (inlinedSize + (Math.abs(Math.sin((entityIn.tickCount + (Minecraft.getInstance().isPaused() ? 0 : partialTicks)) * 0.2F)) * 0.05F)
                    + (lifetime >= maxLifetime - 20 ? (float) ((20 - (maxLifetime - lifetime) + (Minecraft.getInstance().isPaused() ? 0 : partialTicks)) * (inlinedSize * 0.035F)) : 0F));

            matrixStackIn.scale(scale, scale, scale);
        }

        new SporeModel<>().renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutout(new ResourceLocation(Reference.MODID,
                "textures/entities/spore.png"))), packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SporeEntity entity) {
        return new ResourceLocation(Reference.MODID, "textures/entities/spore.png");
    }

    public static class RenderFactory implements EntityRendererProvider {
        @Override
        public EntityRenderer<? super SporeEntity> create(Context manager) {
            return new SporeRenderer(manager);
        }
    }
}