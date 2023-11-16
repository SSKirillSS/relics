package it.hurts.sskirillss.relics.client.screen.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class ScreenUtils {
    public static void drawTexturedTooltipBorder(GuiGraphics guiGraphics, ResourceLocation texture, int width, int height, int x, int y) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, texture);

        Minecraft.getInstance().getTextureManager().bindForSetup(texture);

        int texSize = 19;
        int cornerSize = 9;

        guiGraphics.blit(texture, x, y, cornerSize, cornerSize, 0, 0, cornerSize, cornerSize, texSize, texSize);
        guiGraphics.blit(texture, x + cornerSize, y, width, cornerSize, cornerSize, 0, 1, cornerSize, texSize, texSize);
        guiGraphics.blit(texture, x + cornerSize + width, y, cornerSize, cornerSize, cornerSize + 1, 0, cornerSize, cornerSize, texSize, texSize);
        guiGraphics.blit(texture, x, y + cornerSize, cornerSize, height, 0, cornerSize, cornerSize, 1, texSize, texSize);
        guiGraphics.blit(texture, x + width + cornerSize, y + cornerSize, cornerSize, height, cornerSize + 1, cornerSize, cornerSize, 1, texSize, texSize);
        guiGraphics.blit(texture, x + cornerSize, y + cornerSize, width, height, cornerSize + 1, cornerSize + 1, 1, 1, texSize, texSize);
        guiGraphics.blit(texture, x, y + height + cornerSize, cornerSize, cornerSize, 0, cornerSize + 1, cornerSize, cornerSize, texSize, texSize);
        guiGraphics.blit(texture, x + cornerSize, y + height + cornerSize, width, cornerSize, cornerSize, cornerSize + 1, 1, cornerSize, texSize, texSize);
        guiGraphics.blit(texture, x + cornerSize + width, y + cornerSize + height, cornerSize, cornerSize, cornerSize + 1, cornerSize + 1, cornerSize, cornerSize, texSize, texSize);
    }

    public static void drawCenteredString(GuiGraphics guiGraphics, Font font, Component text, int x, int y, int color, boolean dropShadow) {
        FormattedCharSequence sequence = text.getVisualOrderText();

        guiGraphics.drawString(font, sequence, x - font.width(sequence) / 2, y, color, dropShadow);
    }

    public static void drawCenteredString(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color, boolean dropShadow) {
        guiGraphics.drawString(font, text, x - font.width(text) / 2, y, color, dropShadow);
    }

    public static void drawCenteredString(GuiGraphics guiGraphics, Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow) {
        guiGraphics.drawString(font, text, x - font.width(text) / 2, y, color, dropShadow);
    }

    public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= x + width) && (mouseY >= y && mouseY <= y + height);
    }
}