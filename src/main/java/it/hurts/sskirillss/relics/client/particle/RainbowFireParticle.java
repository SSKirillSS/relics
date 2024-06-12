package it.hurts.sskirillss.relics.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import org.antlr.v4.runtime.misc.NotNull;

public class RainbowFireParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    private static final ParticleRenderType RENDERER = new ParticleRenderType() {
        public void begin(BufferBuilder buffer, @NotNull TextureManager manager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 1);
            RenderSystem.depthMask(false);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
        }

        public String toString() {
            return "relics:rainbow_fire";
        }
    };

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        return 70;
    }

    protected RainbowFireParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.sprites = sprites;
        this.quadSize = 2;
        this.lifetime = 1;
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        this.setSpriteFromAge(sprites);
        super.render(pBuffer, pRenderInfo, pPartialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDERER;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public RainbowFireParticle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
            return new RainbowFireParticle(world, x, y, z, xd, yd, zd, spriteSet);
        }
    }
}
