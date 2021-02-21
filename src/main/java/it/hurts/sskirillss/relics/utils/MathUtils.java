package it.hurts.sskirillss.relics.utils;

import java.util.Random;

public class MathUtils {
    public static float generateReallyRandomFloat(Random random) {
        float randomFloat = random.nextFloat();
        return !random.nextBoolean() ? randomFloat * -1 : randomFloat;
    }
}