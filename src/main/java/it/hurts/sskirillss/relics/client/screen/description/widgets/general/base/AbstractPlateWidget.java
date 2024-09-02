package it.hurts.sskirillss.relics.client.screen.description.widgets.general.base;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.IRelicScreenProvider;
import it.hurts.sskirillss.relics.client.screen.base.ITickingWidget;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.data.GUIRenderer;
import it.hurts.sskirillss.relics.utils.data.SpriteOrientation;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractPlateWidget extends AbstractDescriptionWidget implements IHoverableWidget, ITickingWidget {
    @Getter
    private IRelicScreenProvider provider;
    @Getter
    private final String icon;

    public abstract String getValue(ItemStack stack);

    public AbstractPlateWidget(int x, int y, IRelicScreenProvider provider, String icon) {
        super(x, y, 54, 19);

        this.provider = provider;
        this.icon = icon;
    }

    @Override
    public final void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        poseStack.translate(getX() + Math.sin((minecraft.player.tickCount + pPartialTick + icon.length() * 10) * 0.075F), getY() + Math.cos((minecraft.player.tickCount + pPartialTick + icon.length() * 10) * 0.075F) * 0.5F, 0);

        GUIRenderer.begin(DescriptionTextures.PLATE_BACKGROUND, poseStack)
                .orientation(SpriteOrientation.TOP_LEFT)
                .end();

        GUIRenderer.begin(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/general/icons/" + icon + ".png"), poseStack)
                .orientation(SpriteOrientation.TOP_LEFT)
                .pos(3, 3)
                .end();

        MutableComponent value = Component.literal(getValue(provider.getStack())).withStyle(ChatFormatting.BOLD);

        guiGraphics.drawString(minecraft.font, value, 19, 6, 0xffe278, true);

        renderContent(guiGraphics, pMouseX, pMouseY, pPartialTick);

        if (isHovered())
            GUIRenderer.begin(DescriptionTextures.PLATE_OUTLINE, poseStack)
                    .orientation(SpriteOrientation.TOP_LEFT)
                    .end();

        poseStack.popPose();
    }

    public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    public void playDownSound(SoundManager handler) {

    }
}