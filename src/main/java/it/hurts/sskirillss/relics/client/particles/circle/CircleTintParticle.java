package it.hurts.sskirillss.relics.client.particles.circle;

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

        lifetime = lifeTime;

        quadSize = diameter;
        this.alpha = 1.0F;

        xd = velocityX;
        yd = velocityY;
        zd = velocityZ;

        this.hasPhysics = shouldCollide;
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
        this.quadSize *= this.resizeSpeed;

        xo = x;
        yo = y;
        zo = z;

        move(xd, yd, zd);

        if (onGround) {
            this.remove();
        }

        if (yo == y && yd > 0) {
            this.remove();
        }

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    private static final IParticleRenderType RENDERER = new IParticleRenderType() {
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.001F);
            RenderSystem.disableLighting();

            textureManager.bind(AtlasTexture.LOCATION_PARTICLES);
            textureManager.getTexture(AtlasTexture.LOCATION_PARTICLES).setBlurMipmap(true, false);
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE);
        }

        @Override
        public void end(Tessellator tessellator) {
            tessellator.end();
            RenderSystem.enableDepthTest();
            Minecraft.getInstance().textureManager.getTexture(AtlasTexture.LOCATION_PARTICLES).restoreLastBlurMipmap();
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