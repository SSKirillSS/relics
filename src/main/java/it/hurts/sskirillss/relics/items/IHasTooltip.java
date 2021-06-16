package it.hurts.sskirillss.relics.items;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public interface IHasTooltip {
    List<ITextComponent> shiftTooltip = Lists.newArrayList();
    List<ITextComponent> altTooltip = Lists.newArrayList();
    List<ITextComponent> controlTooltip = Lists.newArrayList();

    default List<ITextComponent> getShiftTooltip(ItemStack stack) {
        return shiftTooltip;
    }

    default List<ITextComponent> getAltTooltip(ItemStack stack) {
        return altTooltip;
    }

    default List<ITextComponent> getControlTooltip(ItemStack stack) {
        return controlTooltip;
    }
}