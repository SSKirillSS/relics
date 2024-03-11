package it.hurts.sskirillss.relics.utils;

import it.hurts.sskirillss.octolib.particle.BasicColoredParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ParticleUtils {
    public static BasicColoredParticle.Options constructSimpleSpark(Color color, float diameter, int lifetime, float scaleModifier) {
        return new BasicColoredParticle.Options(BasicColoredParticle.Constructor.builder()
                .color(color.getRGB())
                .diameter(diameter)
                .lifetime(lifetime)
                .scaleModifier(scaleModifier)
                .physical(true)
                .blurred(false)
                .blended(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
                .depthTest(true)
                .roll(0.5F)
                .build());
    }

    public static void createBall(ParticleOptions particle, Vec3 vec, Level world, int size, float speed) {
        if (!world.isClientSide())
            return;

        for (int i = -size; i <= size; ++i) {
            for (int j = -size; j <= size; ++j) {
                for (int k = -size; k <= size; ++k) {
                    double d3 = (double) j + (world.random.nextDouble() - world.random.nextDouble()) * 0.5D;
                    double d4 = (double) i + (world.random.nextDouble() - world.random.nextDouble()) * 0.5D;
                    double d5 = (double) k + (world.random.nextDouble() - world.random.nextDouble()) * 0.5D;
                    double d6 = (double) Mth.sqrt((float) (d3 * d3 + d4 * d4 + d5 * d5)) / speed + world.random.nextGaussian() * 0.05D;

                    world.addParticle(particle, vec.x(), vec.y(), vec.z(),
                            d3 / d6, d4 / d6, d5 / d6);

                    if (i != -size && i != size && j != -size && j != size)
                        k += size * 2 - 1;
                }
            }
        }
    }

    public static void createCyl(ParticleOptions particle, Vec3 center, Level level, double radius, float step) {
        int offset = 16;

        double len = (float) (2 * Math.PI * radius);
        int num = (int) (len / step);

        for (int i = 0; i < num; i++) {
            double angle = Math.toRadians(((360F / num) * i) + (360F * ((((len / step) - num) / num) / len)));

            double extraX = (radius * Math.sin(angle)) + center.x();
            double extraZ = (radius * Math.cos(angle)) + center.z();
            double extraY = center.y() + 0.5F;

            boolean foundPos = false;

            int tries;

            for (tries = 0; tries < offset * 2; tries++) {
                Vec3 vec = new Vec3(extraX, extraY, extraZ);
                BlockPos pos = new BlockPos(vec);

                BlockState state = level.getBlockState(pos);
                VoxelShape shape = state.getCollisionShape(level, pos);

                if (state.getBlock() instanceof LiquidBlock liquid) {
                    AABB aabb = new AABB(pos);

                    aabb.inflate(-0.5);

                    shape = Shapes.block();
                }

                if (shape.isEmpty()) {
                    if (!foundPos) {
                        extraY -= 1;

                        continue;
                    }
                } else
                    foundPos = true;

                if (shape.isEmpty())
                    break;

                AABB aabb = shape.bounds();

                if (!aabb.move(pos).contains(vec)) {
                    if (aabb.maxY >= 1F) {
                        extraY += 1;

                        continue;
                    }

                    break;
                }

                extraY += step;
            }

            if (tries < offset * 2)
                level.addParticle(particle, extraX, extraY + 0.1F, extraZ, 0, 0, 0);
        }
    }

    public static void createLine(ParticleOptions particle, Level level, Vec3 start, Vec3 end, int amount, Vec3 motion) {
        Vec3 delta = end.subtract(start);
        Vec3 dir = delta.normalize();

        for (int i = 0; i < amount; ++i) {
            double progress = i * delta.length() / amount;

            level.addParticle(particle, start.x + dir.x * progress, start.y + dir.y * progress,
                    start.z + dir.z * progress, motion.x, motion.y, motion.z);
        }
    }

    public static void createLine(ParticleOptions particle, Level level, Vec3 start, Vec3 end, int amount) {
        createLine(particle, level, start, end, amount, Vec3.ZERO);
    }
}