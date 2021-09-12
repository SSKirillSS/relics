package it.hurts.sskirillss.relics.utils.tooltip;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RelicTooltip {
    private List<AbilityTooltip> abilities;
    private ItemStack stack;

    private RelicTooltip(Builder builder) {
        this.abilities = builder.abilities;
        this.stack = builder.stack;
    }

    public RelicTooltip() {

    }

    public List<AbilityTooltip> getAbilities() {
        return Collections.unmodifiableList(abilities);
    }

    public ItemStack getStack() {
        return stack;
    }

    public static class Builder {
        private final List<AbilityTooltip> abilities = new ArrayList<>();
        private ItemStack stack;

        public Builder(ItemStack stack) {
            this.stack = stack;
        }

        public Builder ability(AbilityTooltip ability) {
            abilities.add(ability);
            return this;
        }

        public RelicTooltip build() {
            return new RelicTooltip(this);
        }
    }
}