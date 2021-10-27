package it.hurts.sskirillss.relics.items.relics.base.handlers;

import it.hurts.sskirillss.relics.items.RelicContractItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttribute;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class TooltipHandler {
    public static void setupTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        drawDescription(stack, tooltip);
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
        RelicItem<?> relic = (RelicItem<?>) stack.getItem();
        RelicTooltip relicTooltip = relic.getTooltip(stack);
        RelicAttribute attribute = relic.getAttributes(stack);

        if (Screen.hasShiftDown()) {
            if (relicTooltip != null) {
                if (!relicTooltip.getShift().isEmpty()) {
                    tooltip.add(new StringTextComponent(" "));

                    List<MutablePair<ITextComponent, Boolean>> tooltips = TooltipUtils.buildTooltips(relic);
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
            }

            if (attribute != null) {
                List<MutablePair<String, Integer>> slots = attribute.getSlots();

                if (!slots.isEmpty()) {
                    if (relicTooltip == null || relicTooltip.getShift().isEmpty())
                        tooltip.add(new StringTextComponent(" "));
                    tooltip.add((new StringTextComponent("▶ ").withStyle(TextFormatting.DARK_GREEN))
                            .append(new TranslationTextComponent("tooltip.relics.shift.slots.tooltip")
                                    .withStyle(TextFormatting.GREEN)));

                    for (MutablePair<String, Integer> slot : slots) {
                        String identifier = slot.getLeft();
                        int amount = slot.getRight();

                        tooltip.add((new StringTextComponent("   ◆ ")
                                .withStyle(amount <= 0 ? TextFormatting.RED : TextFormatting.GREEN))
                                .append(new StringTextComponent((amount <= 0 ? "-" : "+") + amount + " ")
                                        .withStyle(TextFormatting.YELLOW))
                                .append(new TranslationTextComponent("curios.identifier." + identifier)
                                        .withStyle(TextFormatting.GRAY)));
                    }
                }
            }
        }

//        if (!relicTooltip.getAlt().isEmpty() && Screen.hasAltDown()) {
//            tooltip.add(new StringTextComponent(" "));
//            tooltip.addAll(relicTooltip.getAlt());
//        }
//
//        if (!relicTooltip.getControl().isEmpty() && Screen.hasControlDown()) {
//            tooltip.add(new StringTextComponent(" "));
//            tooltip.addAll(relicTooltip.getControl());
//        }

        if ((attribute != null && !attribute.getSlots().isEmpty())
                || (relicTooltip != null && ((!relicTooltip.getShift().isEmpty() && !Screen.hasShiftDown())
                || (!relicTooltip.getAlt().isEmpty() && !Screen.hasAltDown())
                || (!relicTooltip.getControl().isEmpty() && !Screen.hasControlDown()))))
            tooltip.add(new StringTextComponent(" "));

        if (!Screen.hasShiftDown() && (relicTooltip != null && !relicTooltip.getShift().isEmpty()
                || (attribute != null && !attribute.getSlots().isEmpty())))
            tooltip.add(new TranslationTextComponent("tooltip.relics.shift.tooltip"));
//
//        if (!Screen.hasAltDown() && !relicTooltip.getAlt().isEmpty())
//            tooltip.add(new TranslationTextComponent("tooltip.relics.alt.tooltip"));
//
//        if (!Screen.hasControlDown() && !relicTooltip.getControl().isEmpty())
//            tooltip.add(new TranslationTextComponent("tooltip.relics.ctrl.tooltip"));
    }

    @SubscribeEvent
    public static void onTooltipRender(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        long time = NBTUtils.getLong(stack, RelicContractItem.TAG_DATE, -1);

        if (event.getPlayer() == null || stack.isEmpty() || time <= -1)
            return;

        World world = event.getPlayer().getCommandSenderWorld();
        PlayerEntity owner = RelicUtils.Owner.getOwner(stack, world);
        time = (time + (3600 * 20) - world.getGameTime()) / 20;

        if (time > 0 && owner != null) {
            long hours = time / 3600;
            long minutes = (time % 3600) / 60;
            long seconds = (time % 3600) % 60;

            event.getToolTip().add(new TranslationTextComponent("tooltip.relics.contract", owner.getDisplayName(), hours, minutes, seconds));
        }
    }
}