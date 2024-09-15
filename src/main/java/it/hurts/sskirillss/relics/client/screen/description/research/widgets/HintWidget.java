package it.hurts.sskirillss.relics.client.screen.description.research.widgets;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.ITickingWidget;
import it.hurts.sskirillss.relics.client.screen.description.general.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.relic.particles.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.description.research.AbilityResearchScreen;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import it.hurts.sskirillss.relics.utils.data.GUIRenderer;
import it.hurts.sskirillss.relics.utils.data.SpriteOrientation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.util.RandomSource;

import java.awt.*;
import java.util.Map;

public class HintWidget extends AbstractDescriptionWidget implements IHoverableWidget, ITickingWidget {
    private final AbilityResearchScreen screen;

    public HintWidget(int x, int y, AbilityResearchScreen screen) {
        super(x, y, 87, 22);

        this.screen = screen;
    }

    @Override
    public void onPress() {
        if (!(screen.stack.getItem() instanceof IRelicItem relic))
            return;

        research(Screen.hasShiftDown() ? relic.getResearchData(screen.ability).getLinks().size() : 1);
    }

    public void research(int amount) {
        if (!(screen.stack.getItem() instanceof IRelicItem relic))
            return;

        Multimap<Integer, Integer> pattern = relic.getResearchData(screen.ability).getLinks();
        Multimap<Integer, Integer> links = relic.getResearchLinks(screen.stack, screen.ability);

        int iteration = 0;

        for (Map.Entry<Integer, Integer> entry : links.entries()) {
            Integer start = entry.getKey();
            Integer end = entry.getValue();

            if (!(pattern.containsEntry(start, end) || pattern.containsEntry(end, start))) {
                screen.removeLink(start, end);

                if (++iteration >= amount)
                    break;
            }
        }

        iteration = 0;

        for (Map.Entry<Integer, Integer> entry : pattern.entries()) {
            Integer start = entry.getKey();
            Integer end = entry.getValue();

            if (!(links.containsEntry(start, end) || links.containsEntry(end, start))) {
                screen.addLink(start, end);

                if (++iteration >= amount)
                    break;
            }
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !(screen.stack.getItem() instanceof IRelicItem))
            return;

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        GUIRenderer.begin(DescriptionTextures.HINT_BACKGROUND, poseStack)
                .orientation(SpriteOrientation.TOP_LEFT)
                .pos(getX(), getY() - 10)
                .end();

        if (isHovered()) {
            GUIRenderer.begin(DescriptionTextures.HINT_OUTLINE, poseStack)
                    .orientation(SpriteOrientation.TOP_LEFT)
                    .pos(getX() - 1, getY() - 6)
                    .end();

            float color = (float) (1.025F + (Math.sin(player.tickCount + pPartialTick) * 0.05F));

            if (Screen.hasShiftDown())
                GUIRenderer.begin(DescriptionTextures.BULB_BURNING, poseStack)
                        .orientation(SpriteOrientation.TOP_LEFT)
                        .pos(getX() + 34, getY() - 17)
                        .patternSize(16, 34)
                        .color(color, color, color, 1F)
                        .animation(AnimationData.builder()
                                .frame(0, 2).frame(1, 2).frame(2, 2)
                                .frame(3, 2).frame(4, 2).frame(5, 2)
                                .frame(6, 2).frame(7, 2))
                        .end();
            else
                GUIRenderer.begin(DescriptionTextures.BULB_GLOWING, poseStack)
                        .orientation(SpriteOrientation.TOP_LEFT)
                        .pos(getX() + 34, getY() - 3)
                        .color(color, color, color, 1F)
                        .end();
        } else
            GUIRenderer.begin(DescriptionTextures.BULB, poseStack)
                    .orientation(SpriteOrientation.TOP_LEFT)
                    .pos(getX() + 34, getY() - 3)
                    .end();

        poseStack.popPose();
    }

    @Override
    public void onTick() {
        if (!isHovered() || !(screen.stack.getItem() instanceof IRelicItem) || minecraft.player == null)
            return;

        RandomSource random = minecraft.player.getRandom();

        if (minecraft.player.tickCount % 5 == 0) {
            ParticleStorage.addParticle(screen, new ExperienceParticleData(new Color(255, 200 + random.nextInt(50), random.nextInt(50)),
                    getX() + 34 + random.nextInt(16), getY() - 6 + random.nextInt(16), 1F + (random.nextFloat() * 0.25F), 50 + random.nextInt(50)));
        }
    }

    @Override
    public void onHovered(GuiGraphics guiGraphics, int mouseX, int mouseY) {
//        if (!(screen.stack.getItem() instanceof IRelicItem relic))
//            return;
//
//        PoseStack poseStack = guiGraphics.pose();
//
//        List<FormattedCharSequence> tooltip = Lists.newArrayList();
//
//        int maxWidth = 150;
//        int renderWidth = 0;
//
//        int level = relic.getRelicLevel(screen.stack);
//
//        List<MutableComponent> entries = Lists.newArrayList(
//                Component.literal("").append(Component.translatable("tooltip.relics.researching.relic.experience.title").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE))
//                        .append(" " + (relic.isRelicMaxLevel(screen.stack) ? "MAX" : relic.getRelicExperience(screen.stack) + "/" + relic.getTotalRelicExperienceBetweenLevels(level, level + 1))),
//                Component.literal(" ")
//        );
//
//        if (Screen.hasShiftDown())
//            entries.add(Component.translatable("tooltip.relics.researching.relic.experience.extra_info").withStyle(ChatFormatting.ITALIC));
//        else
//            entries.add(Component.translatable("tooltip.relics.researching.general.extra_info"));
//
//        for (MutableComponent entry : entries) {
//            int entryWidth = (minecraft.font.width(entry) / 2);
//
//            if (entryWidth > renderWidth)
//                renderWidth = Math.min(entryWidth + 2, maxWidth);
//
//            tooltip.addAll(minecraft.font.split(entry, maxWidth * 2));
//        }
//
//        poseStack.pushPose();
//
//        poseStack.translate(0F, 0F, 100);
//
//        DescriptionUtils.drawTooltipBackground(guiGraphics, renderWidth, tooltip.size() * 5, mouseX - 9 - (renderWidth / 2), mouseY);
//
//        poseStack.scale(0.5F, 0.5F, 0.5F);
//
//        int yOff = 0;
//
//        for (FormattedCharSequence entry : tooltip) {
//            guiGraphics.drawString(minecraft.font, entry, ((mouseX - renderWidth / 2) + 1) * 2, ((mouseY + yOff + 9) * 2), 0x662f13, false);
//
//            yOff += 5;
//        }
//
//        poseStack.popPose();
    }

    @Override
    public void playDownSound(SoundManager handler) {

    }
}