package it.hurts.sskirillss.relics.client.gui.layers;

import it.hurts.sskirillss.relics.system.casts.handlers.HUDRenderHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class ActiveAbilitiesLayer implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        HUDRenderHandler.render(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true));
    }
}