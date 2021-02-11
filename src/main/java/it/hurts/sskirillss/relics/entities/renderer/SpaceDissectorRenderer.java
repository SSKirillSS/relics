package it.hurts.sskirillss.relics.entities.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.entities.SpaceDissectorEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class SpaceDissectorRenderer extends EntityRenderer<SpaceDissectorEntity> {
    protected SpaceDissectorRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(SpaceDissectorEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        float time = entityIn.ticksExisted + (Minecraft.getInstance().isGamePaused() ? 0 : partialTicks);

        matrixStackIn.push();

        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 90.0F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch)));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(time * 40F));
        matrixStackIn.scale(0.75F, 0.75F, 0.75F);
        Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(ItemRegistry.SPACE_DISSECTOR.get()),
                ItemCameraTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);

        matrixStackIn.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(SpaceDissectorEntity entity) {
        return new ResourceLocation(Reference.MODID, "textures/item/space_dissector.png");
    }

    public static class RenderFactory implements IRenderFactory {
        @Override
        public EntityRenderer<? super SpaceDissectorEntity> createRenderFor(EntityRendererManager manager) {
            return new SpaceDissectorRenderer(manager);
        }
    }
}