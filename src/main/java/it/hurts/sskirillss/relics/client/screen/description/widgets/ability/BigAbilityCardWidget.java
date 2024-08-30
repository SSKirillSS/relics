package it.hurts.sskirillss.relics.client.screen.description.widgets.ability;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IRelicScreenProvider;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionUtils;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.base.AbstractBigCardWidget;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BigAbilityCardWidget extends AbstractBigCardWidget {
    private final String ability;

    public BigAbilityCardWidget(int x, int y, IRelicScreenProvider provider, String ability) {
        super(x, y, provider);

        this.ability = ability;
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
                Component.literal("").append(Component.translatable("tooltip.relics.researching.ability.info.level").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE)).append(" " + relic.getAbilityLevel(stack, ability) + "/" + relic.getAbilityData(ability).getMaxLevel()),
                Component.literal("").append(Component.translatable("tooltip.relics.researching.ability.info.quality").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE)).append(" " + MathUtils.round(relic.getAbilityQuality(stack, ability) / 2F, 1) + "/" + relic.getMaxQuality() / 2),
                Component.literal(" ")
        );

        if (Screen.hasShiftDown())
            entries.add(Component.translatable("tooltip.relics.researching.ability.info.extra_info").withStyle(ChatFormatting.ITALIC));
        else
            entries.add(Component.translatable("tooltip.relics.researching.general.extra_info"));

        for (MutableComponent entry : entries) {
            int entryWidth = (minecraft.font.width(entry) / 2);

            if (entryWidth > renderWidth)
                renderWidth = Math.min(entryWidth + 2, maxWidth);

            tooltip.addAll(minecraft.font.split(entry, maxWidth * 2));
        }

        poseStack.pushPose();

        poseStack.translate(0F, 0F, 400);

        DescriptionUtils.drawTooltipBackground(guiGraphics, renderWidth, tooltip.size() * 5, mouseX - 9 - (renderWidth / 2), mouseY);

        poseStack.scale(0.5F, 0.5F, 0.5F);

        int yOff = 0;

        for (FormattedCharSequence entry : tooltip) {
            guiGraphics.drawString(minecraft.font, entry, ((mouseX - renderWidth / 2) + 1) * 2, ((mouseY + yOff + 9) * 2), 0x662f13, false);

            yOff += 5;
        }

        poseStack.popPose();
    }
}