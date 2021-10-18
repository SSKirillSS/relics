package it.hurts.sskirillss.relics.utils.tooltip;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;
import net.minecraft.client.settings.KeyBinding;

import java.util.List;

@Builder
@Data
public class ShiftTooltip {
    @Singular("arg")
    private final List<Object> args;

    @Accessors(fluent = true)
    private final boolean isActive;

    private final String keybinding;

    @Accessors(fluent = true)
    private final boolean isNegative;

    public static class ShiftTooltipBuilder {
        public ShiftTooltipBuilder negative() {
            this.isNegative = true;

            return this;
        }

        public ShiftTooltipBuilder active(String keybinding) {
            this.isActive = true;
            this.keybinding = keybinding;

            return this;
        }

        public ShiftTooltipBuilder active() {
            this.isActive = true;
            this.keybinding = "";

            return this;
        }

        public ShiftTooltipBuilder active(KeyBinding keybinding) {
            this.isActive = true;
            this.keybinding = keybinding.getKey().getDisplayName().getString();

            return this;
        }
    }
}