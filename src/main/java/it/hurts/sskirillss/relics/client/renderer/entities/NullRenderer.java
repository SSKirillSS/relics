package it.hurts.sskirillss.relics.client.renderer.entities;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class NullRenderer<T extends Entity> extends EntityRenderer<T> {
    public NullRenderer(EntityRendererProvider.Context manager) {
        super(manager);
    }

    @Override
    public boolean shouldRender(T entity, @Nonnull Frustum clipping, double x, double y, double z) {
        return true;
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}