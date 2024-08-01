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

public class UpgradeActionWidget extends AbstractActionWidget {
    public UpgradeActionWidget(int x, int y, AbilityDescriptionScreen screen, String ability) {
        super(x, y, PacketRelicTweak.Operation.UPGRADE, screen, ability);
    }

    @Override
    public boolean isLocked() {
        return !(getProvider().getStack().getItem() instanceof IRelicItem relic) || !relic.mayPlayerUpgrade(MC.player, getProvider().getStack(), getAbility());
    }

    @Override
    public void playDownSound(SoundManager handler) {
        if (getProvider().getStack().getItem() instanceof IRelicItem relic && !isLocked()) {
            int level = relic.getAbilityPoints(getProvider().getStack(), getAbility());
            int maxLevel = relic.getAbilityData(getAbility()).getMaxLevel();

            handler.play(SimpleSoundInstance.forUI(SoundRegistry.TABLE_UPGRADE.get(), 1F + ((float) level / maxLevel)));
        }
    }

    @Override
    public void onHovered(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!(getProvider().getStack().getItem() instanceof IRelicItem relic) || !relic.canUseAbility(getProvider().getStack(), getAbility()))
            return;

        AbilityData data = relic.getAbilityData(getAbility());

        if (data.getStats().isEmpty())
            return;

        List<FormattedCharSequence> tooltip = Lists.newArrayList();

        PoseStack poseStack = guiGraphics.pose();

        int maxWidth = 100;
        int renderWidth = 0;

        int requiredPoints = data.getRequiredPoints();
        int requiredExperience = relic.getUpgradeRequiredExperience(getProvider().getStack(), getAbility());

        int points = relic.getPoints(getProvider().getStack());
        int experience = MC.player.totalExperience;

        MutableComponent negativeStatus = Component.translatable("tooltip.relics.relic.status.negative");
        MutableComponent positiveStatus = Component.translatable("tooltip.relics.relic.status.positive");

        List<MutableComponent> entries = Lists.newArrayList(
                Component.translatable("tooltip.relics.relic.upgrade.description").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE),
                Component.literal(" "));

        if (!relic.isAbilityMaxLevel(getProvider().getStack(), getAbility()))
            entries.add(Component.translatable("tooltip.relics.relic.upgrade.cost", requiredPoints,
                    (requiredPoints > points ? negativeStatus : positiveStatus), requiredExperience,
                    (requiredExperience > experience ? negativeStatus : positiveStatus)));
        else
            entries.add(Component.literal("▶ ").append(Component.translatable("tooltip.relics.relic.upgrade.locked")));

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