package it.hurts.sskirillss.relics.utils;

import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ParticleUtils {
    public static void createBall(IParticleData particle, Vector3d vec, World world, int size, float speed) {
        if (world.isRemote()) {
            for (int i = -size; i <= size; ++i) {
                for (int j = -size; j <= size; ++j) {
                    for (int k = -size; k <= size; ++k) {
                        double d3 = (double) j + (world.rand.nextDouble() - world.rand.nextDouble()) * 0.5D;
                        double d4 = (double) i + (world.rand.nextDouble() - world.rand.nextDouble()) * 0.5D;
                        double d5 = (double) k + (world.rand.nextDouble() - world.rand.nextDouble()) * 0.5D;
                        double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / speed + world.rand.nextGaussian() * 0.05D;
                        world.addParticle(particle,
                                vec.getX(), vec.getY(), vec.getZ(),
                                d3 / d6, d4 / d6, d5 / d6);
                        if (i != -size && i != size && j != -size && j != size) {
                            k += size * 2 - 1;
                        }
                    }
                }
            }
        }
    }
}