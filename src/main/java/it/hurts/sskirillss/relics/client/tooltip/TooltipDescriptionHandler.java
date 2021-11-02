package it.hurts.sskirillss.relics.client.tooltip;

import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.api.integration.curios.ISlotModifier;
import it.hurts.sskirillss.relics.api.leveling.ILeveledItem;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TooltipDescriptionHandler {
    @SubscribeEvent
    public static void onTooltipRender(ItemTooltipEvent event) {
        List<ITextComponent> original = event.getToolTip();
        ItemStack stack = event.getItemStack();

        List<ITextComponent> tooltip = new ArrayList<>();

        renderState(stack, tooltip);
        renderShift(stack, tooltip);

        try {
            if (event.getFlags() == ITooltipFlag.TooltipFlags.ADVANCED)
                original.remove(new TranslationTextComponent("item.durability",
                        stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()));
        } catch (Exception e) {
            // It is Wednesday my dudes
        }

        original.addAll(1, tooltip);
    }

    private static void renderState(ItemStack stack, List<ITextComponent> tooltip) {
        Item item = stack.getItem();

        if (item instanceof IRepairableItem) {
            if (IRepairableItem.isBroken(stack))
                tooltip.add((new StringTextComponent("▶ ")
                        .withStyle(TextFormatting.GOLD))
                        .append((new TranslationTextComponent("tooltip.relics.relic.broken"))
                                .withStyle(TextFormatting.YELLOW)));
            else if (stack.getDamageValue() >= stack.getMaxDamage() * 0.1F)
                tooltip.add((new StringTextComponent("▶ ")
                        .withStyle(TextFormatting.GOLD))
                        .append((new TranslationTextComponent("tooltip.relics.relic.damaged"))
                                .withStyle(TextFormatting.YELLOW)));
        }

        if (item instanceof ICurioItem) {
            for (String tag : CuriosApi.getCuriosHelper().getCurioTags(item)) {
                List<ITextComponent> list = new ArrayList<>();

                CuriosApi.getSlotHelper().getSlotType(tag).ifPresent(slot -> {
                    if (slot.getSize() > 0)
                        return;

                    for (Item curio : ItemRegistry.getSlotModifiers()) {
                        ISlotModifier modifier = (ISlotModifier) curio;

                        for (Pair<String, Integer> pair : modifier.getSlotModifiers().getModifiers()) {
                            String identifier = pair.getLeft();
                            int amount = pair.getRight();

                            if (identifier.equals(tag) && amount > 0)
                                list.add((new StringTextComponent("   ◆ ")
                                        .withStyle(TextFormatting.YELLOW))
                                        .append(new StringTextComponent(new ItemStack(curio).getHoverName().getString())
                                                .withStyle(TextFormatting.GREEN))
                                        .append((new StringTextComponent(String.format(" [+%d]", amount))
                                                .withStyle(TextFormatting.WHITE))));
                        }
                    }

                    StringTextComponent info = new StringTextComponent("");

                    info.append((new StringTextComponent("▶ ")
                            .withStyle(TextFormatting.GOLD))
                            .append((new TranslationTextComponent("tooltip.relics.relic.requires_slot"))
                                    .withStyle(TextFormatting.YELLOW))
                            .append(" ")
                            .append((new TranslationTextComponent("curios.identifier." + slot.getIdentifier())
                                    .withStyle(TextFormatting.GREEN))));

                    if (!list.isEmpty())
                        info.append(". ").append(new TranslationTextComponent("tooltip.relics.relic.allowed_modifiers")
                                .withStyle(TextFormatting.YELLOW));

                    tooltip.add(info);

                    if (!list.isEmpty())
                        tooltip.addAll(list);
                });
            }
        }
    }

    private static List<ITextComponent> getSlotTooltip(ItemStack stack) {
        List<String> tags = new ArrayList<>(CuriosApi.getCuriosHelper().getCurioTags(stack.getItem()));
        List<ITextComponent> tooltip = new ArrayList<>();

        if (!(stack.getItem() instanceof ICurioItem) || tags.isEmpty())
            return tooltip;

        StringTextComponent component = new StringTextComponent("");

        component.append((new StringTextComponent("   ◆ ")
                .withStyle(TextFormatting.GREEN))
                .append((new TranslationTextComponent("tooltip.relics.shift.stats.slot"))
                        .withStyle(TextFormatting.YELLOW))
                .append(" "));

        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i);

            component.append((new TranslationTextComponent("curios.identifier." + tag)
                    .withStyle(TextFormatting.WHITE))
                    .append((new StringTextComponent(i + 1 < tags.size() ? ", " : "")
                            .withStyle(TextFormatting.GRAY))));
        }

        tooltip.add(component);

        return tooltip;
    }

    private static List<ITextComponent> getLevelingTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = new ArrayList<>();

        if (!(stack.getItem() instanceof ILeveledItem))
            return tooltip;

        ILeveledItem item = (ILeveledItem) stack.getItem();

        item.addExperience(stack, 1);

        int level = item.getLevel(stack);

        int currExp = item.getExperience(stack);
        int prevExp = item.getTotalExperienceForLevel(Math.max(level, level - 1));
        int nextExp = item.getTotalExperienceForLevel(item.getLevel(stack) + 1);

        tooltip.add((new StringTextComponent("   ◆ ")
                .withStyle(TextFormatting.GREEN))
                .append((new TranslationTextComponent("tooltip.relics.shift.stats.level"))
                        .withStyle(TextFormatting.YELLOW))
                .append((new StringTextComponent(String.format("%d [%d/%d]", level, (currExp - prevExp), (nextExp - prevExp)))
                        .withStyle(TextFormatting.WHITE))));

        return tooltip;
    }

    private static List<ITextComponent> getDurabilityTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = new ArrayList<>();

        if (!(stack.getItem() instanceof IRepairableItem) || IRepairableItem.isBroken(stack))
            return tooltip;

        IRepairableItem item = (IRepairableItem) stack.getItem();

        tooltip.add((new StringTextComponent("   ◆ ")
                .withStyle(TextFormatting.GREEN))
                .append((new TranslationTextComponent("tooltip.relics.shift.stats.durability"))
                        .withStyle(TextFormatting.YELLOW))
                .append((new StringTextComponent(" " + item.getDurability(stack) + "/" + stack.getMaxDamage()))
                        .withStyle(TextFormatting.WHITE)));

        return tooltip;
    }

    private static List<ITextComponent> getAbilitiesTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = new ArrayList<>();

        if (!(stack.getItem() instanceof RelicItem))
            return tooltip;

        RelicItem<?> relic = (RelicItem<?>) stack.getItem();
        RelicTooltip data = relic.getTooltip(stack);

        if (data == null)
            return tooltip;

        List<AbilityTooltip> abilities = data.getAbilities();

        if (abilities.isEmpty())
            return tooltip;

        for (int i = 0; i < abilities.size(); i++) {
            AbilityTooltip ability = abilities.get(i);
            String path = "tooltip." + Reference.MODID + "." + Objects.requireNonNull(relic.getRegistryName()).getPath() + ".ability." + (i + 1) + ".";
            String key = ability.getKeybinding();

            tooltip.add((new StringTextComponent("   ◆ ")
                    .withStyle(ability.isNegative() ? TextFormatting.RED : TextFormatting.GREEN))
                    .append((new TranslationTextComponent(path + "name"))
                            .withStyle(TextFormatting.YELLOW))
                    .append((new StringTextComponent(key == null ? "" : String.format(" [%s]", key))
                            .withStyle(TextFormatting.DARK_GRAY)))
                    .append((new StringTextComponent(" - ")
                            .withStyle(TextFormatting.WHITE)))
                    .append(new TranslationTextComponent(path + "description", ability.getArgs().toArray(new Object[0]))
                            .withStyle(TextFormatting.GRAY)));
        }

        return tooltip;
    }

    private static List<ITextComponent> getModifiersTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = new ArrayList<>();

        if (!(stack.getItem() instanceof ISlotModifier))
            return tooltip;

        for (Pair<String, Integer> pair : ((ISlotModifier) stack.getItem()).getSlotModifiers().getModifiers()) {
            String identifier = pair.getLeft();
            int amount = pair.getRight();

            tooltip.add((new StringTextComponent("   ◆ ")
                    .withStyle(amount > 0 ? TextFormatting.GREEN : TextFormatting.RED))
                    .append((new TranslationTextComponent("curios.identifier." + identifier)
                            .withStyle(TextFormatting.YELLOW)))
                    .append((new StringTextComponent(String.format(" [%s%s]", (amount > 0 ? "+" : "-"), amount))
                            .withStyle(TextFormatting.GRAY))));
        }

        return tooltip;
    }

    private static void renderShift(ItemStack stack, List<ITextComponent> tooltip) {
        List<ITextComponent> slot = getSlotTooltip(stack);
        List<ITextComponent> leveling = getLevelingTooltip(stack);
        List<ITextComponent> abilities = getAbilitiesTooltip(stack);
        List<ITextComponent> modifiers = getModifiersTooltip(stack);
        List<ITextComponent> durability = getDurabilityTooltip(stack);

        boolean hasStats = !leveling.isEmpty() || !durability.isEmpty() || !slot.isEmpty();

        if (hasStats || !abilities.isEmpty() || !modifiers.isEmpty()) {
            if (Screen.hasShiftDown()) {
                tooltip.add(new StringTextComponent(" "));

                if (!abilities.isEmpty()) {
                    tooltip.add((new StringTextComponent("▶ ")
                            .withStyle(TextFormatting.DARK_GREEN))
                            .append((new TranslationTextComponent("tooltip.relics.shift.abilities"))
                                    .withStyle(TextFormatting.GREEN)));

                    tooltip.addAll(abilities);

                    if (hasStats)
                        tooltip.add(new StringTextComponent(" "));
                }

                if (hasStats) {
                    tooltip.add((new StringTextComponent("▶ ")
                            .withStyle(TextFormatting.DARK_GREEN))
                            .append((new TranslationTextComponent("tooltip.relics.shift.stats"))
                                    .withStyle(TextFormatting.GREEN)));

                    tooltip.addAll(slot);
                    tooltip.addAll(leveling);
                    tooltip.addAll(durability);

                    if (!modifiers.isEmpty())
                        tooltip.add(new StringTextComponent(" "));
                }

                if (!modifiers.isEmpty()) {
                    tooltip.add((new StringTextComponent("▶ ")
                            .withStyle(TextFormatting.DARK_GREEN))
                            .append((new TranslationTextComponent("tooltip.relics.shift.slots"))
                                    .withStyle(TextFormatting.GREEN)));

                    tooltip.addAll(modifiers);
                }
            } else {
                tooltip.add(new StringTextComponent(" "));

                tooltip.add(new TranslationTextComponent("tooltip.relics.shift.title"));
            }
        }
    }
}