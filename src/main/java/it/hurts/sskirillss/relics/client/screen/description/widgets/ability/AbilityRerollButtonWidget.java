package it.hurts.sskirillss.relics.client.screen.description.widgets.ability;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketRelicTweak;
import net.minecraft.client.renderer.texture.TextureManager;

public class AbilityRerollButtonWidget extends AbstractDescriptionWidget {
    private final AbilityDescriptionScreen screen;
    private final String ability;

    public AbilityRerollButtonWidget(int x, int y, AbilityDescriptionScreen screen, String ability) {
        super(x, y, 22, 22);

        this.screen = screen;
        this.ability = ability;
    }

    @Override
    public boolean isLocked() {
        return !RelicItem.mayPlayerReroll(MC.player, screen.stack, ability);
    }

    @Override
    public void onPress() {
        if (!isLocked())
            NetworkHandler.sendToServer(new PacketRelicTweak(screen.pos, ability, PacketRelicTweak.Operation.REROLL));
    }

    @Override
    public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, AbilityDescriptionScreen.TEXTURE);

        manager.bindForSetup(AbilityDescriptionScreen.TEXTURE);

        if (RelicItem.mayPlayerReroll(MC.player, screen.stack, ability)) {
            blit(poseStack, x, y, 306, 0, 22, 22, 512, 512);

            if (isHovered)
                blit(poseStack, x - 1, y - 1, 330, 0, 24, 24, 512, 512);
        } else {
            blit(poseStack, x, y, 306, 24, 22, 22, 512, 512);

            if (isHovered)
                blit(poseStack, x - 1, y - 1, 330, 24, 24, 24, 512, 512);
        }
    }
}