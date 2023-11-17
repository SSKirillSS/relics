package it.hurts.sskirillss.relics.client.screen.description.widgets.ability;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.ITickingWidget;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.data.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.client.screen.utils.ScreenUtils;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketRelicTweak;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;

import java.awt.*;
import java.util.List;

public class AbilityRerollButtonWidget extends AbstractDescriptionWidget implements IHoverableWidget, ITickingWidget {
    private final AbilityDescriptionScreen screen;
    private final String ability;

    public AbilityRerollButtonWidget(int x, int y, AbilityDescriptionScreen screen, String ability) {
        super(x, y, 17, 17);

        this.screen = screen;
        this.ability = ability;
    }

    @Override
    public boolean isLocked() {
        return !AbilityUtils.mayPlayerReroll(MC.player, screen.stack, ability);
    }

    @Override
    public void playDownSound(SoundManager handler) {
        if (!isLocked())
            handler.play(SimpleSoundInstance.forUI(SoundRegistry.TABLE_REROLL.get(), 1F));
    }

    @Override
    public void onPress() {
        if (!isLocked())
            NetworkHandler.sendToServer(new PacketRelicTweak(screen.pos, ability, PacketRelicTweak.Operation.REROLL));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, AbilityDescriptionScreen.TEXTURE);

        manager.bindForSetup(AbilityDescriptionScreen.TEXTURE);

        guiGraphics.blit(AbilityDescriptionScreen.TEXTURE, getX(), getY(), isLocked() ? 320 : 302, 88, width, height, 512, 512);

        if (isHovered) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/gui/description/reroll_highlight_" + (isLocked() ? "locked" : "unlocked") + ".png"));

            RenderSystem.enableBlend();

            RenderUtils.renderTextureFromCenter(guiGraphics.pose(), getX() + width / 2, getY() + height / 2, 32, 384, 32, 32, 1F, AnimationData.builder()
                    .frame(0, 2)
                    .frame(1, 2)
                    .frame(2, 2)
                    .frame(3, 2)
                    .frame(4, 2)
                    .frame(5, 2)
                    .frame(6, 2)
                    .frame(7, 2)
                    .frame(8, 2)
                    .frame(9, 2)
                    .frame(10, 2)
                    .frame(11, 2)
            );

            RenderSystem.disableBlend();
        }
    }

    @Override
    public void onHovered(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!AbilityUtils.canUseAbility(screen.stack, ability))
            return;

        RelicAbilityEntry data = AbilityUtils.getRelicAbilityEntry(screen.stack.getItem(), ability);

        if (data.getStats().isEmpty())
            return;

        PoseStack poseStack = guiGraphics.pose();

        List<FormattedCharSequence> tooltip = Lists.newArrayList();

        int maxWidth = 100;
        int renderWidth = 0;

        int requiredExperience = AbilityUtils.getRerollRequiredExperience(screen.stack, ability);

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

        ScreenUtils.drawTexturedTooltipBorder(guiGraphics, new ResourceLocation(Reference.MODID, "textures/gui/tooltip/border/paper.png"),
                renderWidth, height, renderX, renderY);

        int yOff = 0;

        poseStack.scale(0.5F, 0.5F, 0.5F);

        for (FormattedCharSequence entry : tooltip) {
            guiGraphics.drawString(MC.font, entry, (renderX + 9) * 2, (renderY + 9 + yOff) * 2, 0x412708, false);

            yOff += 5;
        }

        poseStack.scale(1F, 1F, 1F);
    }

    @Override
    public void onTick() {
        RandomSource random = MC.player.getRandom();

        if (isHoveredOrFocused()) {
            if (screen.ticksExisted % 10 == 0)
                ParticleStorage.addParticle(screen, new ExperienceParticleData(isLocked()
                        ? new Color(100 + random.nextInt(100), 100 + random.nextInt(100), 100 + random.nextInt(100))
                        : new Color(200 + random.nextInt(50), 150 + random.nextInt(100), 0),
                        getX() + random.nextInt(width), getY() + random.nextInt(height),
                        0.15F + (random.nextFloat() * 0.25F), 100 + random.nextInt(50)));
        }
    }
}