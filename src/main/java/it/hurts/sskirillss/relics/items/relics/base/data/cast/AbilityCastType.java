package it.hurts.sskirillss.relics.items.relics.base.data.cast;

public enum AbilityCastType {
    NONE,
    INSTANTANEOUS,
    INTERRUPTIBLE,
    TOGGLEABLE;

    public static AbilityCastType getByName(String name) {
        return AbilityCastType.valueOf(name.toUpperCase());
    }
}
