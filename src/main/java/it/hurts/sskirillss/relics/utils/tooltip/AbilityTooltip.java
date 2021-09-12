package it.hurts.sskirillss.relics.utils.tooltip;

import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbilityTooltip {
    private final Object[] varArgs;
    private final boolean isActive;
    private final String keybind;
    private final boolean isNegative;

    public AbilityTooltip(Builder builder) {
        this.varArgs = builder.varArgs.toArray(new Object[0]);
        this.isActive = builder.isActive;
        this.keybind = builder.keybind;
        this.isNegative = builder.isNegative;
    }

    public Object[] getVarArgs() {
        return varArgs;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getKeybind() {
        return keybind;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public static class Builder {
        private final List<Object> varArgs = new ArrayList<>();
        private boolean isActive = false;
        private String keybind;
        private boolean isNegative = false;

        public Builder varArg(Object arg) {
            varArgs.add(arg);
            return this;
        }

        public Builder varArgs(Object... args) {
            varArgs.addAll(Arrays.asList(args));
            return this;
        }

        public Builder active() {
            this.isActive = true;
            this.keybind = "";
            return this;
        }

        public Builder active(String keybind) {
            this.isActive = true;
            this.keybind = keybind;
            return this;
        }

        public Builder active(KeyBinding keybind) {
            this.isActive = true;
            this.keybind = keybind.getKey().getDisplayName().getString();
            return this;
        }

        public Builder negative() {
            this.isNegative = true;
            return this;
        }

        public AbilityTooltip build() {
            return new AbilityTooltip(this);
        }
    }
}