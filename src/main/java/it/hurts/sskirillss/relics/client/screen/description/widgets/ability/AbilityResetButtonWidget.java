package it.hurts.sskirillss.relics.client.screen.description.widgets.ability;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.ITickingWidget;
import it.hurts.sskirillss.relics.client.screen.description.AbilityDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.RelicDescriptionScreen;
import it.hurts.sskirillss.relics.client.screen.description.data.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.client.screen.utils.ScreenUtils;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketRelicTweak;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;


import java.awt.Color;
import java.util.List;
import java.util.Random;

public class AbilityResetButtonWidget extends AbstractDescriptionWidget implements IHoverableWidget, ITickingWidget {
    private final AbilityDescriptionScreen screen;
    private final String ability;

    public AbilityResetButtonWidget(int x, int y, AbilityDescriptionScreen screen, String ability) {
        super(x, y, 17, 17);

        this.screen = screen;
        this.ability = ability;
    }

    @Override
    public boolean isLocked() {
        return !(screen.stack.getItem() instanceof IRelicItem relic) || !relic.mayPlayerReset(MC.player, screen.stack, ability);
    }

    @Override
    public void playDownSound(SoundManager handler) {
        if (!isLocked())
            handler.play(SimpleSoundInstance.forUI(SoundRegistry.TABLE_RESET.get(), 1F));
    }

    @Override
    public void onPress() {
        if (!isLocked())
            NetworkHandler.sendToServer(new PacketRelicTweak(screen.pos, ability, PacketRelicTweak.Operation.RESET));
    }

    @Override
    public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, AbilityDescriptionScreen.TEXTURE);

        manager.bindForSetup(AbilityDescriptionScreen.TEXTURE);

        blit(poseStack, x, y, isLocked() ? 320 : 302, 106, width, height, 512, 512);

        if (isHovered) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/gui/description/reset_highlight_" + (isLocked() ? "locked" : "unlocked") + ".png"));

            RenderSystem.enableBlend();

            RenderUtils.renderAnimatedTextureFromCenter(poseStack, x + width / 2f, y + height / 2f, 32, 384, 32, 32, 1F, AnimationData.builder()
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
    public void onTick() {
        Random random = MC.player.getRandom();

        if (isHoveredOrFocused()) {
            if (screen.ticksExisted % 10 == 0)
                ParticleStorage.addParticle(screen, new ExperienceParticleData(isLocked()
                        ? new Color(100 + random.nextInt(100), 100 + random.nextInt(100), 100 + random.nextInt(100))
                        : new Color(200 + random.nextInt(50), 150 + random.nextInt(100), 0),
                        x + random.nextInt(width), y + random.nextInt(height),
                        0.15F + (random.nextFloat() * 0.25F), 100 + random.nextInt(50)));
        }
    }

    @Override
    public void onHovered(PoseStack poseStack, int mouseX, int mouseY) {
        if (!(screen.stack.getItem() instanceof IRelicItem relic) || !relic.canUseAbility(screen.stack, ability))
            return;

        AbilityData data = relic.getAbilityData(ability);

        if (data.getStats().isEmpty())
            return;

        List<FormattedCharSequence> tooltip = Lists.newArrayList();

        int maxWidth = 100;
        int renderWidth = 0;

        int requiredExperience = relic.getResetRequiredExperience(screen.stack, ability);

        int experience = MC.player.totalExperience;

        MutableComponent negativeStatus = new TranslatableComponent("tooltip.relics.relic.status.negative").withStyle(ChatFormatting.RED);
        MutableComponent positiveStatus = new TranslatableComponent("tooltip.relics.relic.status.positive").withStyle(ChatFormatting.GREEN);

        List<MutableComponent> entries = Lists.newArrayList(
                new TranslatableComponent("tooltip.relics.relic.reset.description").withStyle(ChatFormatting.BOLD),
                new TextComponent(" "));

        if (relic.getAbilityPoints(screen.stack, ability) > 0)
            entries.add(new TranslatableComponent("tooltip.relics.relic.reset.cost", requiredExperience,
                    (requiredExperience > experience ? negativeStatus : positiveStatus)));
        else
            entries.add(new TextComponent("▶ ").append(new TranslatableComponent("tooltip.relics.relic.reset.locked")));

        for (MutableComponent entry : entries) {
            int entryWidth = (MC.font.width(entry) + 4) / 2;

            if (entryWidth > renderWidth)
                renderWidth = Math.min(entryWidth, maxWidth);

            tooltip.addAll(MC.font.split(entry, maxWidth * 2));
        }

        int height = Math.round(tooltip.size() * 4.5F);

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