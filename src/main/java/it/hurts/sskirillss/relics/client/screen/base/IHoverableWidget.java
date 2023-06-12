package it.hurts.sskirillss.relics.client.screen.base;

import net.minecraft.client.gui.GuiGraphics;

public interface IHoverableWidget {
    void onHovered(GuiGraphics guiGraphics, int mouseX, int mouseY);
}