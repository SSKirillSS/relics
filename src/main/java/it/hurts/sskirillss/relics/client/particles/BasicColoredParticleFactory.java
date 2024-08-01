package it.hurts.sskirillss.relics.client.particles;

import com.mojang.serialization.Codec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class BasicColoredParticleFactory implements ParticleProvider<BasicColoredParticleOptions> {
    private final SpriteSet sprites;

    @Nullable
    @Override
    public Particle createParticle(BasicColoredParticleOptions options, ClientLevel world, double xPos, double yPos, double zPos, double xVelocity, double yVelocity, double zVelocity) {
        BasicColoredParticle particle = new BasicColoredParticle(world, xPos, yPos, zPos, xVelocity, yVelocity, zVelocity, options.getData());

        particle.pickSprite(sprites);

        return particle;
    }

    public BasicColoredParticleFactory(SpriteSet sprite) {
        this.sprites = sprite;
    }

    public static class Type extends ParticleType<BasicColoredParticleOptions> {
        public Type() {
            super(false, BasicColoredParticleOptions.DESERIALIZER);
        }

        @Override
        public Codec<BasicColoredParticleOptions> codec() {
            return BasicColoredParticleOptions.CODEC;
        }
    }
}