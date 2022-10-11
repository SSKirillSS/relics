package it.hurts.sskirillss.relics.client.screen.description.widgets.relic.card;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.AbstractSilentButton;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.RelicDescriptionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

public class AbilityCardIconWidget extends AbstractSilentButton {
    private final Minecraft MC = Minecraft.getInstance();

    private final RelicDescriptionScreen screen;
    private final String ability;

    public AbilityCardIconWidget(int x, int y, RelicDescriptionScreen screen, String ability) {
        super(x, y, 28, 37);

        this.screen = screen;
        this.ability = ability;
    }

    @Override
    public void onPress() {
        MC.setScreen(new AbilityDescriptionScreen(screen.pos, screen.stack, ability));
    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, RelicDescriptionScreen.TEXTURE);

        manager.bindForSetup(RelicDescriptionScreen.TEXTURE);

        blit(poseStack, x, y, 258, 0, 28, 37, 512, 512);
    }
}