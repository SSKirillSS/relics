package it.hurts.sskirillss.relics.client.models.items.utils;

import java.util.Locale;

public enum ModelSide {
    RIGHT,
    LEFT;

    public ModelSide getOpposite() {
        return this == RIGHT ? LEFT : RIGHT;
    }

    public String getId() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}