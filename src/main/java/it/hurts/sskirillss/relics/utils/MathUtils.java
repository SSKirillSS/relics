package it.hurts.sskirillss.relics.utils;

import java.util.Random;

public class MathUtils {
    public static float randomFloat(Random random) {
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
}