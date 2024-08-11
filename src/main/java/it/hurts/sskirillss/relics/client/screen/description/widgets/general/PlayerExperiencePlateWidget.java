package it.hurts.sskirillss.relics.client.screen.description.widgets.general;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IRelicScreenProvider;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionUtils;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PlayerExperiencePlateWidget extends AbstractPlateWidget {
    public PlayerExperiencePlateWidget(int x, int y, IRelicScreenProvider provider) {
        super(x, y, provider, "player_experience");
    }

    @Override
    public void renderContent(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        LocalPlayer player = MC.player;

        int barWidth = 52;
        int barHeight = 2;

        guiGraphics.blit(DescriptionTextures.PLAYER_EXPERIENCE_BACKGROUND, getX() + 1, getY() + height - 3, 0, 0, barWidth, barHeight, barWidth, barHeight);

        guiGraphics.blit(DescriptionTextures.PLAYER_EXPERIENCE_FILLER, getX() + 1, getY() + height - 3, 0, 0, (int) (barWidth * ((player.totalExperience / ((player.totalExperience / player.experienceProgress) / 100F)) / 100F)), barHeight, barWidth, barHeight);
    }

    @Override
    public void onHovered(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ItemStack stack = getProvider().getStack();

        if (!(stack.getItem() instanceof IRelicItem relic))
            return;

        PoseStack poseStack = guiGraphics.pose();

        List<FormattedCharSequence> tooltip = Lists.newArrayList();

        int maxWidth = 150;
        int renderWidth = 0;

        List<MutableComponent> entries = Lists.newArrayList(
                Component.literal("").append(Component.translatable("tooltip.relics.researching.general.player_experience.title").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE)).append(" " + MC.player.experienceLevel + " [" + MC.player.totalExperience + "]"),
                Component.literal(" ")
        );

        if (Screen.hasShiftDown())
            entries.add(Component.translatable("tooltip.relics.researching.general.player_experience.extra_info").withStyle(ChatFormatting.ITALIC));
        else
            entries.add(Component.translatable("tooltip.relics.researching.general.extra_info"));

        for (MutableComponent entry : entries) {
            int entryWidth = (MC.font.width(entry) / 2);

            if (entryWidth > renderWidth)
                renderWidth = Math.min(entryWidth + 2, maxWidth);

            tooltip.addAll(MC.font.split(entry, maxWidth * 2));
        }

        poseStack.pushPose();

        poseStack.translate(0F, 0F, 100);

        DescriptionUtils.drawTooltipBackground(guiGraphics, renderWidth, tooltip.size() * 5, mouseX - 9 - (renderWidth / 2), mouseY);

        poseStack.scale(0.5F, 0.5F, 0.5F);

        int yOff = 0;

        for (FormattedCharSequence entry : tooltip) {
            guiGraphics.drawString(MC.font, entry, ((mouseX - renderWidth / 2) + 1) * 2, ((mouseY + yOff + 9) * 2), 0x662f13, false);

            yOff += 5;
        }

        poseStack.popPose();
    }

    @Override
    public void onTick() {

    }

    @Override
    public String getValue(ItemStack stack) {
        return String.valueOf(MC.player.experienceLevel);
    }
}