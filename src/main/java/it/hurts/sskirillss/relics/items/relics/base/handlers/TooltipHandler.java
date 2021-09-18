package it.hurts.sskirillss.relics.items.relics.base.handlers;

import it.hurts.sskirillss.relics.items.RelicContractItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.TooltipUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.MutablePair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TooltipHandler {
    public static void setupTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        drawContract(stack, worldIn, tooltip);
        drawDescription(stack, tooltip);
    }

    private static void drawContract(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        PlayerEntity owner = RelicUtils.Owner.getOwner(stack, worldIn);
        long time = (NBTUtils.getLong(stack, RelicContractItem.TAG_DATE, 0) + (3600 * 20) - worldIn.getGameTime()) / 20;

        if (time > 0 && owner != null) {
            long hours = time / 3600;
            long minutes = (time % 3600) / 60;
            long seconds = (time % 3600) % 60;

            tooltip.add(new TranslationTextComponent("tooltip.relics.contract", owner.getDisplayName(), hours, minutes, seconds));
        }
    }

    private static StringTextComponent drawProgressBar(float percentage, String style, String startHEX, String middleHEX, String endHEX, String neutralHEX, boolean withPercents) {
        StringBuilder string = new StringBuilder(style);
        int offset = (int) Math.min(100, Math.floor(string.length() * percentage / 100));
        Color color = Color.parseColor(percentage > 33.3 ? percentage > 66.6 ? endHEX : middleHEX : startHEX);
        StringTextComponent component = new StringTextComponent("");

        component.append(new StringTextComponent(string.substring(0, offset)).setStyle(Style.EMPTY.withColor(color)));
        component.append(new StringTextComponent(string.substring(offset, string.length())).setStyle(Style.EMPTY
                .withColor(Color.parseColor(neutralHEX))));

        if (withPercents)
            component.append(new StringTextComponent(" " + Math.round(percentage * 10.0F) / 10.0F + "%").setStyle(Style.EMPTY.withColor(color)));

        return component;
    }

    private static void drawLevel(ItemStack stack, List<ITextComponent> tooltip) {
        int level = RelicUtils.Level.getLevel(stack);
        int prevExp = RelicUtils.Level.getTotalExperienceForLevel(stack, Math.max(level, level - 1));

        tooltip.add(new TranslationTextComponent("tooltip.relics.level", level, RelicUtils.Level.getExperience(stack) - prevExp,
                RelicUtils.Level.getTotalExperienceForLevel(stack, level + 1) - prevExp));

        float percentage = (RelicUtils.Level.getExperience(stack) - prevExp) * 1.0F / (RelicUtils.Level.getTotalExperienceForLevel(stack,
                RelicUtils.Level.getLevel(stack) + 1) - prevExp) * 100;

        tooltip.add(drawProgressBar(percentage, RelicsConfig.RelicsGeneral.LEVELING_BAR_STYLE.get(),
                RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_LOW.get(), RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_MEDIUM.get(),
                RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_HIGH.get(), RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_NEUTRAL.get(), true));
    }

    private static void drawDescription(ItemStack stack, List<ITextComponent> tooltip) {
        RelicTooltip relicTooltip = ((RelicItem) stack.getItem()).getShiftTooltip(stack);

        if (!relicTooltip.getAbilities().isEmpty() && Screen.hasShiftDown()) {
            tooltip.add(new StringTextComponent(" "));

            List<MutablePair<ITextComponent, Boolean>> tooltips = TooltipUtils.buildTooltips(relicTooltip);
            List<ITextComponent> active = new ArrayList<>();
            List<ITextComponent> passive = new ArrayList<>();

            tooltips.forEach(pair -> {
                ITextComponent component = pair.getLeft();
                if (pair.getRight())
                    active.add(component);
                else
                    passive.add(component);
            });

            if (!passive.isEmpty()) {
                tooltip.add((new StringTextComponent("▶ ").withStyle(TextFormatting.DARK_GREEN))
                        .append(new TranslationTextComponent("tooltip.relics.shift.abilities.passive.tooltip")
                                .withStyle(TextFormatting.GREEN)));

                tooltip.addAll(passive);

                tooltip.add(new StringTextComponent(" "));
            }

            if (!active.isEmpty()) {
                tooltip.add((new StringTextComponent("▶ ").withStyle(TextFormatting.DARK_GREEN))
                        .append(new TranslationTextComponent("tooltip.relics.shift.abilities.active.tooltip")
                                .withStyle(TextFormatting.GREEN)));

                tooltip.addAll(active);
            }
        }

        if (!((RelicItem) stack.getItem()).getAltTooltip(stack).isEmpty() && Screen.hasAltDown()) {
            tooltip.add(new StringTextComponent(" "));
            tooltip.addAll(((RelicItem) stack.getItem()).getAltTooltip(stack));
        }

        if (!((RelicItem) stack.getItem()).getControlTooltip(stack).isEmpty() && Screen.hasControlDown()) {
            tooltip.add(new StringTextComponent(" "));
            tooltip.addAll(((RelicItem) stack.getItem()).getControlTooltip(stack));
        }

        if ((!((RelicItem) stack.getItem()).getShiftTooltip(stack).getAbilities().isEmpty() && !Screen.hasShiftDown()) || (!((RelicItem) stack.getItem()).getAltTooltip(stack).isEmpty() && !Screen.hasAltDown())
                || (!((RelicItem) stack.getItem()).getControlTooltip(stack).isEmpty() && !Screen.hasControlDown()))
            tooltip.add(new StringTextComponent(" "));

        if (!Screen.hasShiftDown() && !((RelicItem) stack.getItem()).getShiftTooltip(stack).getAbilities().isEmpty())
            tooltip.add(new TranslationTextComponent("tooltip.relics.shift.tooltip"));

        if (!Screen.hasAltDown() && !((RelicItem) stack.getItem()).getAltTooltip(stack).isEmpty())
            tooltip.add(new TranslationTextComponent("tooltip.relics.alt.tooltip"));

        if (!Screen.hasControlDown() && !((RelicItem) stack.getItem()).getControlTooltip(stack).isEmpty())
            tooltip.add(new TranslationTextComponent("tooltip.relics.ctrl.tooltip"));
    }
}