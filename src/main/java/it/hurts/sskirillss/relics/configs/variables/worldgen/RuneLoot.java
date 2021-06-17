package it.hurts.sskirillss.relics.configs.variables.worldgen;

import java.util.List;

public class RuneLoot {
    private final List<String> lootChests;
    private final float chance;

    public RuneLoot(List<String> lootChests, float chance) {
        this.lootChests = lootChests;
        this.chance = chance;
    }

    public List<String> getLootChests() {
        return lootChests;
    }

    public float getChance() {
        return chance;
    }
}