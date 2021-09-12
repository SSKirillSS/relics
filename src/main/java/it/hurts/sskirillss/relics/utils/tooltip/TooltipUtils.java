package it.hurts.sskirillss.relics.utils.tooltip;

import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;

public class TooltipUtils {
    public static List<MutablePair<ITextComponent, Boolean>> buildTooltips(RelicTooltip relic) {
        List<MutablePair<ITextComponent, Boolean>> tooltip = new ArrayList<>();

        for (int i = 0; i < relic.getAbilities().size(); i++){
            AbilityTooltip ability = relic.getAbilities().get(i);
            ItemStack stack = relic.getStack();
            if (stack.isEmpty()) continue;

            String path = "tooltip." + Reference.MODID + "." + stack.getItem().getRegistryName().getPath() + ".ability.";

            String key = ability.getKeybind();
            boolean isActive = ability.isActive();

            TextComponent space = new StringTextComponent(" ");
            IFormattableTextComponent point = new StringTextComponent("   ◆ ").withStyle(ability.isNegative() ? TextFormatting.RED : TextFormatting.GREEN);

            IFormattableTextComponent name = new TranslationTextComponent(path + (i + 1) + ".name").withStyle(TextFormatting.YELLOW);
            IFormattableTextComponent keybind = new StringTextComponent("");
            if (isActive) keybind = new StringTextComponent("[" + (key.isEmpty() ? "Alt" : key) + "]").withStyle(TextFormatting.DARK_GRAY);
            IFormattableTextComponent divider = new StringTextComponent("-").withStyle(TextFormatting.WHITE);
            IFormattableTextComponent description = new TranslationTextComponent(path + (i + 1) + ".description", ability.getVarArgs()).withStyle(TextFormatting.GRAY);

            IFormattableTextComponent result = new StringTextComponent("");
            result.append(point).append(name).append(space);
            if (isActive) result.append(keybind).append(space);
            result.append(divider).append(space).append(description);

            tooltip.add(new MutablePair<>(result, ability.isActive()));
        }

        return tooltip;
    }
}