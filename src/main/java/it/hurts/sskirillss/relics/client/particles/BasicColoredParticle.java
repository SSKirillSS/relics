package it.hurts.sskirillss.relics.client.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BasicColoredParticle extends TextureSheetParticle {
    private final BasicColoredParticleConstructor constructor;

    public BasicColoredParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BasicColoredParticleConstructor constructor) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        setColor(constructor.getColor().getRed() / 255F, constructor.getColor().getGreen() / 255F, constructor.getColor().getBlue() / 255F);
        setSize(constructor.getDiameter(), constructor.getDiameter());
        setAlpha(constructor.getColor().getAlpha() / 255F);
        setLifetime(constructor.getLifetime());

        this.constructor = constructor;

        this.quadSize = constructor.getDiameter();
        this.hasPhysics = constructor.isPhysical();

        this.xd = velocityX;
        this.yd = velocityY;
        this.zd = velocityZ;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 vec3 = camera.getPosition();

        float f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());

        Quaternionf quaternionf = new Quaternionf(camera.rotation());

        quaternionf.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f3 = this.getQuadSize(partialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();

        int j = this.getLightColor(partialTicks);

        buffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    @Override
    public void tick() {
        this.quadSize *= constructor.getScaleModifier();

        xo = x;
        yo = y;
        zo = z;

        oRoll = roll;
        roll += constructor.getRoll();

        move(xd, yd, zd);

        if (this.age++ >= this.lifetime)
            this.remove();
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Nonnull
    @Override
    public ParticleRenderType getRenderType() {
        return RENDERER;
    }

    private static final ParticleRenderType RENDERER = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder buffer, @NotNull TextureManager manager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);

            RenderSystem.enableBlend();

            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            RenderSystem.depthMask(false);

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();

            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
        }

        @Override
        public String toString() {
            return Reference.MODID + ":" + "basic_colored";
        }
    };
}