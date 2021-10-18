package it.hurts.sskirillss.relics.utils.tooltip;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

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
}