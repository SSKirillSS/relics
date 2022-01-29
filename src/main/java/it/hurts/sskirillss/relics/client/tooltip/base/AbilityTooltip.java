package it.hurts.sskirillss.relics.client.tooltip.base;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;
import net.minecraft.client.KeyMapping;

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

        public AbilityTooltipBuilder active(String key) {
            this.keybinding = key;

            return this;
        }

        public AbilityTooltipBuilder active() {
            this.keybinding = "Alt";

            return this;
        }

        public AbilityTooltipBuilder active(KeyMapping key) {
            this.keybinding = key.getKey().getDisplayName().getString();

            return this;
        }
    }
}