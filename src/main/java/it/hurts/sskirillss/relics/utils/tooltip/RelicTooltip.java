package it.hurts.sskirillss.relics.utils.tooltip;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.List;

@Builder
@Data
public class RelicTooltip {
    @Singular("alt")
    private List<AltTooltip> alt;

    @Singular("control")
    private List<ControlTooltip> control;

    @Singular("shift")
    private List<ShiftTooltip> shift;

    @Builder.Default
    private TooltipWindow window = null;

    @Builder
    @Data
    public static class TooltipWindow {
        private int topColor;
        private int bottomColor;

        @Accessors(fluent = true)
        boolean hasFrame;

        public static class TooltipWindowBuilder {
            public TooltipWindowBuilder hasFrame() {
                hasFrame = true;

                return this;
            }
        }
    }
}