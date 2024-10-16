package it.hurts.sskirillss.relics.utils;

import net.minecraft.util.RandomSource;

import java.util.Random;

public class MathUtils {
    public static float randomFloat(RandomSource random) {
        return -1 + 2 * random.nextFloat();
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(max, Math.min(value, min));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(max, Math.min(min, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(max, Math.min(value, min));
    }

    public static float randomBetween(Random random, float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    public static double randomBetween(Random random, double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    public static int randomBetween(Random random, int min, int max) {
        return (int) Math.round(randomBetween(random, (double) min, (double) max));
    }

    public static double round(double value, int steps) {
        double multiplier = Math.pow(10, steps);

        return Math.round(value * multiplier) / multiplier;
    }

    public static int multicast(RandomSource random, double chance, double chanceMultiplier) {
        return random.nextDouble() <= chance ? multicast(random, chance * chanceMultiplier, chanceMultiplier) + 1 : 0;
    }
}