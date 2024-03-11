package it.hurts.sskirillss.relics.client.screen.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class ScreenUtils {
    public static void drawTexturedTooltipBorder(PoseStack poseStack, ResourceLocation texture, int width, int height, int x, int y) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, texture);

        Minecraft.getInstance().getTextureManager().bindForSetup(texture);

        int texSize = 19;
        int cornerSize = 9;

        Gui.blit(poseStack, x, y, cornerSize, cornerSize, 0, 0, cornerSize, cornerSize, texSize, texSize);
        Gui.blit(poseStack, x + cornerSize, y, width, cornerSize, cornerSize, 0, 1, cornerSize, texSize, texSize);
        Gui.blit(poseStack, x + cornerSize + width, y, cornerSize, cornerSize, cornerSize + 1, 0, cornerSize, cornerSize, texSize, texSize);
        Gui.blit(poseStack, x, y + cornerSize, cornerSize, height, 0, cornerSize, cornerSize, 1, texSize, texSize);
        Gui.blit(poseStack, x + width + cornerSize, y + cornerSize, cornerSize, height, cornerSize + 1, cornerSize, cornerSize, 1, texSize, texSize);
        Gui.blit(poseStack, x + cornerSize, y + cornerSize, width, height, cornerSize + 1, cornerSize + 1, 1, 1, texSize, texSize);
        Gui.blit(poseStack, x, y + height + cornerSize, cornerSize, cornerSize, 0, cornerSize + 1, cornerSize, cornerSize, texSize, texSize);
        Gui.blit(poseStack, x + cornerSize, y + height + cornerSize, width, cornerSize, cornerSize, cornerSize + 1, 1, cornerSize, texSize, texSize);
        Gui.blit(poseStack, x + cornerSize + width, y + cornerSize + height, cornerSize, cornerSize, cornerSize + 1, cornerSize + 1, cornerSize, cornerSize, texSize, texSize);
    }

    public static void drawCenteredString(PoseStack pose, Font font, Component text, int x, int y, int color, boolean dropShadow) {
        FormattedCharSequence sequence = text.getVisualOrderText();

        if (dropShadow) {
            font.drawShadow(pose, sequence, x - font.width(sequence) / 2F, y, color);
        } else {
            font.draw(pose, sequence, x - font.width(sequence) / 2F, y, color);
        }
    }

    public static void drawCenteredString(PoseStack pose, Font font, Component text, float x, float y, int color, boolean dropShadow) {
        FormattedCharSequence sequence = text.getVisualOrderText();

        if (dropShadow) {
            font.drawShadow(pose, sequence, x - font.width(sequence) / 2F, y, color);
        } else {
            font.draw(pose, sequence, x - font.width(sequence) / 2F, y, color);
        }
    }

    public static void drawCenteredString(PoseStack pose, Font font, String text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            font.drawShadow(pose, text, x - font.width(text) / 2F, y, color);
        } else {
            font.draw(pose, text, x - font.width(text) / 2F, y, color);
        }
    }

    public static void drawCenteredString(PoseStack pose, Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            font.drawShadow(pose, text, x - font.width(text) / 2F, y, color);
        } else {
            font.draw(pose, text, x - font.width(text) / 2F, y, color);
        }
    }

    public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= x + width) && (mouseY >= y && mouseY <= y + height);
    }
}