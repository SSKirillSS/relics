package it.hurts.sskirillss.relics.client.tooltip;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TooltipBorderHandler {
    @SubscribeEvent
    public static void onTooltipColorEvent(RenderTooltipEvent.Color event) {
        ItemStack stack = event.getStack();

        if (!(stack.getItem() instanceof RelicItem))
            return;

        Pair<String, String> colors = getBorderColors(stack);

        Color color = new Color(stack.getRarity().color.getColor()).darker();
        int top = colors == null ? color.getRGB() : Color.decode(colors.getLeft()).getRGB();
        int bottom = colors == null ? color.darker().darker().getRGB() : Color.decode(colors.getRight()).getRGB();

        event.setBorderStart(top);
        event.setBorderEnd(bottom);
    }

    @SubscribeEvent
    public static void onPostTooltipEvent(RenderTooltipEvent.PostText event) {
        ItemStack stack = event.getStack();

        if (getBorderColors(stack) == null)
            return;

        int x = event.getX();
        int y = event.getY();
        int width = event.getWidth();
        int height = event.getHeight();
        MatrixStack matrix = event.getMatrixStack();

        Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(Reference.MODID, "textures/gui/tooltip/" + stack.getItem().getRegistryName().getPath() + ".png"));

        int texWidth = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int texHeight = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);

        if (texHeight == 0 || texWidth == 0)
            return;

        matrix.pushPose();

        matrix.translate(0, 0, 410.0);

        AbstractGui.blit(matrix, x - 8 - 6, y - 8 - 6, 1, 1 % texHeight, 16, 16, texWidth, texHeight);
        AbstractGui.blit(matrix, x + width - 8 + 6, y - 8 - 6, texWidth - 16 - 1, 1 % texHeight, 16, 16, texWidth, texHeight);

        AbstractGui.blit(matrix, x - 8 - 6, y + height - 8 + 6, 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);
        AbstractGui.blit(matrix, x + width - 8 + 6, y + height - 8 + 6, texWidth - 16 - 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);

        if (width >= 94) {
            AbstractGui.blit(matrix, x + (width / 2) - 47, y - 16, 16 + 2 * texWidth + 1, 1 % texHeight, 94, 16, texWidth, texHeight);
            AbstractGui.blit(matrix, x + (width / 2) - 47, y + height, 16 + 2 * texWidth + 1, 1 % texHeight + 16, 94, 16, texWidth, texHeight);
        }

        matrix.popPose();
    }

    @Nullable
    private static Pair<String, String> getBorderColors(ItemStack stack) {
        if (!(stack.getItem() instanceof RelicItem))
            return null;

        RelicItem<?> relic = (RelicItem<?>) stack.getItem();
        RelicTooltip tooltip = relic.getTooltip(stack);

        if (tooltip == null)
            return null;

        return tooltip.getBorders();
    }
}