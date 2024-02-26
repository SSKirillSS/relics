package it.hurts.sskirillss.relics.config.data;

import it.hurts.sskirillss.relics.items.relics.base.data.utils.RelicStyle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StyleConfigData {
    private RelicStyle style = RelicStyle.DEFAULT;
}