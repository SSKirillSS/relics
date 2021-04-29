package it.hurts.sskirillss.relics.entities.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.entities.StellarCatalystProjectileEntity;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class StellarCatalystProjectileRenderer extends EntityRenderer<StellarCatalystProjectileEntity> {
    protected StellarCatalystProjectileRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(StellarCatalystProjectileEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        float time = entityIn.tickCount + (Minecraft.getInstance().isPaused() ? 0 : partialTicks);
        RenderUtils.renderBeams(matrixStackIn, bufferIn, time, 20, time * 0.035F, new Color(255, 0, 255));
    }

    @Override
    public ResourceLocation getTextureLocation(StellarCatalystProjectileEntity entity) {
        return new ResourceLocation(Reference.MODID, "textures/item/stellar_catalyst.png");
    }

    public static class RenderFactory implements IRenderFactory {
        @Override
        public EntityRenderer<? super StellarCatalystProjectileEntity> createRenderFor(EntityRendererManager manager) {
            return new StellarCatalystProjectileRenderer(manager);
        }
    }
}