package it.hurts.sskirillss.relics.client.screen.description.widgets.ability;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketRelicTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;

public class AbilityResetButtonWidget extends AbstractButton {
    private final Minecraft MC = Minecraft.getInstance();

    private final AbilityDescriptionScreen screen;
    private final String ability;

    public AbilityResetButtonWidget(int x, int y, AbilityDescriptionScreen screen, String ability) {
        super(x, y, 22, 22, TextComponent.EMPTY);

        this.screen = screen;
        this.ability = ability;
    }

    @Override
    public void onPress() {
        if (RelicItem.mayPlayerReset(MC.player, screen.stack, ability))
            NetworkHandler.sendToServer(new PacketRelicTweak(screen.pos, ability, PacketRelicTweak.Operation.RESET));
    }

    @Override
    public void playDownSound(SoundManager handler) {
        if (RelicItem.mayPlayerReset(MC.player, screen.stack, ability))
            handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
    }

    @Override
    public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, AbilityDescriptionScreen.TEXTURE);

        manager.bindForSetup(AbilityDescriptionScreen.TEXTURE);

        if (RelicItem.mayPlayerReset(MC.player, screen.stack, ability)) {
            blit(poseStack, x, y, 282, 0, 22, 22, 512, 512);

            if (isHovered)
                blit(poseStack, x - 1, y - 1, 330, 0, 24, 24, 512, 512);
        } else {
            blit(poseStack, x, y, 282, 24, 22, 22, 512, 512);

            if (isHovered)
                blit(poseStack, x - 1, y - 1, 330, 24, 24, 24, 512, 512);
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }
}