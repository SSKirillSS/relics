package it.hurts.sskirillss.relics.items.relics.base.data.cast;

public enum AbilityCastStage {
    START,
    TICK,
    END;

    public static AbilityCastStage getByName(String name) {
        return AbilityCastStage.valueOf(name.toUpperCase());
    }
}