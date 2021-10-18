package it.hurts.sskirillss.relics.utils.tooltip;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;

public class TooltipUtils {
    public static List<MutablePair<ITextComponent, Boolean>> buildTooltips(RelicItem<?> relic) {
        List<MutablePair<ITextComponent, Boolean>> tooltip = new ArrayList<>();
        List<ShiftTooltip> shift = relic.getTooltip(new ItemStack(relic)).getShift();

        for (int i = 0; i < shift.size(); i++) {
            ShiftTooltip ability = shift.get(i);

            String path = "tooltip." + Reference.MODID + "." + relic.getRegistryName().getPath() + ".ability.";

            String key = ability.getKeybinding();
            boolean isActive = ability.isActive();

            TextComponent space = new StringTextComponent(" ");
            IFormattableTextComponent point = new StringTextComponent("   ◆ ").withStyle(ability.isNegative() ? TextFormatting.RED : TextFormatting.GREEN);

            IFormattableTextComponent name = new TranslationTextComponent(path + (i + 1) + ".name").withStyle(TextFormatting.YELLOW);
            IFormattableTextComponent keybinding = new StringTextComponent("");

            if (isActive)
                keybinding = new StringTextComponent("[" + (key.isEmpty() ? "Alt" : key) + "]").withStyle(TextFormatting.DARK_GRAY);

            IFormattableTextComponent divider = new StringTextComponent("-").withStyle(TextFormatting.WHITE);
            IFormattableTextComponent description = new TranslationTextComponent(path + (i + 1) + ".description", ability.getArgs().toArray(new Object[0])).withStyle(TextFormatting.GRAY);

            IFormattableTextComponent result = new StringTextComponent("");

            result.append(point).append(name).append(space);

            if (isActive)
                result.append(keybinding).append(space);

            result.append(divider).append(space).append(description);

            tooltip.add(new MutablePair<>(result, ability.isActive()));
        }

        return tooltip;
    }
}