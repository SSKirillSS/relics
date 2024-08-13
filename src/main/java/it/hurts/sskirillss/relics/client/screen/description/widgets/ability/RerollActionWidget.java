package it.hurts.sskirillss.relics.client.screen.description.widgets.ability;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionUtils;
import it.hurts.sskirillss.relics.client.screen.description.widgets.ability.base.AbstractActionWidget;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketRelicTweak;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class RerollActionWidget extends AbstractActionWidget {
    public RerollActionWidget(int x, int y, AbilityDescriptionScreen screen, String ability) {
        super(x, y, PacketRelicTweak.Operation.REROLL, screen, ability);
    }

    @Override
    public boolean isLocked() {
        return !(getProvider().getStack().getItem() instanceof IRelicItem relic) || !relic.mayPlayerReroll(MC.player, getProvider().getStack(), getAbility());
    }

    @Override
    public void playDownSound(SoundManager handler) {
        if (!isLocked())
            handler.play(SimpleSoundInstance.forUI(SoundRegistry.TABLE_REROLL.get(), 1F));
    }

    @Override
    public void onHovered(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!(getProvider().getStack().getItem() instanceof IRelicItem relic) || !relic.canUseAbility(getProvider().getStack(), getAbility()))
            return;

        AbilityData data = relic.getAbilityData(getAbility());

        if (data.getStats().isEmpty())
            return;

        PoseStack poseStack = guiGraphics.pose();

        List<FormattedCharSequence> tooltip = Lists.newArrayList();

        int maxWidth = 100;
        int renderWidth = 0;

        int requiredLevel = relic.getRerollRequiredLevel(getProvider().getStack(), getAbility());

        int level = MC.player.experienceLevel;

        MutableComponent negativeStatus = Component.translatable("tooltip.relics.relic.status.negative").withStyle(ChatFormatting.RED);
        MutableComponent positiveStatus = Component.translatable("tooltip.relics.relic.status.positive").withStyle(ChatFormatting.GREEN);

        List<MutableComponent> entries = Lists.newArrayList(
                Component.translatable("tooltip.relics.relic.reroll.description").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE),
                Component.literal(" "),
                Component.translatable("tooltip.relics.relic.reroll.cost", requiredLevel,
                        (requiredLevel > level ? negativeStatus : positiveStatus)));

        for (MutableComponent entry : entries) {
            int entryWidth = (MC.font.width(entry) + 4) / 2;

            if (entryWidth > renderWidth)
                renderWidth = Math.min(entryWidth, maxWidth);

            tooltip.addAll(MC.font.split(entry, maxWidth * 2));
        }

        int height = Math.round(tooltip.size() * 5F);

        int renderX = getX() + width + 1;
        int renderY = mouseY - (height / 2) - 9;

        DescriptionUtils.drawTooltipBackground(guiGraphics, renderWidth, height, renderX, renderY);

        int yOff = 0;

        poseStack.scale(0.5F, 0.5F, 0.5F);

        for (FormattedCharSequence entry : tooltip) {
            guiGraphics.drawString(MC.font, entry, (renderX + 10) * 2, (renderY + 9 + yOff) * 2, 0x662f13, false);

            yOff += 5;
        }

        poseStack.scale(1F, 1F, 1F);
    }
}