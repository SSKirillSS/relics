package it.hurts.sskirillss.relics.client.screen.description.widgets.relic;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.ITickingWidget;
import it.hurts.sskirillss.relics.client.screen.description.RelicDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.data.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionUtils;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;

import java.awt.*;
import java.util.List;

public class RelicExperienceWidget extends AbstractDescriptionWidget implements IHoverableWidget, ITickingWidget {
    private static final int FILLER_WIDTH = 125;

    private final RelicDescriptionScreen screen;

    public RelicExperienceWidget(int x, int y, RelicDescriptionScreen screen) {
        super(x, y, 139, 15);

        this.screen = screen;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !(screen.stack.getItem() instanceof IRelicItem relic))
            return;

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        float color = (float) (1.025F + (Math.sin(player.tickCount * 0.5F) * 0.05F));

        RenderSystem.setShaderColor(color, color, color, 1F);
        RenderSystem.enableBlend();

        guiGraphics.blit(DescriptionTextures.EXPERIENCE_FILLER, getX() + 3, getY() + 2, 0, 0, calculateFillerWidth(relic), 11, FILLER_WIDTH, 11);

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        if (isHovered())
            guiGraphics.blit(DescriptionTextures.EXPERIENCE_OUTLINE, getX() - 1, getY() - 6, 0, 0, 141, 23, 141, 23);

        poseStack.scale(0.5F, 0.5F, 0.5F);

        MutableComponent percentage = Component.literal(relic.isMaxLevel(screen.stack) ? "MAX" : MathUtils.round(calculateFillerPercentage(relic), 1) + "%").withStyle(ChatFormatting.BOLD);

        guiGraphics.drawString(MC.font, percentage, (getX() + 67) * 2 - (MC.font.width(percentage) / 2), (getY() + 6) * 2, 0x662f13, false);

        poseStack.popPose();
    }

    @Override
    public void onTick() {
        if (!(screen.stack.getItem() instanceof IRelicItem relic) || MC.player == null)
            return;

        RandomSource random = MC.player.getRandom();

        int fillerWidth = calculateFillerWidth(relic);

        if (MC.player.tickCount % 5 == 0) {
            for (float i = 0; i < fillerWidth / 40F; i++) {
                ParticleStorage.addParticle(screen, new ExperienceParticleData(new Color(200, 255, 0),
                        getX() + 5 + random.nextInt(fillerWidth), getY() + random.nextInt(2), 1F + (random.nextFloat() * 0.25F), 50 + random.nextInt(50)));
            }
        }
    }

    @Override
    public void onHovered(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!(screen.stack.getItem() instanceof IRelicItem relic))
            return;

        PoseStack poseStack = guiGraphics.pose();

        List<FormattedCharSequence> tooltip = Lists.newArrayList();

        int maxWidth = 150;
        int renderWidth = 0;

        int level = relic.getLevel(screen.stack);

        List<MutableComponent> entries = Lists.newArrayList(
                Component.literal("").append(Component.translatable("tooltip.relics.researching.relic.experience.title").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE))
                        .append(" " + (relic.isMaxLevel(screen.stack) ? "MAX" : relic.getExperience(screen.stack) + "/" + relic.getExperienceBetweenLevels(level, level + 1))),
                Component.literal(" ")
        );

        if (Screen.hasShiftDown())
            entries.add(Component.translatable("tooltip.relics.researching.relic.experience.extra_info").withStyle(ChatFormatting.ITALIC));
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
    public void playDownSound(SoundManager handler) {

    }

    private float calculateFillerPercentage(IRelicItem relic) {
        int level = relic.getLevel(screen.stack);

        return relic.getExperience(screen.stack) / (relic.getExperienceBetweenLevels(level, level + 1) / 100F);
    }

    private int calculateFillerWidth(IRelicItem relic) {
        return relic.isMaxLevel(screen.stack) ? FILLER_WIDTH : (int) Math.ceil(calculateFillerPercentage(relic) / 100F * FILLER_WIDTH);
    }
}