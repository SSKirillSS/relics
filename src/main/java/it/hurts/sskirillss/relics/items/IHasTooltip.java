package it.hurts.sskirillss.relics.items;

import com.google.common.collect.Lists;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public interface IHasTooltip {
    List<ITextComponent> shiftTooltip = Lists.newArrayList();
    List<ITextComponent> altTooltip = Lists.newArrayList();
    List<ITextComponent> controlTooltip = Lists.newArrayList();

    default List<ITextComponent> getShiftTooltip() {
        return shiftTooltip;
    }

    default List<ITextComponent> getAltTooltip() {
        return altTooltip;
    }

    default List<ITextComponent> getControlTooltip() {
        return controlTooltip;
    }
}