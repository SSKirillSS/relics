package it.hurts.sskirillss.relics.client.screen.description.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.sounds.SoundManager;

import java.awt.*;

public class StatWidget extends AbstractDescriptionWidget {
    private AbilityDescriptionScreen screen;
    private final String stat;

    public StatWidget(int x, int y, AbilityDescriptionScreen screen, String stat) {
        super(x, y, 15, 16);

        this.screen = screen;
        this.stat = stat;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!(screen.getStack().getItem() instanceof IRelicItem relic) || MC.player == null)
            return;

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        StatIcon icon = relic.getStatData(screen.ability, stat).getIcon();

        Color color = new Color(icon.getColor());

        float blinkOffset = (float) (Math.sin((MC.player.tickCount + (stat.length() * 10)) * 0.2F) * 0.1F);

        RenderSystem.setShaderColor(color.getRed() / 255F + blinkOffset, color.getGreen() / 255F + blinkOffset, color.getBlue() / 255F + blinkOffset, 1F);
        RenderSystem.enableBlend();

        guiGraphics.blit(icon.getPath(), getX(), getY(), 0, 0, 15, 16, 15, 16);

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        poseStack.popPose();
    }

    @Override
    public void playDownSound(SoundManager handler) {

    }
}