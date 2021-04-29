package it.hurts.sskirillss.relics.entities.renderer;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class NullRenderer<T extends Entity> extends EntityRenderer<T> {
    public NullRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public boolean shouldRender(T entity, @Nonnull ClippingHelper clipping, double x, double y, double z) {
        return false;
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull T entity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }
}