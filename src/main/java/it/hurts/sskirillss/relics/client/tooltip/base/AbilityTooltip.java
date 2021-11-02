package it.hurts.sskirillss.relics.client.tooltip.base;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;
import net.minecraft.client.settings.KeyBinding;

import java.util.List;

@Builder
@Data
public class AbilityTooltip {
    @Singular("arg")
    private final List<Object> args;

    private final String keybinding;

    @Accessors(fluent = true)
    private final boolean isNegative;

    public static class AbilityTooltipBuilder {
        public AbilityTooltipBuilder negative() {
            this.isNegative = true;

            return this;
        }

        public AbilityTooltipBuilder active(String keybinding) {
            this.keybinding = keybinding;

            return this;
        }

        public AbilityTooltipBuilder active() {
            this.keybinding = "Alt";

            return this;
        }

        public AbilityTooltipBuilder active(KeyBinding keybinding) {
            this.keybinding = keybinding.getKey().getDisplayName().getString();

            return this;
        }
    }
}