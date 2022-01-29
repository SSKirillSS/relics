package it.hurts.sskirillss.relics.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import it.hurts.sskirillss.relics.entities.SpaceDissectorEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpaceDissectorRenderer extends EntityRenderer<SpaceDissectorEntity> {
    protected SpaceDissectorRenderer(Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(SpaceDissectorEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        float time = entityIn.tickCount + (Minecraft.getInstance().isPaused() ? 0 : partialTicks);

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.getYRot(), entityIn.getYRot()) - 90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.getXRot(), entityIn.getXRot())));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(time * 40F));
        matrixStackIn.scale(0.75F, 0.75F, 0.75F);

        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(ItemRegistry.SPACE_DISSECTOR.get()),
                ItemTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 0);

        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SpaceDissectorEntity entity) {
        return new ResourceLocation(Reference.MODID, "textures/item/space_dissector.png");
    }

    public static class RenderFactory implements EntityRendererProvider {
        @Override
        public EntityRenderer<? super SpaceDissectorEntity> create(Context manager) {
            return new SpaceDissectorRenderer(manager);
        }
    }
}