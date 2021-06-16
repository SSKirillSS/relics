package it.hurts.sskirillss.relics.utils;

import net.minecraft.util.text.*;
import net.minecraft.util.text.Color;

public class TooltipUtils {
    public static StringTextComponent drawProgressBar(float percentage, String style, String startHEX, String middleHEX, String endHEX, String neutralHEX, boolean withPercents) {
        StringBuilder string = new StringBuilder(style);
        int offset = (int) Math.min(100, Math.floor(string.length() * percentage / 100));
        Color color = Color.parseColor(percentage > 33.3 ? percentage > 66.6 ? endHEX : middleHEX : startHEX);
        StringTextComponent component = new StringTextComponent("");
        component.append(new StringTextComponent(string.substring(0, offset)).setStyle(Style.EMPTY.withColor(color)));
        component.append(new StringTextComponent(string.substring(offset, string.length())).setStyle(Style.EMPTY
                .withColor(Color.parseColor(neutralHEX))));
        if (withPercents) component.append(new StringTextComponent(" " + Math.round(percentage * 10.0F) / 10.0F + "%").setStyle(Style.EMPTY.withColor(color)));
        return component;
    }
}