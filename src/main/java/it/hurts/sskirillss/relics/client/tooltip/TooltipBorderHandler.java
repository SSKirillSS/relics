package it.hurts.sskirillss.relics.client.tooltip;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.*;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TooltipBorderHandler {
    @SubscribeEvent
    public static void onTooltipColorEvent(RenderTooltipEvent.Color event) {
        ItemStack stack = event.getItemStack();

        if (!(stack.getItem() instanceof RelicItem))
            return;

        Pair<String, String> colors = getBorderColors(stack);

        Color color = new Color(stack.getRarity().color.getColor()).darker();
        int top = colors == null ? color.getRGB() : Color.decode(colors.getLeft()).getRGB();
        int bottom = colors == null ? color.darker().darker().getRGB() : Color.decode(colors.getRight()).getRGB();

        event.setBorderStart(top);
        event.setBorderEnd(bottom);
    }

    @Nullable
    public static Pair<String, String> getBorderColors(ItemStack stack) {
        if (!(stack.getItem() instanceof RelicItem<?> relic))
            return null;

        RelicTooltip tooltip = relic.getTooltip(stack);

        if (tooltip == null)
            return null;

        return tooltip.getBorders();
    }
}