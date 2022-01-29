package it.hurts.sskirillss.relics.client.particles.spark;

import com.mojang.serialization.Codec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;

import javax.annotation.Nullable;

public class SparkTintFactory implements ParticleProvider<SparkTintData> {
    private final SpriteSet sprites;

    @Nullable
    @Override
    public Particle createParticle(SparkTintData data, ClientLevel world, double xPos, double yPos, double zPos, double xVelocity, double yVelocity, double zVelocity) {
        SparkTintParticle particle = new SparkTintParticle(world, xPos, yPos, zPos, xVelocity, yVelocity, zVelocity, data.getTint(), data.getDiameter(),
                data.getLifeTime());
        particle.pickSprite(sprites);
        return particle;
    }

    public SparkTintFactory(SpriteSet sprite) {
        this.sprites = sprite;
    }

    public static class SparkTintType extends ParticleType<SparkTintData> {
        public SparkTintType() {
            super(false, SparkTintData.DESERIALIZER);
        }

        @Override
        public Codec<SparkTintData> codec() {
            return SparkTintData.CODEC;
        }
    }
}