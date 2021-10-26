package it.hurts.sskirillss.relics.client.tooltip;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TooltipHandler {
    @SubscribeEvent
    public static void onTooltipColorEvent(RenderTooltipEvent.Color event) {
        ItemStack stack = event.getStack();

        if (!(stack.getItem() instanceof RelicItem))
            return;

        if (RelicItem.isBroken(stack)) {
            event.setBorderStart(0xFFff0000);
            event.setBorderEnd(0xFFff0000);
        } else {
            RelicTooltip.TooltipWindow window = getWindowData(stack);
            Integer rgb = stack.getRarity().color.getColor();

            Color color = new Color(rgb == null ? 16777215 : rgb).darker();
            int top = window == null ? color.getRGB() : window.getTopColor();
            int bottom = window == null ? color.darker().darker().getRGB() : window.getBottomColor();

            event.setBorderStart(top);
            event.setBorderEnd(bottom);
        }
    }

    @SubscribeEvent
    public static void onPostTooltipEvent(RenderTooltipEvent.PostText event) {
        ItemStack stack = event.getStack();
        RelicTooltip.TooltipWindow window = getWindowData(event.getStack());

        if (window == null || !window.hasFrame())
            return;

        int x = event.getX();
        int y = event.getY();
        int width = event.getWidth();
        int height = event.getHeight();
        MatrixStack matrix = event.getMatrixStack();

        Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(Reference.MODID, "textures/gui/tooltip/" + stack.getItem().getRegistryName().getPath() + ".png"));

        int texWidth = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int texHeight = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);

        if (texHeight == 16 || texWidth == 16
                || texHeight == 0 || texWidth == 0)
            return;

        matrix.pushPose();

        matrix.translate(0, 0, 410.0);

        AbstractGui.blit(matrix, x - 6, y - 6, 2 * 64, 1 % texHeight, 8, 8, texWidth, texHeight);
        AbstractGui.blit(matrix, x + width - 8 + 6, y - 6, 56 + 2 * 64, 1 % texHeight, 8, 8, texWidth, texHeight);
        AbstractGui.blit(matrix, x - 6, y + height - 8 + 6, 2 * 64, 1 % texHeight + 8, 8, 8, texWidth, texHeight);
        AbstractGui.blit(matrix, x + width - 8 + 6, y + height - 8 + 6, 56 + 2 * 64, 1 % texHeight + 8, 8, 8, texWidth, texHeight);

        if (width >= 48) {
            AbstractGui.blit(matrix, x + (width / 2) - 24, y - 9, 8 + 2 * 64, 1 % texHeight, 48, 8, texWidth, texHeight);
            AbstractGui.blit(matrix, x + (width / 2) - 24, y + height - 8 + 9, 8 + 2 * 64, 1 % texHeight + 8, 48, 8, texWidth, texHeight);
        }

        matrix.popPose();
    }

    private static RelicTooltip.TooltipWindow getWindowData(ItemStack stack) {
        if (!(stack.getItem() instanceof RelicItem) || RelicItem.isBroken(stack))
            return null;

        RelicItem<?> relic = (RelicItem<?>) stack.getItem();
        RelicTooltip tooltip = relic.getTooltip(stack);

        return tooltip == null ? null : tooltip.getWindow();
    }
}