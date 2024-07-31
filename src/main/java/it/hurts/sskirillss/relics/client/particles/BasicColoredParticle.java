package it.hurts.sskirillss.relics.client.particles;

import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.hurts.sskirillss.relics.init.ParticleRegistry;
import it.hurts.sskirillss.relics.utils.Reference;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Locale;

public class BasicColoredParticle extends TextureSheetParticle {
    private final Constructor constructor;

    public BasicColoredParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Constructor constructor) {
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

    @Data
    @Builder
    public static class Constructor {
        @Builder.Default
        private Color color;

        @Builder.Default
        private float diameter = 1F;

        @Builder.Default
        private float roll = 0F;

        @Builder.Default
        private boolean blended;

        @Nullable
        private Pair<Integer, Integer> blendFunc;

        @Builder.Default
        private boolean blurred = false;

        @Builder.Default
        private boolean physical = true;

        @Builder.Default
        private boolean depthTest = true;

        @Builder.Default
        private int lifetime = 20;

        @Builder.Default
        private float scaleModifier = 1F;

        public static class ConstructorBuilder {
            private Color color = new Color(0xFFFFFFFF, true);

            private boolean blended = false;

            public ConstructorBuilder color(int color) {
                this.color = new Color(color, true);

                return this;
            }

            public ConstructorBuilder color(float r, float g, float b, float a) {
                return this.color(new Color(r, g, b, a).getRGB());
            }

            public ConstructorBuilder color(float r, float g, float b) {
                return this.color(r, g, b, 1F);
            }

            public ConstructorBuilder color(int r, int g, int b, int a) {
                return this.color(r / 255F, g / 255F, b / 255F, a / 255F);
            }

            public ConstructorBuilder color(int r, int g, int b) {
                return this.color(r, g, b, 0xFF);
            }

            private ConstructorBuilder blendFunc(Pair<Integer, Integer> pair) {
                return this;
            }

            public ConstructorBuilder blended(boolean blended) {
                this.blended = blended;

                return this;
            }

            public ConstructorBuilder blended(int source, int dest) {
                this.blendFunc = Pair.of(source, dest);

                this.blended = true;

                return this;
            }
        }
    }

    public static class Options implements ParticleOptions {
        @Getter
        private final Constructor data;

        private Options(int color, float diameter, int lifetime, float roll) {
            this.data = Constructor.builder()
                    .color(color)
                    .diameter(diameter)
                    .lifetime(lifetime)
                    .roll(roll)
                    .build();
        }

        public Options(Constructor data) {
            this.data = data;
        }

        @Nonnull
        @Override
        public ParticleType<Options> getType() {
            return ParticleRegistry.BASIC_COLORED.get();
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buf) {
            buf.writeInt(data.getColor().getRGB());
            buf.writeFloat(data.getDiameter());
            buf.writeInt(data.getLifetime());
            buf.writeFloat(data.getRoll());
        }

        @Nonnull
        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %d %.2f %d %.2f",
                    ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()), data.getColor().getRGB(), data.getDiameter(), data.getLifetime(), data.getRoll());
        }

        public static final Codec<Options> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.INT.fieldOf("color").forGetter(options -> options.getData().getColor().getRGB()),
                        Codec.FLOAT.fieldOf("diameter").forGetter(options -> options.getData().getDiameter()),
                        Codec.INT.fieldOf("lifetime").forGetter(options -> options.getData().getLifetime()),
                        Codec.FLOAT.fieldOf("roll").forGetter(options -> options.getData().getRoll())
                ).apply(instance, Options::new));

        public static final ParticleOptions.Deserializer<Options> DESERIALIZER = new ParticleOptions.Deserializer<>() {
            @Nonnull
            @Override
            public Options fromCommand(@Nonnull ParticleType<Options> type, @Nonnull StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                int color = reader.readInt();

                reader.expect(' ');
                float diameter = reader.readFloat();

                reader.expect(' ');
                int lifetime = reader.readInt();

                reader.expect(' ');
                float roll = reader.readFloat();

                return new Options(color, diameter, lifetime, roll);
            }

            @Override
            public Options fromNetwork(@Nonnull ParticleType<Options> type, FriendlyByteBuf buf) {
                int color = buf.readInt();
                float diameter = buf.readFloat();
                int lifetime = buf.readInt();
                float roll = buf.readFloat();

                return new Options(color, diameter, lifetime, roll);
            }
        };
    }

    public static class Factory implements ParticleProvider<Options> {
        private final SpriteSet sprites;

        @Nullable
        @Override
        public Particle createParticle(Options options, ClientLevel world, double xPos, double yPos, double zPos, double xVelocity, double yVelocity, double zVelocity) {
            BasicColoredParticle particle = new BasicColoredParticle(world, xPos, yPos, zPos, xVelocity, yVelocity, zVelocity, options.getData());

            particle.pickSprite(sprites);

            return particle;
        }

        public Factory(SpriteSet sprite) {
            this.sprites = sprite;
        }

        public static class Type extends ParticleType<Options> {
            public Type() {
                super(false, Options.DESERIALIZER);
            }

            @Override
            public Codec<Options> codec() {
                return Options.CODEC;
            }
        }
    }
}