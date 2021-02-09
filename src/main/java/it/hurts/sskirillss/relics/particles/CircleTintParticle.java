package it.hurts.sskirillss.relics.particles;

import com.mojang.blaze3d.systems.RenderSystem;
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

public class CircleTintParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite sprites;
    float resizeSpeed;

    public CircleTintParticle(ClientWorld world, double x, double y, double z, double velocityX,
                              double velocityY, double velocityZ, Color spark, float diameter,
                              int lifeTime, float resizeSpeed, boolean shouldCollide, IAnimatedSprite sprites) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.sprites = sprites;
        this.resizeSpeed = resizeSpeed;
        setColor(spark.getRed() / 255.0F, spark.getGreen() / 255.0F, spark.getBlue() / 255.0F);
        setSize(diameter, diameter);

        maxAge = lifeTime;

        particleScale = diameter;
        this.particleAlpha = 1.0F;

        motionX = velocityX;
        motionY = velocityY;
        motionZ = velocityZ;

        this.canCollide = shouldCollide;
    }

    @Override
    public float getScale(float scaleFactor) {
        return this.particleScale;
    }

    @Override
    protected int getBrightnessForRender(float partialTick) {
        return LightTexture.packLight(15, 15);
    }

    @Nonnull
    @Override
    public IParticleRenderType getRenderType() {
        return RENDERER;
    }

    @Override
    public void tick() {
        this.particleScale *= this.resizeSpeed;

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        move(motionX, motionY, motionZ);

        if (onGround) {
            this.setExpired();
        }

        if (prevPosY == posY && motionY > 0) {
            this.setExpired();
        }

        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }
    }

    private static final IParticleRenderType RENDERER = new IParticleRenderType() {
        @Override
        public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.001F);
            RenderSystem.disableLighting();

            textureManager.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
            textureManager.getTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE).setBlurMipmap(true, false);
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }

        @Override
        public void finishRender(Tessellator tessellator) {
            tessellator.draw();
            RenderSystem.enableDepthTest();
            Minecraft.getInstance().textureManager.getTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE).restoreLastBlurMipmap();
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
        }

        @Override
        public String toString() {
            return Reference.MODID + ":" + "circle_tint";
        }
    };
}