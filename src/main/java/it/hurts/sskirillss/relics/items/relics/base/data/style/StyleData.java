package it.hurts.sskirillss.relics.items.relics.base.data.style;

import it.hurts.sskirillss.relics.items.relics.base.data.style.misc.Backgrounds;
import lombok.Builder;
import lombok.Data;
import net.minecraft.resources.ResourceLocation;

@Data
@Builder
public class StyleData {
    @Builder.Default
    private ResourceLocation background = Backgrounds.DEFAULT;

    @Builder.Default
    private TooltipData tooltip = TooltipData.builder().build();
}