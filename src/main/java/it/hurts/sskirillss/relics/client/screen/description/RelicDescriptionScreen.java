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
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class RelicDescriptionScreen extends Screen implements IAutoScaledScreen {
    private final Minecraft MC = Minecraft.getInstance();

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/description/relic_background.png");
    public static final ResourceLocation LEVELING_POINT_HIGHLIGHT = new ResourceLocation(Reference.MODID, "textures/gui/description/leveling_point_highlight.png");
    public static final ResourceLocation LEVELING_POINT = new ResourceLocation(Reference.MODID, "textures/gui/description/leveling_point.png");
    public static final ResourceLocation BORDER_PAPER = new ResourceLocation(Reference.MODID, "textures/gui/tooltip/border/paper.png");
    public static final ResourceLocation RELIC_EXPERIENCE_HIGHLIGHT = new ResourceLocation(Reference.MODID, "textures/gui/description/relic_experience_highlight.png");
    public static final ResourceLocation VANILLA_EXPERIENCE_HIGHLIGHT = new ResourceLocation(Reference.MODID, "textures/gui/description/experience_highlight.png");

    public final BlockPos pos;
    public ItemStack stack;

    public int backgroundHeight = 171;
    public int backgroundWidth = 268;

    public int ticksExisted;

    public RelicDescriptionScreen(BlockPos pos) {
        super(net.minecraft.network.chat.TextComponent.EMPTY);

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

        Level level = player.getLevel();

        if (!(level.getBlockEntity(pos) instanceof ResearchingTableTile tile))
            return;

        stack = tile.getStack();

        if (stack == null || !(stack.getItem() instanceof IRelicItem relic))
            return;

        ticksExisted++;

        Random random = player.getRandom();

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
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        LocalPlayer player = MC.player;

        if (stack == null || !(stack.getItem() instanceof IRelicItem relic) || player == null)
            return;

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return;

        TextureManager manager = MC.getTextureManager();

        this.renderBackground(pPoseStack);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        int texWidth = 512;
        int texHeight = 512;

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        blit(pPoseStack, x, y, 0, 0, backgroundWidth, backgroundHeight, texWidth, texHeight);

        int level = relic.getLevel(stack);

        float percentage = relic.getExperience(stack) / (relic.getExperienceBetweenLevels(stack, level, level + 1) / 100F);

        boolean isMaxLevel = relic.isMaxLevel(stack);

        blit(pPoseStack, x + 30, y + 72, 302, 144, isMaxLevel ? 206 : (int) Math.ceil(percentage / 100F * 206), 3, texWidth, texHeight);

        boolean hoveredRelicExperience = ScreenUtils.isHovered(x + 30, y + 72, 206, 3, pMouseX, pMouseY);

        if (hoveredRelicExperience) {
            RenderSystem.setShaderTexture(0, RelicDescriptionScreen.RELIC_EXPERIENCE_HIGHLIGHT);

            RenderSystem.enableBlend();

            RenderUtils.renderAnimatedTextureFromCenter(pPoseStack, x + 133F, y + 73.5F, 210, 98, 210, 7, 1F, AnimationData.builder()
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

        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(pPoseStack, x + 30, y + 85, 302, 148, (int) Math.ceil(percentage / 100F * 206), 3, texWidth, texHeight);

        boolean hoveredVanillaExperience = ScreenUtils.isHovered(x + 30, y + 85, 206, 3, pMouseX, pMouseY);

        if (hoveredVanillaExperience) {
            RenderSystem.setShaderTexture(0, RelicDescriptionScreen.VANILLA_EXPERIENCE_HIGHLIGHT);

            RenderSystem.enableBlend();

            RenderUtils.renderAnimatedTextureFromCenter(pPoseStack, x + 133F, y + 86.5F, 210, 98, 210, 7, 1F, AnimationData.builder()
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

        blit(pPoseStack, x + 18, y + 15, 0, 0, 34, 34, 34, 34);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        blit(pPoseStack, x + 13, y + 10, 302, 5, 46, 55, texWidth, texHeight);

        float scale = 1.75F;

        RenderSystem.getModelViewStack().pushPose(); // Modify the pose stack the item renderer will use
        RenderSystem.getModelViewStack().translate(x + 21, y + 18.5F, 0);
        RenderSystem.getModelViewStack().scale(scale, scale, scale);
        Minecraft.getInstance().getItemRenderer().renderGuiItem(stack, 0, 0);
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix(); // Reset to the current pose stack

        RenderSystem.setShaderTexture(0, TEXTURE);

        int xOff = 0;

        for (int i = 1; i < relic.getRelicQuality(stack) + 1; i++) {
            boolean isAliquot = i % 2 == 1;

            blit(pPoseStack, x + 15 + xOff, y + 51, 353 + (isAliquot ? 0 : 5), 3, isAliquot ? 5 : 4, 9, texWidth, texHeight);

            xOff += isAliquot ? 5 : 3;
        }

        MutableComponent name = new net.minecraft.network.chat.TextComponent(stack.getDisplayName().getString()
                        .replace("[", "").replace("]", ""))
                .withStyle(ChatFormatting.BOLD);

        pPoseStack.pushPose();

        pPoseStack.scale(0.5F, 0.5F, 1F);

        MC.font.draw(pPoseStack, name, (x + 62) * 2, (y + 21) * 2, 0x412708);

        pPoseStack.popPose();

        pPoseStack.pushPose();

        pPoseStack.scale(0.75F, 0.75F, 0.75F);

        MutableComponent experience = isMaxLevel ? new TranslatableComponent("tooltip.relics.relic.max_level") : new net.minecraft.network.chat.TextComponent((String.valueOf(level)));

        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135.75F) * 1.33F), ((y + 71) * 1.33F), 0x793300, false);
        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 134.25F) * 1.33F), ((y + 71) * 1.33F), 0x793300, false);
        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135) * 1.33F), ((y + 71.75F) * 1.33F), 0x793300, false);
        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135) * 1.33F), ((y + 70.25F) * 1.33F), 0x793300, false);

        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135) * 1.33F), ((y + 71) * 1.33F), 0xfff500, false);

        experience = new net.minecraft.network.chat.TextComponent(String.valueOf(player.experienceLevel));

        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135.75F) * 1.33F), ((y + 84) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 134.25F) * 1.33F), ((y + 84) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135) * 1.33F), ((y + 84.75F) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135) * 1.33F), ((y + 83.25F) * 1.33F), 0x054503, false);

        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135) * 1.33F), ((y + 84) * 1.33F), 0x7efc20, false);

        pPoseStack.popPose();

        String registryName = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();

        pPoseStack.pushPose();

        pPoseStack.scale(0.5F, 0.5F, 1F);

        int yOff = 9;

        List<FormattedCharSequence> lines = new ArrayList<>();

        for (String entry : new TranslatableComponent("tooltip.relics." + registryName + ".leveling").getString().lines().toList())
            lines.addAll(MC.font.split(new net.minecraft.network.chat.TextComponent("● ").append(entry), 350));

        for (FormattedCharSequence line : lines) {
            MC.font.draw(pPoseStack, line, (x + 62) * 2, (y + 26) * 2 + yOff, 0x412708);

            yOff += 9;
        }

        pPoseStack.popPose();

        int points = relic.getPoints(stack);

        if (points > 0) {
            pPoseStack.pushPose();

            MutableComponent value = new net.minecraft.network.chat.TextComponent((String.valueOf(points)).formatted(ChatFormatting.BOLD));

            manager.bindForSetup(LEVELING_POINT);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, RelicDescriptionScreen.LEVELING_POINT);

            blit(pPoseStack, x + backgroundWidth + 5, y - 2, 0, 0, 50, 31, 50, 31);

            MC.font.draw(pPoseStack, value, x + backgroundWidth + 39 - (MC.font.width(value) / 2F), y + 10, 0xffce96);

            pPoseStack.popPose();
        }

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        if (hoveredRelicExperience) {
            pPoseStack.pushPose();

            pPoseStack.translate(0, 0, 10);

            List<FormattedCharSequence> tooltip = Lists.newArrayList();

            int maxWidth = 200;
            int renderWidth = 0;

            List<MutableComponent> entries = Lists.newArrayList(
                    new TranslatableComponent("tooltip.relics.relic.relic_experience.title").withStyle(ChatFormatting.BOLD)
            );

            if (!isMaxLevel) {
                entries.add(new net.minecraft.network.chat.TextComponent((" ")));

                entries.add(new net.minecraft.network.chat.TextComponent(("● ")).append(new TranslatableComponent("tooltip.relics.relic.relic_experience.current_amount", relic.getExperience(stack),
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

            ScreenUtils.drawTexturedTooltipBorder(pPoseStack, RelicDescriptionScreen.BORDER_PAPER, renderWidth, height, renderX, renderY);

            yOff = 0;

            pPoseStack.scale(0.5F, 0.5F, 0.5F);

            for (FormattedCharSequence entry : tooltip) {
                MC.font.draw(pPoseStack, entry, (renderX + 9) * 2, (renderY + 9 + yOff) * 2, 0x412708);

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
                    new TranslatableComponent("tooltip.relics.relic.vanilla_experience.title").withStyle(ChatFormatting.BOLD),
                    new TextComponent(" "),
                    new TextComponent("● ").append(new TranslatableComponent("tooltip.relics.relic.vanilla_experience.current_amount", (player.totalExperience - EntityUtils.getTotalExperienceForLevel(player.experienceLevel)),
                            (EntityUtils.getTotalExperienceForLevel(player.experienceLevel + 1) - EntityUtils.getTotalExperienceForLevel(player.experienceLevel)),
                            MathUtils.round(player.experienceProgress * 100F, 1))),
                    new TextComponent("● ").append(new TranslatableComponent("tooltip.relics.relic.vanilla_experience.total_amount", player.totalExperience))
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

            ScreenUtils.drawTexturedTooltipBorder(pPoseStack, RelicDescriptionScreen.BORDER_PAPER, renderWidth, height, renderX, renderY);

            yOff = 0;

            pPoseStack.scale(0.5F, 0.5F, 0.5F);

            for (FormattedCharSequence entry : tooltip) {
                MC.font.draw(pPoseStack, entry, (renderX + 9) * 2, (renderY + 9 + yOff) * 2, 0x412708);

                yOff += 5;
            }

            pPoseStack.scale(1F, 1F, 1F);

            pPoseStack.popPose();
        }

        if (points > 0 && ScreenUtils.isHovered(x + backgroundWidth + 5, y - 2, 50, 31, pMouseX, pMouseY)) {
            RenderSystem.setShaderTexture(0, RelicDescriptionScreen.LEVELING_POINT_HIGHLIGHT);

            RenderSystem.enableBlend();

            RenderUtils.renderAnimatedTextureFromCenter(pPoseStack, x + backgroundWidth + 5 + (50 / 2), y - 2 + (31 / 2), 64, 768, 64, 64, 1F, AnimationData.builder()
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
                    new TranslatableComponent("tooltip.relics.relic.leveling_points.title").withStyle(ChatFormatting.BOLD)
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

            ScreenUtils.drawTexturedTooltipBorder(pPoseStack, RelicDescriptionScreen.BORDER_PAPER,
                    renderWidth, height, renderX, renderY);

            yOff = 0;

            pPoseStack.scale(0.5F, 0.5F, 0.5F);

            for (FormattedCharSequence entry : tooltip) {
                MC.font.draw(pPoseStack, entry, (renderX + 9) * 2, (renderY + 9 + yOff) * 2, 0x412708);

                yOff += 5;
            }

            pPoseStack.scale(1F, 1F, 1F);

            pPoseStack.popPose();
        }

        for (GuiEventListener listener : this.children()) {
            if (listener instanceof AbstractButton button && button.isHoveredOrFocused()
                    && button instanceof IHoverableWidget widget)
                widget.onHovered(pPoseStack, pMouseX, pMouseY);
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