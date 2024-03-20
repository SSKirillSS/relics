package it.hurts.sskirillss.relics.config.data;

import it.hurts.sskirillss.relics.items.relics.base.data.style.misc.Backgrounds;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StyleConfigData {
    private ResourceLocation background = Backgrounds.DEFAULT;
}