package it.hurts.sskirillss.relics.configs.variables;

import it.hurts.sskirillss.relics.configs.variables.crafting.RuneIngredients;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RuneLoot;

public class RuneConfig {
    private final RuneIngredients ingredients;
    private final RuneLoot loot;

    public RuneConfig(RuneIngredients ingredients, RuneLoot loot) {
        this.ingredients = ingredients;
        this.loot = loot;
    }

    public RuneIngredients getIngredients() {
        return ingredients;
    }

    public RuneLoot getLoot() {
        return loot;
    }
}