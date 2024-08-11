package it.hurts.sskirillss.relics.client.screen.description.widgets.general;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.IRelicScreenProvider;
import it.hurts.sskirillss.relics.client.screen.base.ITickingWidget;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.utils.Reference;
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

        poseStack.translate(Math.sin((MC.player.tickCount + pPartialTick + icon.length() * 10) * 0.075F), Math.cos((MC.player.tickCount + pPartialTick + icon.length() * 10) * 0.075F) * 0.5F, 0);

        guiGraphics.blit(DescriptionTextures.PLATE_BACKGROUND, getX(), getY(), 0, 0, width, height, width, height);

        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/general/icons/" + icon + ".png"), getX() + 3, getY() + 3, 0, 0, 14, 14, 14, 14);

        MutableComponent value = Component.literal(getValue(provider.getStack())).withStyle(ChatFormatting.BOLD);

        guiGraphics.drawString(MC.font, value, getX() + 19, getY() + 6, 0xffe278, true);

        renderContent(guiGraphics, pMouseX, pMouseY, pPartialTick);

        if (isHovered())
            guiGraphics.blit(DescriptionTextures.PLATE_OUTLINE, getX(), getY(), 0, 0, width, height, width, height);

        poseStack.popPose();
    }

    public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    public void playDownSound(SoundManager handler) {

    }
}