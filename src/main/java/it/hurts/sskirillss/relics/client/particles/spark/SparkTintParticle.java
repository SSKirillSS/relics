package it.hurts.sskirillss.relics.client.particles.spark;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;
import java.awt.*;

public class SparkTintParticle extends TextureSheetParticle {
    public SparkTintParticle(ClientLevel world, double x, double y, double z, double velocityX,
                             double velocityY, double velocityZ, Color color, float diameter,
                             int lifeTime) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        setColor(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);
        setSize(diameter, diameter);

        lifetime = lifeTime;

        RandomSource random = world.getRandom();
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
    public ParticleRenderType getRenderType() {
        return RENDERER;
    }

    @Override
    public void tick() {
        this.quadSize *= 0.95F;

        xo = x;
        yo = y;
        zo = z;

        move(xd, yd, zd);

        if (this.age + (this.age * 0.1) > lifetime)
            this.alpha -= 0.025F;

        if (this.age++ >= this.lifetime)
            this.remove();
    }

    private static final ParticleRenderType RENDERER = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);

            RenderSystem.depthMask(false);

            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();
            RenderSystem.enableDepthTest();
            Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_PARTICLES).restoreLastBlurMipmap();
            RenderSystem.depthMask(true);
        }

        @Override
        public String toString() {
            return Reference.MODID + ":" + "spark_tint";
        }
    };
}