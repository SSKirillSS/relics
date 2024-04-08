package it.hurts.sskirillss.relics.client.screen.description.widgets.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;

public class AbstractDescriptionWidget extends AbstractButton {
    public final Minecraft MC = Minecraft.getInstance();

    public AbstractDescriptionWidget(int x, int y, int width, int height) {
        super(x, y, width, height, TextComponent.EMPTY);
    }

    public boolean isLocked() {
        return false;
    }

    @Override
    public void onPress() {

    }

    @Override
    public void playDownSound(SoundManager handler) {
        if (!isLocked())
            handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }
}
