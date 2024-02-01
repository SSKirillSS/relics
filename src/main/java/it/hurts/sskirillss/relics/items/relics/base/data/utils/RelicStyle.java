package it.hurts.sskirillss.relics.items.relics.base.data.utils;

import java.util.Locale;

public enum RelicStyle {
    DEFAULT,
    AQUATIC;

    public String getID() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}