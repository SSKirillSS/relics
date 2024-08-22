package it.hurts.sskirillss.relics.client.screen.description.widgets.general.base;

import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.IRelicScreenProvider;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.sounds.SoundManager;

public abstract class AbstractBigCardWidget extends AbstractDescriptionWidget implements IHoverableWidget {
    @Getter
    private IRelicScreenProvider provider;

    public AbstractBigCardWidget(int x, int y, IRelicScreenProvider provider) {
        super(x, y, 48, 74);

        this.provider = provider;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (isHovered())
            guiGraphics.blit(DescriptionTextures.BIG_CARD_FRAME_OUTLINE, getX() - 1, getY() - 1, 0, 0, width + 2, height + 2, width + 2, height + 2);
    }

    @Override
    public void playDownSound(SoundManager handler) {

    }
}