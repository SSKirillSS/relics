package it.hurts.sskirillss.relics.client.tooltip;

import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.api.leveling.ILeveledItem;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TooltipDescriptionHandler {
    @SubscribeEvent
    public static void onTooltipRender(ItemTooltipEvent event) {
        Player player = event.getPlayer();
        List<Component> original = event.getToolTip();
        ItemStack stack = event.getItemStack();

        if (!(stack.getItem() instanceof RelicItem<?>))
            return;

        List<ITextComponent> tooltip = new ArrayList<>();

        renderState(stack, tooltip, player);
        renderShift(stack, tooltip);

        try {
            if (event.getFlags() == TooltipFlag.Default.ADVANCED)
                original.remove(new TranslatableComponent("item.durability",
                        stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()));
        } catch (Exception ignored) {

        }

        original.addAll(1, tooltip);
    }

    private static void renderState(ItemStack stack, List<Component> tooltip, Player player) {
        if (player == null || stack == null)
            return;

        Item item = stack.getItem();

        if (item instanceof IRepairableItem) {
            if (DurabilityUtils.isBroken(stack))
                tooltip.add((new TextComponent("▶ ")
                        .withStyle(ChatFormatting.GOLD))
                        .append((new TranslatableComponent("tooltip.relics.relic.broken"))
                                .withStyle(ChatFormatting.YELLOW)));
            else if (stack.getDamageValue() >= stack.getMaxDamage() * 0.1F)
                tooltip.add((new TextComponent("▶ ")
                        .withStyle(ChatFormatting.GOLD))
                        .append((new TranslatableComponent("tooltip.relics.relic.damaged"))
                                .withStyle(ChatFormatting.YELLOW)));
        }

        for (String tag : CuriosApi.getCuriosHelper().getCurioTags(item)) {
            CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent(handler -> {
                Map<String, ICurioStacksHandler> curios = handler.getCurios();

                if (curios.size() == 0)
                    return;

                List<Component> list = new ArrayList<>();

                if (curios.get(tag) == null || curios.get(tag).getSlots() > 0)
                    return;

                for (Item curio : ItemRegistry.getSlotModifiers()) {
                    RelicItem<?> relic = (RelicItem<?>) curio;

                    for (Pair<String, Integer> pair : relic.getSlotModifiers(stack).getModifiers()) {
                        String identifier = pair.getLeft();
                        int amount = pair.getRight();

                        if (identifier.equals(tag) && amount > 0)
                            list.add((new TextComponent("   ◆ ")
                                    .withStyle(ChatFormatting.YELLOW))
                                    .append(new TextComponent(new ItemStack(curio).getHoverName().getString())
                                            .withStyle(ChatFormatting.GREEN))
                                    .append((new TextComponent(String.format(" [+%d]", amount))
                                            .withStyle(ChatFormatting.WHITE))));
                    }
                }

                TextComponent info = new TextComponent("");

                info.append((new TextComponent("▶ ")
                        .withStyle(ChatFormatting.GOLD))
                        .append((new TranslatableComponent("tooltip.relics.relic.requires_slot"))
                                .withStyle(ChatFormatting.YELLOW))
                        .append(" ")
                        .append((new TranslatableComponent("curios.identifier." + tag)
                                .withStyle(ChatFormatting.GREEN))));

                if (!list.isEmpty())
                    info.append(". ")
                            .withStyle(ChatFormatting.YELLOW)
                            .append(new TranslatableComponent("tooltip.relics.relic.allowed_modifiers")
                                    .withStyle(ChatFormatting.YELLOW));

                tooltip.add(info);

                if (!list.isEmpty())
                    tooltip.addAll(list);
            });
        }
    }

    private static List<Component> getSlotTooltip(ItemStack stack) {
        List<String> tags = new ArrayList<>(CuriosApi.getCuriosHelper().getCurioTags(stack.getItem()));
        List<Component> tooltip = new ArrayList<>();

        if (!(stack.getItem() instanceof ICurioItem) || tags.isEmpty())
            return tooltip;

        TextComponent component = new TextComponent("");

        component.append((new TextComponent("   ◆ ")
                .withStyle(ChatFormatting.GREEN))
                .append((new TranslatableComponent("tooltip.relics.shift.stats.slot"))
                        .withStyle(ChatFormatting.YELLOW))
                .append(" "));

        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i);

            component.append((new TranslatableComponent("curios.identifier." + tag)
                    .withStyle(ChatFormatting.WHITE))
                    .append((new TextComponent(i + 1 < tags.size() ? ", " : "")
                            .withStyle(ChatFormatting.GRAY))));
        }

        tooltip.add(component);

        return tooltip;
    }

    private static List<Component> getLevelingTooltip(ItemStack stack) {
        List<Component> tooltip = new ArrayList<>();

        if (!(stack.getItem() instanceof ILeveledItem item))
            return tooltip;

        int level = item.getLevel(stack);

        int currExp = item.getExperience(stack);
        int prevExp = item.getTotalExperienceForLevel(Math.max(level, level - 1));
        int nextExp = item.getTotalExperienceForLevel(item.getLevel(stack) + 1);

        tooltip.add((new TextComponent("   ◆ ")
                .withStyle(ChatFormatting.GREEN))
                .append((new TranslatableComponent("tooltip.relics.shift.stats.level"))
                        .withStyle(ChatFormatting.YELLOW))
                .append((new TextComponent(String.format("%d [%d/%d]", level, (currExp - prevExp), (nextExp - prevExp)))
                        .withStyle(ChatFormatting.WHITE))));

        return tooltip;
    }

    private static List<Component> getDurabilityTooltip(ItemStack stack) {
        List<Component> tooltip = new ArrayList<>();

        if (!(stack.getItem() instanceof IRepairableItem) || DurabilityUtils.isBroken(stack))
            return tooltip;

        tooltip.add((new TextComponent("   ◆ ")
                .withStyle(ChatFormatting.GREEN))
                .append((new TranslatableComponent("tooltip.relics.shift.stats.durability"))
                        .withStyle(ChatFormatting.YELLOW))
                .append((new TextComponent(String.format(" %d/%d", DurabilityUtils.getDurability(stack), stack.getMaxDamage())))
                        .withStyle(ChatFormatting.WHITE)));

        return tooltip;
    }

    private static List<Component> getAbilitiesTooltip(ItemStack stack) {
        List<Component> tooltip = new ArrayList<>();

        if (!(stack.getItem() instanceof RelicItem<?> relic))
            return tooltip;

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

            tooltip.add((new TextComponent("   ◆ ")
                    .withStyle(ability.isNegative() ? ChatFormatting.RED : ChatFormatting.GREEN))
                    .append((new TranslatableComponent(path + "name"))
                            .withStyle(ChatFormatting.YELLOW))
                    .append((new TextComponent(key == null ? "" : String.format(" [%s]", key))
                            .withStyle(ChatFormatting.DARK_GRAY)))
                    .append((new TextComponent(" - ")
                            .withStyle(ChatFormatting.WHITE)))
                    .append(new TranslatableComponent(path + "description", ability.getArgs().toArray(new Object[0]))
                            .withStyle(ChatFormatting.GRAY)));
        }

        return tooltip;
    }

    private static List<Component> getModifiersTooltip(ItemStack stack) {
        List<Component> tooltip = new ArrayList<>();

        if (!(stack.getItem() instanceof RelicItem<?> relic)
                || relic.getSlotModifiers(stack) == null)
            return tooltip;

        for (Pair<String, Integer> pair : relic.getSlotModifiers(stack).getModifiers()) {
            String identifier = pair.getLeft();
            int amount = pair.getRight();

            tooltip.add((new TextComponent("   ◆ ")
                    .withStyle(amount > 0 ? ChatFormatting.GREEN : ChatFormatting.RED))
                    .append((new TranslatableComponent("curios.identifier." + identifier)
                            .withStyle(ChatFormatting.YELLOW)))
                    .append((new TextComponent(String.format(" [%s%s]", (amount > 0 ? "+" : "-"), amount))
                            .withStyle(ChatFormatting.GRAY))));
        }

        return tooltip;
    }

    private static void renderShift(ItemStack stack, List<Component> tooltip) {
        List<Component> slot = getSlotTooltip(stack);
        List<Component> leveling = getLevelingTooltip(stack);
        List<Component> abilities = getAbilitiesTooltip(stack);
        List<Component> modifiers = getModifiersTooltip(stack);
        List<Component> durability = getDurabilityTooltip(stack);

        boolean hasStats = !leveling.isEmpty() || !durability.isEmpty() || !slot.isEmpty();

        if (hasStats || !abilities.isEmpty() || !modifiers.isEmpty()) {
            if (Screen.hasShiftDown()) {
                tooltip.add(new TextComponent(" "));

                if (!abilities.isEmpty()) {
                    tooltip.add((new TextComponent("▶ ")
                            .withStyle(ChatFormatting.DARK_GREEN))
                            .append((new TranslatableComponent("tooltip.relics.shift.abilities"))
                                    .withStyle(ChatFormatting.GREEN)));

                    tooltip.addAll(abilities);

                    if (hasStats)
                        tooltip.add(new TextComponent(" "));
                }

                if (hasStats) {
                    tooltip.add((new TextComponent("▶ ")
                            .withStyle(ChatFormatting.DARK_GREEN))
                            .append((new TranslatableComponent("tooltip.relics.shift.stats"))
                                    .withStyle(ChatFormatting.GREEN)));

                    tooltip.addAll(slot);
                    tooltip.addAll(leveling);
                    tooltip.addAll(durability);

                    if (!modifiers.isEmpty())
                        tooltip.add(new TextComponent(" "));
                }

                if (!modifiers.isEmpty()) {
                    tooltip.add((new TextComponent("▶ ")
                            .withStyle(ChatFormatting.DARK_GREEN))
                            .append((new TranslatableComponent("tooltip.relics.shift.slots"))
                                    .withStyle(ChatFormatting.GREEN)));

                    tooltip.addAll(modifiers);
                }
            } else {
                tooltip.add(new TextComponent(" "));

                tooltip.add(new TranslatableComponent("tooltip.relics.shift.title"));
            }
        }
    }
}