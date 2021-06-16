package it.hurts.sskirillss.relics.utils;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.util.text.Color;

import java.util.List;

public class TooltipUtils {
    public static List<ITextComponent> applyTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        if (stack.getItem() instanceof IHasTooltip) {
            IHasTooltip item = (IHasTooltip) stack.getItem();

            if (!item.getShiftTooltip(stack).isEmpty() && Screen.hasShiftDown()) {
                tooltip.add(new StringTextComponent(" "));
                tooltip.add(new TranslationTextComponent("tooltip.relics.shift.divider_up"));
                tooltip.addAll(item.getShiftTooltip(stack));
                tooltip.add(new TranslationTextComponent("tooltip.relics.shift.divider_down"));
            }

            if (!item.getAltTooltip(stack).isEmpty() && Screen.hasAltDown()) {
                tooltip.add(new StringTextComponent(" "));
                tooltip.add(new TranslationTextComponent("tooltip.relics.alt.divider_up"));
                tooltip.addAll(item.getAltTooltip(stack));
                tooltip.add(new TranslationTextComponent("tooltip.relics.alt.divider_sown"));
            }

            if (!item.getControlTooltip(stack).isEmpty() && Screen.hasControlDown()) {
                tooltip.add(new StringTextComponent(" "));
                tooltip.add(new TranslationTextComponent("tooltip.relics.control.divider_up"));
                tooltip.addAll(item.getControlTooltip(stack));
                tooltip.add(new TranslationTextComponent("tooltip.relics.control.divider_down"));
            }

            if ((!item.getShiftTooltip(stack).isEmpty() && !Screen.hasShiftDown()) || (!item.getAltTooltip(stack).isEmpty() && !Screen.hasAltDown())
                    || (!item.getControlTooltip(stack).isEmpty() && !Screen.hasControlDown())) tooltip.add(new StringTextComponent(" "));

            if (!Screen.hasShiftDown() && !item.getShiftTooltip(stack).isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.shift.tooltip"));
            if (!Screen.hasAltDown() && !item.getAltTooltip(stack).isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.alt.tooltip"));
            if (!Screen.hasControlDown() && !item.getControlTooltip(stack).isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.ctrl.tooltip"));
        }
        return tooltip;
    }

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