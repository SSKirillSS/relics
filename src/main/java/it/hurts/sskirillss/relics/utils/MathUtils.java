package it.hurts.sskirillss.relics.utils;

import java.util.Random;

public class MathUtils {
    public static float generateReallyRandomFloat(Random random) {
        return -1 + 2 * random.nextFloat();
    }
}