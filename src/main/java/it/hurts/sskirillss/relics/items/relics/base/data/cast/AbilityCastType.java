package it.hurts.sskirillss.relics.items.relics.base.data.cast;

public enum AbilityCastType {
    NONE,
    INSTANTANEOUS,
    INTERRUPTIBLE,
    CYCLICAL,
    TOGGLEABLE,
    CHARGEABLE;

    public static AbilityCastType getByName(String name) {
        return AbilityCastType.valueOf(name.toUpperCase());
    }
}
