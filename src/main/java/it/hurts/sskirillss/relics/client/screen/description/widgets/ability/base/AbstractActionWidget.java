package it.hurts.sskirillss.relics.client.screen.description.widgets.ability.base;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.IRelicScreenProvider;
import it.hurts.sskirillss.relics.client.screen.base.ITickingWidget;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.data.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.client.screen.utils.ScreenUtils;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketRelicTweak;
import it.hurts.sskirillss.relics.utils.Reference;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;

import java.awt.*;
import java.util.List;
import java.util.Locale;

public abstract class AbstractActionWidget extends AbstractDescriptionWidget implements IHoverableWidget, ITickingWidget {
    @Getter
    private final PacketRelicTweak.Operation operation;
    @Getter
    private final IRelicScreenProvider provider;
    @Getter
    private final String ability;

    public AbstractActionWidget(int x, int y, PacketRelicTweak.Operation operation, AbilityDescriptionScreen screen, String ability) {
        super(x, y, 14, 13);

        this.operation = operation;
        this.provider = screen;
        this.ability = ability;
    }

    @Override
    public abstract boolean isLocked();

    @Override
    public void onPress() {
        if (!isLocked())
            NetworkHandler.sendToServer(new PacketRelicTweak(provider.getContainer(), provider.getSlot(), ability, operation));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        String actionId = operation.toString().toLowerCase(Locale.ROOT);

        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/ability/" + actionId + "_button_" + (isLocked() ? "inactive" : "active") + ".png"), getX(), getY(), 0, 0, width, height, width, height);

        if (isHovered)
            guiGraphics.blit(DescriptionTextures.ACTION_BUTTON_OUTLINE, getX(), getY(), 0, 0, width, height, width, height);
    }

    @Override
    public void onHovered(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!(provider.getStack().getItem() instanceof IRelicItem relic))
            return;

        AbilityData data = relic.getAbilityData(ability);

        if (data.getStats().isEmpty())
            return;

        PoseStack poseStack = guiGraphics.pose();

        List<FormattedCharSequence> tooltip = Lists.newArrayList();

        int maxWidth = 100;
        int renderWidth = 0;

        int requiredExperience = relic.getRerollRequiredExperience(ability);

        int experience = MC.player.totalExperience;

        MutableComponent negativeStatus = Component.translatable("tooltip.relics.relic.status.negative").withStyle(ChatFormatting.RED);
        MutableComponent positiveStatus = Component.translatable("tooltip.relics.relic.status.positive").withStyle(ChatFormatting.GREEN);

        List<MutableComponent> entries = Lists.newArrayList(
                Component.translatable("tooltip.relics.relic.reroll.description").withStyle(ChatFormatting.BOLD),
                Component.literal(" "),
                Component.translatable("tooltip.relics.relic.reroll.cost", requiredExperience,
                        (requiredExperience > experience ? negativeStatus : positiveStatus)));

        for (MutableComponent entry : entries) {
            int entryWidth = (MC.font.width(entry) + 4) / 2;

            if (entryWidth > renderWidth)
                renderWidth = Math.min(entryWidth, maxWidth);

            tooltip.addAll(MC.font.split(entry, maxWidth * 2));
        }

        int height = Math.round(tooltip.size() * 4.5F);

        int renderX = getX() + width + 1;
        int renderY = mouseY - (height / 2) - 9;

        ScreenUtils.drawTexturedTooltipBorder(guiGraphics, ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/tooltip/border/paper.png"),
                renderWidth, height, renderX, renderY);

        int yOff = 0;

        poseStack.scale(0.5F, 0.5F, 0.5F);

        for (FormattedCharSequence entry : tooltip) {
            guiGraphics.drawString(MC.font, entry, (renderX + 9) * 2, (renderY + 9 + yOff) * 2, 0x662f13, false);

            yOff += 5;
        }

        poseStack.scale(1F, 1F, 1F);
    }


    @Override
    public void onTick() {
        if (MC.player == null)
            return;

        RandomSource random = MC.player.getRandom();

        if (isHovered()) {
            if (MC.player.tickCount % 5 == 0)
                ParticleStorage.addParticle((Screen) provider, new ExperienceParticleData(isLocked()
                        ? new Color(100 + random.nextInt(100), 100 + random.nextInt(100), 100 + random.nextInt(100))
                        : new Color(200 + random.nextInt(50), 150 + random.nextInt(100), 0),
                        getX() + random.nextInt(width), getY() + random.nextInt(height / 4),
                        1F + (random.nextFloat() * 0.25F), 50 + random.nextInt(50)));
        }
    }
}