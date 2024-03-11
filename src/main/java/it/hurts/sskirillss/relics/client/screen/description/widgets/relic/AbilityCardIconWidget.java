package it.hurts.sskirillss.relics.client.screen.description.widgets.relic;

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
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.QualityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.List;

public class AbilityCardIconWidget extends AbstractDescriptionWidget implements IHoverableWidget, ITickingWidget {
    private final RelicDescriptionScreen screen;
    private final String ability;

    private float scale = 1F;

    public AbilityCardIconWidget(int x, int y, RelicDescriptionScreen screen, String ability) {
        super(x, y, 30, 46);

        this.screen = screen;
        this.ability = ability;
    }

    @Override
    public void onPress() {
        MC.setScreen(new AbilityDescriptionScreen(screen.pos, screen.stack, ability));
    }

    @Override
    public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        TextureManager manager = MC.getTextureManager();

        boolean canUse = AbilityUtils.canUseAbility(screen.stack, ability);
        boolean canUpgrade = AbilityUtils.mayPlayerUpgrade(MC.player, screen.stack, ability);

        ResourceLocation card = new ResourceLocation(Reference.MODID, "textures/gui/description/cards/" + ForgeRegistries.ITEMS.getKey(screen.stack.getItem()).getPath() + "/" + ability + ".png");

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, card);

        manager.bindForSetup(card);

        poseStack.pushPose();

        poseStack.scale(scale, scale, scale);

        poseStack.translate((x + (width / 2F)) / scale, (y + (height / 2F)) / scale, 0);

        if (!canUse)
            RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, scale);
        else if (canUpgrade)
            RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, scale);

        blit(poseStack, -(20 / 2) - 1, -(29 / 2) - 2, 0, 0, 20, 29, 20, 29);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, RelicDescriptionScreen.TEXTURE);

        manager.bindForSetup(RelicDescriptionScreen.TEXTURE);

        if (canUse) {
            blit(poseStack, -(width / 2), -(height / 2), 302, 61, width, height, 512, 512);

            if (isHovered) {
                RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/gui/description/card_highlight_unlocked.png"));

                RenderSystem.enableBlend();

                RenderUtils.renderTextureFromCenter(poseStack, 0, 0, 64, 768, 64, 64, 1F, AnimationData.builder()
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
        } else {
            blit(poseStack, -(width / 2) - 1, -(height / 2), 333, 61, width + 1, height, 512, 512);

            if (isHovered) {
                RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/gui/description/card_highlight_locked.png"));

                RenderSystem.enableBlend();

                RenderUtils.renderTextureFromCenter(poseStack, 0, 0, 64, 768, 64, 64, 1F, AnimationData.builder()
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

        if (canUpgrade) {
            ResourceLocation icon = new ResourceLocation(Reference.MODID, "textures/gui/description/upgrade_available.png");

            RenderSystem.setShaderTexture(0, icon);

            manager.bindForSetup(icon);

            RenderSystem.enableBlend();

            RenderUtils.renderTextureFromCenter(poseStack, -1, -1, 32, 256, 32, 32, 0.9F + ((float) (Math.sin((screen.ticksExisted + pPartialTick) * 0.25F) * 0.025F)), AnimationData.builder()
                    .frame(0, 2)
                    .frame(1, 2)
                    .frame(2, 2)
                    .frame(3, 2)
                    .frame(4, 2)
                    .frame(5, 2)
                    .frame(6, 2)
                    .frame(7, 20)
            );

            RenderSystem.disableBlend();
        }

        int xOff = 0;

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, RelicDescriptionScreen.TEXTURE);

        manager.bindForSetup(RelicDescriptionScreen.TEXTURE);

        for (int i = 1; i < QualityUtils.getAbilityQuality(screen.stack, ability) + 1; i++) {
            boolean isAliquot = i % 2 == 1;

            blit(poseStack, -(width / 2) + xOff + 2, -(height / 2) + 38, (canUse ? 302 : 334) + (isAliquot ? 0 : 2), 108, isAliquot ? 2 : 3, 4, 512, 512);

            xOff += (isAliquot ? 2 : 3);
        }

        MutableComponent title = Component.literal(String.valueOf(AbilityUtils.getAbilityPoints(screen.stack, ability))).withStyle(ChatFormatting.BOLD);

        float textScale = 0.5F;

        poseStack.scale(textScale, textScale, textScale);

        MC.font.drawShadow(poseStack, title, -((width + 1) / 2F) - (MC.font.width(title) / 2F) + 13, (-(height / 2F) - 19), 0xffce96);

        poseStack.popPose();
    }

    @Override
    public void onTick() {
        float maxScale = 1.1F;
        float minScale = 1F;

        RandomSource random = MC.player.getRandom();

        if (AbilityUtils.mayPlayerUpgrade(MC.player, screen.stack, ability)) {
            if (screen.ticksExisted % 7 == 0)
                ParticleStorage.addParticle(screen, new ExperienceParticleData(new Color(200 + random.nextInt(50), 150 + random.nextInt(100), 0),
                        x + 5 + random.nextInt(18), y + 18, 0.15F + (random.nextFloat() * 0.25F), 100 + random.nextInt(50)));
        }

        if (isHovered) {
            if (screen.ticksExisted % 3 == 0)
                ParticleStorage.addParticle(screen, new ExperienceParticleData(AbilityUtils.canUseAbility(screen.stack, ability)
                        ? new Color(200 + random.nextInt(50), 150 + random.nextInt(100), 0)
                        : new Color(100 + random.nextInt(100), 100 + random.nextInt(100), 100 + random.nextInt(100)),
                        x + random.nextInt(width), y - 1, 0.15F + (random.nextFloat() * 0.25F), 100 + random.nextInt(50)));

            if (scale < maxScale)
                scale = Math.min(maxScale, scale + ((maxScale - scale) * (0.25F)));
        } else {
            if (scale != minScale)
                scale = Math.max(minScale, (scale - 0.025F));
        }
    }

    @Override
    public void onHovered(PoseStack poseStack, int mouseX, int mouseY) {
        RelicAbilityEntry data = AbilityUtils.getRelicAbilityEntry(screen.stack.getItem(), ability);

        if (data == null)
            return;

        List<FormattedCharSequence> tooltip = Lists.newArrayList();

        int maxWidth = 110;
        int renderWidth = 0;

        List<MutableComponent> entries = Lists.newArrayList(
                Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(screen.stack.getItem()).getPath() + ".ability." + ability).withStyle(ChatFormatting.BOLD)
        );

        int level = LevelingUtils.getLevel(screen.stack);
        int requiredLevel = data.getRequiredLevel();

        if (level < requiredLevel) {
            entries.add(Component.literal(" "));

            entries.add(Component.literal("▶ ").append(Component.translatable("tooltip.relics.relic.ability.tooltip.low_level", requiredLevel, Component.translatable("tooltip.relics.relic.status.negative").withStyle(ChatFormatting.RED))));
        } else if (data.getMaxLevel() == 0) {
            entries.add(Component.literal(" "));

            entries.add(Component.literal("▶ ").append(Component.translatable("tooltip.relics.relic.ability.tooltip.no_stats")));
        } else if (AbilityUtils.mayPlayerUpgrade(MC.player, screen.stack, ability)) {
            entries.add(Component.literal(" "));

            entries.add(Component.literal("▶ ").append(Component.translatable("tooltip.relics.relic.ability.tooltip.ready_to_upgrade", Component.translatable("tooltip.relics.relic.status.positive").withStyle(ChatFormatting.GREEN))));
        }

        for (MutableComponent entry : entries) {
            int entryWidth = (MC.font.width(entry)) / 2;

            if (entryWidth > renderWidth)
                renderWidth = Math.min(entryWidth, maxWidth);

            tooltip.addAll(MC.font.split(entry, maxWidth * 2));
        }

        int height = Math.round(tooltip.size() * 4.5F);

        int y = getHeight() / 2;

        poseStack.scale(scale, scale, scale);

        poseStack.translate((x + (getWidth() / 2F)) / scale, (y + (getHeight() / 2F)) / scale, 0);

        ScreenUtils.drawTexturedTooltipBorder(poseStack, RelicDescriptionScreen.BORDER_PAPER, renderWidth, height, -((renderWidth + 19) / 2), y);

        int yOff = 0;

        for (FormattedCharSequence entry : tooltip) {
            poseStack.pushPose();

            poseStack.scale(0.5F, 0.5F, 0.5F);

            MC.font.draw(poseStack, entry, -(MC.font.width(entry) / 2F), ((y + yOff + 9) * 2), 0x412708);

            yOff += 5;

            poseStack.popPose();
        }
    }
}