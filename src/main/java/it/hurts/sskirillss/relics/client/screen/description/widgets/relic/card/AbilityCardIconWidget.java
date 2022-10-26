package it.hurts.sskirillss.relics.client.screen.description.widgets.relic.card;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.RelicDescriptionScreen;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.opengl.GL11;

public class AbilityCardIconWidget extends AbstractButton {
    private final Minecraft MC = Minecraft.getInstance();

    private final RelicDescriptionScreen screen;
    private final String ability;

    public AbilityCardIconWidget(int x, int y, RelicDescriptionScreen screen, String ability) {
        super(x, y, 28, 37, TextComponent.EMPTY);

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
    public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, RelicDescriptionScreen.TEXTURE);

        manager.bindForSetup(RelicDescriptionScreen.TEXTURE);

        if (RelicItem.canUseAbility(screen.stack, ability)) {
            blit(poseStack, x, y, 258, 0, 28, 37, 512, 512);

            if (isHovered)
                blit(poseStack, x - 1, y - 1, 318, 0, 30, 39, 512, 512);
        } else {
            blit(poseStack, x, y, 258, 39, 28, 37, 512, 512);

            if (isHovered)
                blit(poseStack, x - 1, y - 1, 318, 39, 30, 39, 512, 512);
        }

        ResourceLocation card = new ResourceLocation(Reference.MODID, "textures/gui/description/cards/" + screen.stack.getItem().getRegistryName().getPath() + "/" + ability + ".png");

        RenderSystem.setShaderTexture(0, card);

        manager.bindForSetup(card);

        if (GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT) == 29) {
            if (!RelicItem.canUseAbility(screen.stack, ability))
                RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, 1F);

            blit(poseStack, x + 3, y + 3, 2, 2, 20, 29, 24, 33);
        }

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, RelicDescriptionScreen.TEXTURE);

        manager.bindForSetup(RelicDescriptionScreen.TEXTURE);

        if (RelicItem.canUseAbility(screen.stack, ability))
            blit(poseStack, x, y, 288, 0, 28, 38, 512, 512);
        else
            blit(poseStack, x, y, 288, 39, 28, 38, 512, 512);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}