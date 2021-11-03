package it.hurts.sskirillss.relics.client.particles.spark;

import com.mojang.serialization.Codec;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleType;

import javax.annotation.Nullable;

public class SparkTintFactory implements IParticleFactory<SparkTintData> {
    private final IAnimatedSprite sprites;

    @Nullable
    @Override
    public Particle createParticle(SparkTintData data, ClientWorld world, double xPos, double yPos, double zPos, double xVelocity, double yVelocity, double zVelocity) {
        SparkTintParticle particle = new SparkTintParticle(world, xPos, yPos, zPos, xVelocity, yVelocity, zVelocity, data.getTint(), data.getDiameter(),
                data.getLifeTime());
        particle.pickSprite(sprites);
        return particle;
    }

    public SparkTintFactory(IAnimatedSprite sprite) {
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