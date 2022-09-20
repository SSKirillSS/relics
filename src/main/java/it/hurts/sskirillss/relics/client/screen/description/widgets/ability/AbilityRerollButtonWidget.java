package it.hurts.sskirillss.relics.client.screen.description.widgets.ability;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.AbstractSilentButton;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketRelicTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.sounds.SoundManager;
import org.jetbrains.annotations.NotNull;

public class AbilityRerollButtonWidget extends AbstractSilentButton {
    private final Minecraft MC = Minecraft.getInstance();

    private final AbilityDescriptionScreen screen;
    private final String ability;

    public AbilityRerollButtonWidget(int x, int y, AbilityDescriptionScreen screen, String ability) {
        super(x, y, 22, 22);

        this.screen = screen;
        this.ability = ability;
    }

    @Override
    public void onPress() {
        NetworkHandler.sendToServer(new PacketRelicTweak(screen.pos, ability, PacketRelicTweak.Operation.REROLL));
    }

    @Override
    public void playDownSound(SoundManager handler) {

    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, AbilityDescriptionScreen.TEXTURE);

        manager.bindForSetup(AbilityDescriptionScreen.TEXTURE);

        blit(poseStack, x, y, 306, 0, 22, 22, 512, 512);
    }
}