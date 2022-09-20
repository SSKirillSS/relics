package it.hurts.sskirillss.relics.client.tooltip;

import it.hurts.sskirillss.relics.client.screen.description.RelicDescriptionScreen;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class TooltipTickHandler {
    private static ItemStack relicStack = ItemStack.EMPTY;
    private static int usingTicks;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ClientTickEvent event) {
        Minecraft MC = Minecraft.getInstance();

        if (!(MC.getCameraEntity() instanceof Player player)
                || !(MC.screen instanceof AbstractContainerScreen<? extends AbstractContainerMenu> screen))
            return;

        Slot slot = screen.getSlotUnderMouse();

        if (slot == null)
            return;

        ItemStack stack = slot.getItem();

        if (!(stack.getItem() instanceof RelicItem<?> relic))
            return;

        if (Screen.hasControlDown()) {
            ++usingTicks;

            relicStack = stack;

            if (usingTicks >= 100) {
                Minecraft.getInstance().setScreen(new RelicDescriptionScreen(player.blockPosition(), stack));

                usingTicks = 0;

                relicStack = ItemStack.EMPTY;
            }
        } else {
            usingTicks = 0;

            relicStack = ItemStack.EMPTY;
        }
    }

    @SubscribeEvent
    public static void onTooltipTick(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (!stack.sameItem(relicStack))
            return;

        List<Component> tooltip = event.getToolTip();

        tooltip.add(drawProgressBar(usingTicks * 100F / 100, "--- Я РУССКИЙ ВОЕННЫЙ КОРАБЛЬ ---"));
    }

    public static TextComponent drawProgressBar(float percentage, String style) {
        StringBuilder string = new StringBuilder(style);

        int offset = (int) Math.min(100, Math.floor(string.length() * percentage / 100));

        TextComponent component = new TextComponent("");

        float angle = 0F;

        String start = string.substring(0, offset);

        for (int i = 0; i < offset; i++) {
            component.append(new TextComponent(String.valueOf(start.charAt(i))).setStyle(Style.EMPTY.withColor(Mth.hsvToRgb(Math.min(1F, angle), 1F, 1F))));

            angle += 0.01F;
        }

        component.append(new TextComponent(string.substring(offset, string.length())).setStyle(Style.EMPTY
                .withColor(TextColor.parseColor("#808080"))));

        return component;
    }
}