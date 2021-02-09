package it.hurts.sskirillss.relics.utils;

import java.util.Random;

public class MathUtils {
    public static float generateReallyRandomFloat() {
        float randomFloat = new Random().nextFloat();
        return !new Random().nextBoolean() ? randomFloat * -1 : randomFloat;
    }
}