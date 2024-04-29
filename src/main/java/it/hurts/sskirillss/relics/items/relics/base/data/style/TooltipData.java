package it.hurts.sskirillss.relics.items.relics.base.data.style;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TooltipData {
    @Builder.Default
    private int borderTop = -1;
    @Builder.Default
    private int borderBottom = -1;

    @Builder.Default
    private int backgroundTop = -1;
    @Builder.Default
    private int backgroundBottom = -1;

    @Builder.Default
    @Deprecated(forRemoval = true)
    private boolean textured = false;

    @Builder.Default
    @Deprecated(forRemoval = true)
    private String icon = "";
}