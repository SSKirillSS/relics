package it.hurts.sskirillss.relics.client.screen.description;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IAutoScaledScreen;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.description.data.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.description.widgets.relic.AbilityCardIconWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.relic.ExperienceExchangeWidget;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.client.screen.utils.ScreenUtils;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
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

import java.awt.*;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RelicDescriptionScreen extends Screen implements IAutoScaledScreen {
    private final Minecraft MC = Minecraft.getInstance();

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/description/relic_background.png");

    public final BlockPos pos;
    public ItemStack stack;

    public int backgroundHeight = 171;
    public int backgroundWidth = 268;

    public int ticksExisted;

    public RelicDescriptionScreen(BlockPos pos) {
        super(Component.empty());

        this.pos = pos;

        gatherData();
    }

    public void gatherData() {
        Level level = MC.level;

        if (!(level.getBlockEntity(pos) instanceof ResearchingTableTile tile))
            return;

        ItemStack stack = tile.getStack();

        if (!(stack.getItem() instanceof IRelicItem))
            return;

        this.stack = stack;
    }

    @Override
    protected void init() {
        if (stack == null || !(stack.getItem() instanceof IRelicItem relic))
            return;

        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        int step = 0;

        for (Map.Entry<String, AbilityData> ability : relic.getRelicData().getAbilities().getAbilities().entrySet()) {
            this.addRenderableWidget(new AbilityCardIconWidget(x + 41 + step, y + 105, this, ability.getKey()));

            step += 39;
        }

        this.addRenderableWidget(new ExperienceExchangeWidget(x + 239, y + 72, this));
    }

    @Override
    public void tick() {
        super.tick();

        LocalPlayer player = MC.player;

        if (player == null)
            return;

        Level level = player.level();

        if (!(level.getBlockEntity(pos) instanceof ResearchingTableTile tile))
            return;

        stack = tile.getStack();

        if (stack == null || !(stack.getItem() instanceof IRelicItem relic))
            return;

        ticksExisted++;

        RandomSource random = player.getRandom();

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        if (ticksExisted % 3 == 0) {
            {
                int relicLevel = relic.getLevel(stack);

                float percentage = relic.isMaxLevel(stack) ? 100F : relic.getExperience(stack) / (relic.getExperienceBetweenLevels(stack, relicLevel, relicLevel + 1) / 100F);

                int sourceWidth = 206;
                int maxWidth = (int) (sourceWidth * (percentage / 100F));

                if (maxWidth > 0) {
                    int xOff = random.nextInt(sourceWidth);

                    if (xOff <= maxWidth)
                        ParticleStorage.addParticle(this, new ExperienceParticleData(new Color(200 + random.nextInt(50), 150 + random.nextInt(100), 0),
                                x + 30 + xOff, y + 73, 0.15F + (random.nextFloat() * 0.25F), 100 + random.nextInt(50)));
                }
            }

            {
                float percentage = (int) (player.totalExperience / ((player.totalExperience / player.experienceProgress) / 100F));

                int sourceWidth = 206;
                int maxWidth = (int) (sourceWidth * (percentage / 100F));

                if (maxWidth > 0) {
                    int xOff = random.nextInt(sourceWidth);

                    if (xOff <= maxWidth)
                        ParticleStorage.addParticle(this, new ExperienceParticleData(new Color(100 + random.nextInt(50), 200 + random.nextInt(50), 0),
                                x + 30 + xOff, y + 86, 0.15F + (random.nextFloat() * 0.25F), 100 + random.nextInt(50)));
                }
            }
        }

        if (this.ticksExisted % 10 == 0) {
            {
                if (relic.getPoints(stack) > 0) {
                    ParticleStorage.addParticle(this, new ExperienceParticleData(
                            new Color(200 + random.nextInt(50), 150 + random.nextInt(100), 0),
                            x + backgroundWidth + 15 + random.nextInt(16), y + 8 + random.nextInt(10),
                            0.15F + (random.nextFloat() * 0.25F), 100 + random.nextInt(50)));
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        LocalPlayer player = MC.player;

        if (stack == null || !(stack.getItem() instanceof IRelicItem relic) || player == null)
            return;

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
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

        int level = relic.getLevel(stack);

        float percentage = relic.getExperience(stack) / (relic.getExperienceBetweenLevels(stack, level, level + 1) / 100F);

        boolean isMaxLevel = relic.isMaxLevel(stack);

        guiGraphics.blit(TEXTURE, x + 30, y + 72, 302, 144, isMaxLevel ? 206 : (int) Math.ceil(percentage / 100F * 206), 3, texWidth, texHeight);

        boolean hoveredRelicExperience = ScreenUtils.isHovered(x + 30, y + 72, 206, 3, pMouseX, pMouseY);

        if (hoveredRelicExperience) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/gui/description/relic_experience_highlight.png"));

            RenderSystem.enableBlend();

            RenderUtils.renderAnimatedTextureFromCenter(guiGraphics.pose(), x + 133F, y + 73.5F, 210, 98, 210, 7, 1F, AnimationData.builder()
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

        percentage = (player.totalExperience / ((player.totalExperience / player.experienceProgress) / 100F));

        guiGraphics.blit(TEXTURE, x + 30, y + 85, 302, 148, (int) Math.ceil(percentage / 100F * 206), 3, texWidth, texHeight);

        boolean hoveredVanillaExperience = ScreenUtils.isHovered(x + 30, y + 85, 206, 3, pMouseX, pMouseY);

        if (hoveredVanillaExperience) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/gui/description/experience_highlight.png"));

            RenderSystem.enableBlend();

            RenderUtils.renderAnimatedTextureFromCenter(guiGraphics.pose(), x + 133F, y + 86.5F, 210, 98, 210, 7, 1F, AnimationData.builder()
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

        ResourceLocation background = relic.getStyleData().getBackground();

        RenderSystem.setShaderTexture(0, background);

        float color = (float) (0.75F + Math.sin(player.tickCount * 0.1F) * 0.05F);

        RenderSystem.setShaderColor(color, color, color, 1F);

        guiGraphics.blit(background, x + 18, y + 15, 0, 0, 34, 34, 34, 34);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        guiGraphics.blit(TEXTURE, x + 13, y + 10, 302, 5, 46, 55, texWidth, texHeight);

        pPoseStack.pushPose();

        float scale = 1.75F;

        pPoseStack.translate(x + 21, y + 18.5F, 0);
        pPoseStack.scale(scale, scale, scale);

        guiGraphics.renderItem(stack, 0, 0);

        pPoseStack.popPose();

        int xOff = 0;

        for (int i = 1; i < relic.getRelicQuality(stack) + 1; i++) {
            boolean isAliquot = i % 2 == 1;

            guiGraphics.blit(TEXTURE, x + 15 + xOff, y + 51, 353 + (isAliquot ? 0 : 5), 3, isAliquot ? 5 : 4, 9, texWidth, texHeight);

            xOff += isAliquot ? 5 : 3;
        }

        MutableComponent name = Component.literal(stack.getDisplayName().getString()
                        .replace("[", "").replace("]", ""))
                .withStyle(ChatFormatting.BOLD);

        pPoseStack.pushPose();

        pPoseStack.scale(0.5F, 0.5F, 1F);

        guiGraphics.drawString(MC.font, name, (x + 62) * 2, (y + 21) * 2, 0x412708, false);

        pPoseStack.popPose();

        pPoseStack.pushPose();

        pPoseStack.scale(0.75F, 0.75F, 0.75F);

        MutableComponent experience = isMaxLevel ? Component.translatable("tooltip.relics.relic.max_level") : Component.literal(String.valueOf(level));

        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, ((x + 135.75F) * 1.33F), ((y + 71) * 1.33F), 0x793300, false);
        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, ((x + 134.25F) * 1.33F), ((y + 71) * 1.33F), 0x793300, false);
        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, ((x + 135) * 1.33F), ((y + 71.75F) * 1.33F), 0x793300, false);
        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, ((x + 135) * 1.33F), ((y + 70.25F) * 1.33F), 0x793300, false);

        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, ((x + 135) * 1.33F), ((y + 71) * 1.33F), 0xfff500, false);

        experience = Component.literal(String.valueOf(player.experienceLevel));

        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, ((x + 135.75F) * 1.33F), ((y + 84) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, ((x + 134.25F) * 1.33F), ((y + 84) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, ((x + 135) * 1.33F), ((y + 84.75F) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, ((x + 135) * 1.33F), ((y + 83.25F) * 1.33F), 0x054503, false);

        ScreenUtils.drawCenteredString(guiGraphics, MC.font, experience, ((x + 135) * 1.33F), ((y + 84) * 1.33F), 0x7efc20, false);

        pPoseStack.popPose();

        String registryName = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();

        pPoseStack.pushPose();

        pPoseStack.scale(0.5F, 0.5F, 1F);

        int yOff = 9;

        for (FormattedCharSequence line : MC.font.split(Component.translatable("tooltip.relics." + registryName + ".description"), 350)) {
            guiGraphics.drawString(MC.font, line, (x + 62) * 2, (y + 26) * 2 + yOff, 0x412708, false);

            yOff += 9;
        }

        pPoseStack.popPose();

        int points = relic.getPoints(stack);

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

        if (hoveredRelicExperience) {
            pPoseStack.pushPose();

            pPoseStack.translate(0, 0, 10);

            List<FormattedCharSequence> tooltip = Lists.newArrayList();

            int maxWidth = 200;
            int renderWidth = 0;

            List<MutableComponent> entries = Lists.newArrayList(
                    Component.translatable("tooltip.relics.relic.relic_experience.title").withStyle(ChatFormatting.BOLD)
            );

            if (!isMaxLevel) {
                entries.add(Component.literal(" "));

                entries.add(Component.literal("● ").append(Component.translatable("tooltip.relics.relic.relic_experience.current_amount", relic.getExperience(stack),
                        relic.getExperienceBetweenLevels(stack, level, level + 1),
                        MathUtils.round((relic.getExperience(stack) / (relic.getExperienceBetweenLevels(stack, level, level + 1) / 100F)), 1))));
            }

            for (MutableComponent entry : entries) {
                int entryWidth = (MC.font.width(entry)) / 2;

                if (entryWidth > renderWidth)
                    renderWidth = Math.min(entryWidth, maxWidth);

                tooltip.addAll(MC.font.split(entry, maxWidth * 2));
            }

            int height = Math.round(tooltip.size() * 4.5F);

            int renderX = pMouseX - 9 - (renderWidth / 2);
            int renderY = y + 77;

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
            int renderY = y + 90;

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

            RenderUtils.renderAnimatedTextureFromCenter(guiGraphics.pose(), x + backgroundWidth + 5 + (50 / 2), y - 2 + (31 / 2), 64, 768, 64, 64, 1F, AnimationData.builder()
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
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (MC.options.keyInventory.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
            this.onClose();

            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
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