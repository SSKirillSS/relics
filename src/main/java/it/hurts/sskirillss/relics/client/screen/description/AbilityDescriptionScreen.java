package it.hurts.sskirillss.relics.client.screen.description;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IAutoScaledScreen;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.description.data.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.description.widgets.ability.AbilityRerollButtonWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.ability.AbilityResetButtonWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.ability.AbilityUpgradeButtonWidget;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.client.screen.utils.ScreenUtils;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.QualityUtils;
import it.hurts.sskirillss.relics.tiles.ResearchingTableTile;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AbilityDescriptionScreen extends Screen implements IAutoScaledScreen {
    private final Minecraft MC = Minecraft.getInstance();

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/description/ability_background.png");

    public final BlockPos pos;
    public ItemStack stack;
    public final String ability;

    public int backgroundHeight = 171;
    public int backgroundWidth = 268;

    public AbilityUpgradeButtonWidget upgradeButton;
    public AbilityRerollButtonWidget rerollButton;
    public AbilityResetButtonWidget resetButton;

    public int ticksExisted;

    public AbilityDescriptionScreen(BlockPos pos, ItemStack stack, String ability) {
        super(Component.empty());

        this.pos = pos;
        this.stack = stack;
        this.ability = ability;
    }

    @Override
    protected void init() {
        if (!(stack.getItem() instanceof RelicItem))
            return;

        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        this.upgradeButton = new AbilityUpgradeButtonWidget(((this.width - backgroundWidth) / 2) + 233, ((this.height - backgroundHeight) / 2) + 104, this, ability);
        this.rerollButton = new AbilityRerollButtonWidget(((this.width - backgroundWidth) / 2) + 233, ((this.height - backgroundHeight) / 2) + 122, this, ability);
        this.resetButton = new AbilityResetButtonWidget(((this.width - backgroundWidth) / 2) + 233, ((this.height - backgroundHeight) / 2) + 140, this, ability);

        this.addRenderableWidget(upgradeButton);
        this.addRenderableWidget(rerollButton);
        this.addRenderableWidget(resetButton);
    }

    @Override
    public void tick() {
        super.tick();

        LocalPlayer player = MC.player;

        ticksExisted++;

        RandomSource random = MC.player.getRandom();

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        if (ticksExisted % 3 == 0) {
            {
                int percentage = (int) (player.totalExperience / ((player.totalExperience / player.experienceProgress) / 100));

                int sourceWidth = 206;
                int maxWidth = (int) (sourceWidth * (percentage / 100F));

                if (maxWidth > 0) {
                    int xOff = random.nextInt(sourceWidth);

                    if (xOff <= maxWidth)
                        ParticleStorage.addParticle(this, new ExperienceParticleData(new Color(100 + random.nextInt(50), 200 + random.nextInt(50), 0),
                                x + 30 + xOff, y + 82, 0.15F + (random.nextFloat() * 0.25F), 100 + random.nextInt(50)));
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        LocalPlayer player = MC.player;

        if (player == null)
            return;

        Level world = player.level();

        if (!(world.getBlockEntity(pos) instanceof ResearchingTableTile tile))
            return;

        stack = tile.getStack();

        if (!(stack.getItem() instanceof RelicItem relic))
            return;

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return;

        RelicAbilityEntry abilityData = AbilityUtils.getRelicAbilityEntry(relic, ability);

        if (abilityData == null)
            return;

        PoseStack pPoseStack = guiGraphics.pose();
        TextureManager manager = MC.getTextureManager();

        this.renderBackground(guiGraphics);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        int texWidth = 512;
        int texHeight = 512;

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, texWidth, texHeight);

        int percentage = (int) (player.totalExperience / ((player.totalExperience / player.experienceProgress) / 100));

        guiGraphics.blit(TEXTURE, x + 30, y + 82, 301, 124, (int) Math.ceil(percentage / 100F * 206), 3, texWidth, texHeight);

        boolean hoveredVanillaExperience = ScreenUtils.isHovered(x + 30, y + 81, 206, 3, pMouseX, pMouseY);

        if (hoveredVanillaExperience) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/gui/description/experience_highlight.png"));

            RenderSystem.enableBlend();

            RenderUtils.renderTextureFromCenter(guiGraphics.pose(), x + 134F, y + 83.5F, 210, 98, 210, 7, 1F, AnimationData.builder()
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
                    .frame(12, 2)
                    .frame(13, 2)
            );

            RenderSystem.disableBlend();
        }

        pPoseStack.pushPose();

        pPoseStack.scale(0.75F, 0.75F, 0.75F);

        MutableComponent experience = Component.literal(String.valueOf(player.experienceLevel));

        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, (int) ((x + 136) * 1.33F), (int) ((y + 81) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, (int) ((x + 134) * 1.33F), (int) ((y + 81) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, (int) ((x + 135) * 1.33F), (int) ((y + 82) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, (int) ((x + 135) * 1.33F), (int) ((y + 80.5F) * 1.33F), 0x054503, false);

        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, (int) ((x + 135) * 1.33F), (int) ((y + 81) * 1.33F), 0x7efc20, false);

        pPoseStack.popPose();

        pPoseStack.pushPose();

        pPoseStack.scale(0.5F, 0.5F, 0.5F);

        int level = AbilityUtils.getAbilityPoints(stack, ability);
        int maxLevel = abilityData.getMaxLevel() == -1 ? (relicData.getLevelingData().getMaxLevel() / abilityData.getRequiredPoints()) : abilityData.getMaxLevel();

        MutableComponent name = Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(relic).getPath() + ".ability." + ability);

        if (!abilityData.getStats().isEmpty())
            name.append(Component.translatable("tooltip.relics.relic.ability.level", level, maxLevel == -1 ? "∞" : maxLevel));

        guiGraphics.drawString(MC.font, name.withStyle(ChatFormatting.BOLD), (x + 62) * 2, (y + 20) * 2 - 1, 0x412708, false);

        List<FormattedCharSequence> lines = MC.font.split(Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + ability + ".description"), 350);

        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(MC.font, lines.get(i), (x + 62) * 2, (y + 28 + (i * 5)) * 2, 0x412708, false);
        }

        pPoseStack.popPose();

        int yOff = 0;
        int xOff = 0;

        boolean isLocked = !AbilityUtils.canUseAbility(stack, ability);

        boolean isHoveredUpgrade = !isLocked && upgradeButton.isHovered();
        boolean isHoveredReroll = !isLocked && rerollButton.isHovered();
        boolean isHoveredReset = !isLocked && resetButton.isHovered();

        for (String stat : AbilityUtils.getAbilityInitialValues(stack, ability).keySet()) {
            RelicAbilityStat statData = AbilityUtils.getRelicAbilityStat(relic, ability, stat);

            if (statData != null) {
                MutableComponent cost = Component.literal(String.valueOf(statData.getFormatValue().apply(AbilityUtils.getAbilityValue(stack, ability, stat))));

                if (isHoveredUpgrade && level < maxLevel) {
                    cost.append(" ➠ " + statData.getFormatValue().apply(AbilityUtils.getAbilityValue(stack, ability, stat, level + 1)));
                }

                if (isHoveredReroll) {
                    cost.append(" ➠ ").append(Component.literal("X.XXX").withStyle(ChatFormatting.OBFUSCATED));
                }

                if (isHoveredReset && level > 0) {
                    cost.append(" ➠ " + statData.getFormatValue().apply(AbilityUtils.getAbilityValue(stack, ability, stat, 0)));
                }

                pPoseStack.pushPose();

                pPoseStack.scale(0.5F, 0.5F, 0.5F);

                guiGraphics.drawString(MC.font, Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + ability + ".stat." + stat + ".title"), (x + 35) * 2, (y + yOff + 102) * 2, 0x412708, false);

                guiGraphics.drawString(MC.font, Component.literal("● ").append(Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + ability + ".stat." + stat + ".value", cost)), (x + 40) * 2, (y + yOff + 107) * 2, 0x412708, false);

                pPoseStack.popPose();

                RenderSystem.setShaderTexture(0, TEXTURE);

                for (int i = 0; i < 5; i++) {
                    guiGraphics.blit(TEXTURE, x + xOff + 202, y + yOff + 102, 398, 15, 4, 4, 512, 512);

                    xOff += 5;
                }

                xOff = 0;

                manager.bindForSetup(TEXTURE);

                for (int i = 1; i < QualityUtils.getStatQuality(stack, ability, stat) + 1; i++) {
                    boolean isAliquot = i % 2 == 1;

                    guiGraphics.blit(TEXTURE, x + xOff + 202, y + yOff + 102, (isLocked ? 407 : 398) + (isAliquot ? 0 : 2), 10, isAliquot ? 2 : 3, 4, 512, 512);

                    xOff += (isAliquot ? 2 : 3);
                }

                yOff += 14;
                xOff = 0;
            }
        }

        ResourceLocation card = new ResourceLocation(Reference.MODID, "textures/gui/description/cards/" + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + "/" + ability + ".png");

        RenderSystem.setShaderTexture(0, card);

        manager.bindForSetup(card);

        if (GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT) == 29) {
            if (isLocked)
                RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, 1F);

            guiGraphics.blit(card, x + 17, y + 11, 36, 50, 0, 0, 20, 29, 20, 29);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        }

        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        if (isLocked)
            guiGraphics.blit(TEXTURE, x + 12, y + 7, 349, 0, 47, 69, texWidth, texHeight);
        else
            guiGraphics.blit(TEXTURE, x + 13, y + 7, 302, 0, 46, 69, texWidth, texHeight);

        for (int i = 1; i < QualityUtils.getAbilityQuality(stack, ability) + 1; i++) {
            boolean isAliquot = i % 2 == 1;

            guiGraphics.blit(TEXTURE, x + 15 + xOff, y + 63, (isLocked ? 407 : 397) + (isAliquot ? 0 : 5), 0, isAliquot ? 5 : 4, 9, texWidth, texHeight);

            xOff += isAliquot ? 5 : 3;
        }

        int points = LevelingUtils.getPoints(stack);

        if (points > 0) {
            pPoseStack.pushPose();

            MutableComponent value = Component.literal(String.valueOf(points)).withStyle(ChatFormatting.BOLD);

            ResourceLocation icon = new ResourceLocation(Reference.MODID, "textures/gui/description/leveling_point.png");

            manager.bindForSetup(icon);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, icon);

            guiGraphics.blit(icon, x + backgroundWidth + 5, y - 2, 0, 0, 50, 31, 50, 31);

            guiGraphics.drawString(MC.font, value, x + backgroundWidth + 39 - (MC.font.width(value) / 2), y + 10, 0xffce96);

            pPoseStack.popPose();
        }

        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        if (hoveredVanillaExperience) {
            pPoseStack.pushPose();

            pPoseStack.translate(0, 0, 10);

            List<FormattedCharSequence> tooltip = Lists.newArrayList();

            int maxWidth = 200;
            int renderWidth = 0;

            List<MutableComponent> entries = Lists.newArrayList(
                    Component.translatable("tooltip.relics.relic.vanilla_experience.title").withStyle(ChatFormatting.BOLD),
                    Component.literal(" "),
                    Component.literal("● ").append(Component.translatable("tooltip.relics.relic.vanilla_experience.current_amount", (player.totalExperience - EntityUtils.getTotalExperienceForLevel(player.experienceLevel)),
                            (EntityUtils.getTotalExperienceForLevel(player.experienceLevel + 1) - EntityUtils.getTotalExperienceForLevel(player.experienceLevel)),
                            MathUtils.round(player.experienceProgress * 100F, 1))),
                    Component.literal("● ").append(Component.translatable("tooltip.relics.relic.vanilla_experience.total_amount", player.totalExperience))
            );

            for (MutableComponent entry : entries) {
                int entryWidth = (MC.font.width(entry)) / 2;

                if (entryWidth > renderWidth)
                    renderWidth = Math.min(entryWidth, maxWidth);

                tooltip.addAll(MC.font.split(entry, maxWidth * 2));
            }

            int height = Math.round(tooltip.size() * 4.5F);

            int renderX = pMouseX - 9 - (renderWidth / 2);
            int renderY = y + 87;

            ScreenUtils.drawTexturedTooltipBorder(guiGraphics, new ResourceLocation(Reference.MODID, "textures/gui/tooltip/border/paper.png"),
                    renderWidth, height, renderX, renderY);

            yOff = 0;

            pPoseStack.scale(0.5F, 0.5F, 0.5F);

            for (FormattedCharSequence entry : tooltip) {
                guiGraphics.drawString(MC.font, entry, (renderX + 9) * 2, (renderY + 9 + yOff) * 2, 0x412708, false);

                yOff += 5;
            }

            pPoseStack.scale(1F, 1F, 1F);

            pPoseStack.popPose();
        }

        if (points > 0 && ScreenUtils.isHovered(x + backgroundWidth + 5, y - 2, 50, 31, pMouseX, pMouseY)) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/gui/description/leveling_point_highlight.png"));

            RenderSystem.enableBlend();

            RenderUtils.renderTextureFromCenter(guiGraphics.pose(), x + backgroundWidth + 5 + (50 / 2), y - 2 + (31 / 2), 64, 768, 64, 64, 1F, AnimationData.builder()
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

            pPoseStack.pushPose();

            pPoseStack.translate(0, 0, 10);

            List<FormattedCharSequence> tooltip = Lists.newArrayList();

            int maxWidth = 200;
            int renderWidth = 0;

            List<MutableComponent> entries = Lists.newArrayList(
                    Component.translatable("tooltip.relics.relic.leveling_points.title").withStyle(ChatFormatting.BOLD)
            );

            for (MutableComponent entry : entries) {
                int entryWidth = (MC.font.width(entry)) / 2;

                if (entryWidth > renderWidth)
                    renderWidth = Math.min(entryWidth, maxWidth);

                tooltip.addAll(MC.font.split(entry, maxWidth * 2));
            }

            int height = Math.round(tooltip.size() * 4.5F);

            int renderX = pMouseX + 1;
            int renderY = pMouseY + 1;

            ScreenUtils.drawTexturedTooltipBorder(guiGraphics, new ResourceLocation(Reference.MODID, "textures/gui/tooltip/border/paper.png"),
                    renderWidth, height, renderX, renderY);

            yOff = 0;

            pPoseStack.scale(0.5F, 0.5F, 0.5F);

            for (FormattedCharSequence entry : tooltip) {
                guiGraphics.drawString(MC.font, entry, (renderX + 9) * 2, (renderY + 9 + yOff) * 2, 0x412708, false);

                yOff += 5;
            }

            pPoseStack.scale(1F, 1F, 1F);

            pPoseStack.popPose();
        }

        for (GuiEventListener listener : this.children()) {
            if (listener instanceof AbstractButton button && button.isHovered()
                    && button instanceof IHoverableWidget widget)
                widget.onHovered(guiGraphics, pMouseX, pMouseY);
        }
    }

    @Override
    public void onClose() {
        MC.setScreen(new RelicDescriptionScreen(pos));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public int getAutoScale() {
        return 0;
    }
}