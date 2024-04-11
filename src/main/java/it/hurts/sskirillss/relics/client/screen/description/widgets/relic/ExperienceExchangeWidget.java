package it.hurts.sskirillss.relics.client.screen.description.widgets.relic;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.ITickingWidget;
import it.hurts.sskirillss.relics.client.screen.description.RelicDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.data.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.client.screen.utils.ScreenUtils;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketExperienceExchange;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;


import java.awt.Color;
import java.util.List;
import java.util.Random;

public class ExperienceExchangeWidget extends AbstractDescriptionWidget implements ITickingWidget, IHoverableWidget {
    private final RelicDescriptionScreen screen;

    private int exchangeSpeed = 1;

    public ExperienceExchangeWidget(int x, int y, RelicDescriptionScreen screen) {
        super(x, y, 12, 16);

        this.screen = screen;
    }

    @Override
    public boolean isLocked() {
        return !(screen.stack.getItem() instanceof IRelicItem relic) || !relic.isExchangeAvailable(MC.player, screen.stack) || relic.isMaxLevel(screen.stack);
    }

    @Override
    public void onPress() {
        this.setFocused(true);

        if (isLocked())
            exchangeSpeed = 1;
    }

    @Override
    public void onTick() {
        LocalPlayer player = MC.player;

        if (player == null)
            return;

        Random random = player.getRandom();

        if (isHoveredOrFocused()) {
            if (screen.ticksExisted % 10 == 0)
                ParticleStorage.addParticle(screen, new ExperienceParticleData(isLocked()
                        ? new Color(100 + random.nextInt(100), 100 + random.nextInt(100), 100 + random.nextInt(100))
                        : new Color(200 + random.nextInt(50), 150 + random.nextInt(100), 0),
                        x + random.nextInt(width), y + random.nextInt(height),
                        0.15F + (random.nextFloat() * 0.25F), 100 + random.nextInt(50)));

            screen.gatherData();
        }

        if (isLocked() || !(isFocused() && isHovered))
            return;

        if (player.experienceLevel > 0 || player.totalExperience > 0) {
            if (screen.ticksExisted % 2 == 0)
                exchangeSpeed += 1;

            NetworkHandler.sendToServer(new PacketExperienceExchange(screen.pos, (int) Math.ceil(exchangeSpeed / 4F)));

            MC.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1F + (exchangeSpeed * 0.02F)));
        }
    }

    @Override
    public void onRelease(double pMouseX, double pMouseY) {
        this.setFocused(false);

        exchangeSpeed = 1;
    }

    @Override
    public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, RelicDescriptionScreen.TEXTURE);

        manager.bindForSetup(RelicDescriptionScreen.TEXTURE);

        boolean isLocked = isLocked();

        blit(poseStack, x, y, isLocked ? 407 : 394, 1, width, height, 512, 512);

        if (isHovered) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/gui/description/exchange_highlight_" + (isLocked ? "locked" : "unlocked") + ".png"));

            RenderSystem.enableBlend();

            RenderUtils.renderAnimatedTextureFromCenter(poseStack, x + 6, y + 7, 32, 384, 32, 32, 1F, AnimationData.builder()
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
    public void onHovered(PoseStack poseStack, int mouseX, int mouseY) {
        if (!(screen.stack.getItem() instanceof IRelicItem relic))
            return;

        RelicData data = relic.getRelicData();

        if (data == null)
            return;

        List<FormattedCharSequence> tooltip = Lists.newArrayList();

        int maxWidth = 100;
        int renderWidth = 0;

        int cost = relic.getExchangeCost(screen.stack);

        int experience = MC.player.totalExperience;

        MutableComponent negativeStatus = new TranslatableComponent("tooltip.relics.relic.status.negative").withStyle(ChatFormatting.RED);
        MutableComponent positiveStatus = new TranslatableComponent("tooltip.relics.relic.status.positive").withStyle(ChatFormatting.GREEN);

        List<MutableComponent> entries = Lists.newArrayList(
                new TranslatableComponent("tooltip.relics.relic.exchange.description").withStyle(ChatFormatting.BOLD),
                new TextComponent(" ")
        );

        if (relic.isMaxLevel(screen.stack))
            entries.add(new TextComponent("▶ ").append(new TranslatableComponent("tooltip.relics.relic.exchange.locked")));
        else
            entries.add(new TranslatableComponent("tooltip.relics.relic.exchange.cost", cost,
                    (cost >= experience ? negativeStatus : positiveStatus)));

        for (MutableComponent entry : entries) {
            int entryWidth = (MC.font.width(entry) + 4) / 2;

            if (entryWidth > renderWidth)
                renderWidth = Math.min(entryWidth, maxWidth);

            tooltip.addAll(MC.font.split(entry, maxWidth * 2));
        }

        int height = Math.round(tooltip.size() * 5);

        int renderX = x + width + 1;
        int renderY = mouseY - (height / 2) - 9;

        ScreenUtils.drawTexturedTooltipBorder(poseStack, RelicDescriptionScreen.BORDER_PAPER, renderWidth, height, renderX, renderY);

        int yOff = 0;

        poseStack.scale(0.5F, 0.5F, 0.5F);

        for (FormattedCharSequence entry : tooltip) {
            MC.font.draw(poseStack, entry, (renderX + 9) * 2, (renderY + 9 + yOff) * 2, 0x412708);

            yOff += 5;
        }

        poseStack.scale(1F, 1F, 1F);
    }
}