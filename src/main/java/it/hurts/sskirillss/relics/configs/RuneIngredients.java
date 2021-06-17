package it.hurts.sskirillss.relics.configs;

import java.util.List;

public class RuneIngredients {
    private final List<String> ingredients;

    public RuneIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}