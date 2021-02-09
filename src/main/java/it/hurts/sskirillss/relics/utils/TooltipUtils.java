package it.hurts.sskirillss.relics.utils;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class TooltipUtils {
    public static List<ITextComponent> applyTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        if (stack.getItem() instanceof IHasTooltip) {
            IHasTooltip item = (IHasTooltip) stack.getItem();

            if (!item.getShiftTooltip().isEmpty() && Screen.hasShiftDown()) {
                tooltip.add(new StringTextComponent(" "));
                tooltip.add(new TranslationTextComponent("tooltip.relics.shift.divider_up"));
                tooltip.addAll(item.getShiftTooltip());
                tooltip.add(new TranslationTextComponent("tooltip.relics.shift.divider_down"));
            }

            if (!item.getAltTooltip().isEmpty() && Screen.hasAltDown()) {
                tooltip.add(new StringTextComponent(" "));
                tooltip.add(new TranslationTextComponent("tooltip.relics.alt.divider_up"));
                tooltip.addAll(item.getAltTooltip());
                tooltip.add(new TranslationTextComponent("tooltip.relics.alt.divider_sown"));
            }

            if (!item.getControlTooltip().isEmpty() && Screen.hasControlDown()) {
                tooltip.add(new StringTextComponent(" "));
                tooltip.add(new TranslationTextComponent("tooltip.relics.control.divider_up"));
                tooltip.addAll(item.getControlTooltip());
                tooltip.add(new TranslationTextComponent("tooltip.relics.control.divider_down"));
            }

            if ((!item.getShiftTooltip().isEmpty() && !Screen.hasShiftDown()) || (!item.getAltTooltip().isEmpty() && !Screen.hasAltDown())
                    || (!item.getControlTooltip().isEmpty() && !Screen.hasControlDown())) tooltip.add(new StringTextComponent(" "));

            if (!Screen.hasShiftDown() && !item.getShiftTooltip().isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.shift.tooltip"));
            if (!Screen.hasAltDown() && !item.getAltTooltip().isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.alt.tooltip"));
            if (!Screen.hasControlDown() && !item.getControlTooltip().isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.ctrl.tooltip"));
        }
        return tooltip;
    }
}