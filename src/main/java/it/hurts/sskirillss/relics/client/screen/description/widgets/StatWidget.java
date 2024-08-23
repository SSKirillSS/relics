package it.hurts.sskirillss.relics.client.screen.description.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class StatWidget extends AbstractDescriptionWidget {
    private AbilityDescriptionScreen screen;
    private final String stat;

    public StatWidget(int x, int y, AbilityDescriptionScreen screen, String stat) {
        super(x, y, 209, 16);

        this.screen = screen;
        this.stat = stat;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        ItemStack stack = screen.stack;
        String ability = screen.ability;

        LocalPlayer player = MC.player;

        if (!(stack.getItem() instanceof IRelicItem relic) || player == null)
            return;

        StatData statData = relic.getStatData(ability, stat);

        PoseStack poseStack = guiGraphics.pose();

        int xOff = 0;
        int yOff = 0;

        {
            poseStack.pushPose();

            StatIcon icon = statData.getIcon();

            Color color = new Color(icon.getColor());

            float blinkOffset = (float) (Math.sin((player.tickCount + (stat.length() * 10)) * 0.2F) * 0.1F);

            RenderSystem.setShaderColor(color.getRed() / 255F + blinkOffset, color.getGreen() / 255F + blinkOffset, color.getBlue() / 255F + blinkOffset, 1F);
            RenderSystem.enableBlend();

            guiGraphics.blit(icon.getPath(), getX() + 8, getY(), 0, 0, 15, height, 15, height);

            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            poseStack.popPose();
        }

        {
            boolean isLocked = !relic.canUseAbility(stack, ability);

            boolean isHoveredUpgrade = !isLocked && screen.upgradeButton.isHovered();
            boolean isHoveredReroll = !isLocked && screen.rerollButton.isHovered();
            boolean isHoveredReset = !isLocked && screen.resetButton.isHovered();

            int maxLevel = relic.getAbilityData(ability).getMaxLevel();
            int level = relic.getAbilityLevel(stack, ability);

            MutableComponent cost = Component.literal(String.valueOf(statData.getFormatValue().apply(relic.getStatValue(stack, ability, stat))));

            if (isHoveredUpgrade && level < maxLevel) {
                cost.append(" ➠ " + statData.getFormatValue().apply(relic.getStatValue(stack, ability, stat, level + 1)));
            }

            if (isHoveredReroll) {
                cost.append(" ➠ ").append(Component.literal("X.XXX").withStyle(ChatFormatting.OBFUSCATED));
            }

            if (isHoveredReset && level > 0) {
                cost.append(" ➠ " + statData.getFormatValue().apply(relic.getStatValue(stack, ability, stat, 0)));
            }

            poseStack.pushPose();

            poseStack.scale(0.5F, 0.5F, 0.5F);

            guiGraphics.drawString(MC.font, Component.translatable("tooltip.relics." + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + ".ability." + ability + ".stat." + stat + ".title").withStyle(ChatFormatting.BOLD), (getX() + 27) * 2, (getY() + 3) * 2, 0x662f13, false);

            guiGraphics.drawString(MC.font, Component.literal("● ").append(Component.translatable("tooltip.relics." + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + ".ability." + ability + ".stat." + stat + ".value", cost)), (getX() + 33) * 2, (getY() + 9) * 2, 0x662f13, false);

            poseStack.popPose();
        }

        {
            for (int i = 0; i < 5; i++) {
                guiGraphics.blit(DescriptionTextures.SMALL_STAR_HOLE, getX() + xOff + width - 32, getY() + yOff + 3, 0, 0, 4, 4, 4, 4);

                xOff += 5;
            }

            xOff = 0;

            int quality = relic.getStatQuality(stack, ability, stat);
            boolean isAliquot = quality % 2 == 1;

            for (int i = 0; i < Math.floor(quality / 2D); i++) {
                guiGraphics.blit(DescriptionTextures.SMALL_STAR_ACTIVE, getX() + xOff + width - 32, getY() + yOff + 3, 0, 0, 4, 4, 4, 4);

                xOff += 5;
            }

            if (isAliquot)
                guiGraphics.blit(DescriptionTextures.SMALL_STAR_ACTIVE, getX() + xOff + width - 32, getY() + yOff + 3, 0, 0, 2, 4, 4, 4);
        }
    }

    @Override
    public void playDownSound(SoundManager handler) {

    }
}