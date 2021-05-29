package it.hurts.sskirillss.relics.particles.spark;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Random;

public class SparkTintParticle extends SpriteTexturedParticle {
    public SparkTintParticle(ClientWorld world, double x, double y, double z, double velocityX,
                             double velocityY, double velocityZ, Color color, float diameter,
                             int lifeTime) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        setColor(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);
        setSize(diameter, diameter);

        lifetime = lifeTime;

        Random random = world.getRandom();
        xd = velocityX + MathUtils.randomFloat(random) * 0.01F;
        yd = velocityY + random.nextFloat() * 0.05F;
        zd = velocityZ + MathUtils.randomFloat(random) * 0.01F;

        quadSize = diameter;
        this.alpha = 1.0F;

        this.hasPhysics = true;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return this.quadSize;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.pack(15, 15);
    }

    @Nonnull
    @Override
    public IParticleRenderType getRenderType() {
        return RENDERER;
    }

    @Override
    public void tick() {
        this.quadSize *= 0.95F;

        xo = x;
        yo = y;
        zo = z;

        move(xd, yd, zd);

        if (this.age + (this.age * 0.1) > lifetime) this.alpha -= 0.025F;

        if (this.age++ >= this.lifetime) this.remove();
    }

    private static final IParticleRenderType RENDERER = new IParticleRenderType() {
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.disableLighting();
            textureManager.bind(AtlasTexture.LOCATION_PARTICLES);
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE);
        }

        @Override
        public void end(Tessellator tessellator) {
            tessellator.end();
            RenderSystem.enableDepthTest();
            Minecraft.getInstance().textureManager.getTexture(AtlasTexture.LOCATION_PARTICLES).restoreLastBlurMipmap();
            RenderSystem.depthMask(true);
        }

        @Override
        public String toString() {
            return Reference.MODID + ":" + "spark_tint";
        }
    };
}