package it.hurts.sskirillss.relics.client.screen.description;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
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
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.opengl.GL11;
import net.minecraft.network.chat.TextComponent;

import java.awt.*;
import java.util.List;
import java.util.Random;

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
        super(TextComponent.EMPTY);

        this.pos = pos;
        this.stack = stack;
        this.ability = ability;
    }

    @Override
    protected void init() {
        if (stack == null || !(stack.getItem() instanceof IRelicItem))
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

        if (player == null)
            return;

        Level level = player.getLevel();

        if (!(level.getBlockEntity(pos) instanceof ResearchingTableTile tile))
            return;

        stack = tile.getStack();

        if (stack == null || !(stack.getItem() instanceof IRelicItem))
            return;

        ticksExisted++;

        Random random = player.getRandom();

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
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        LocalPlayer player = MC.player;

        if (stack == null || !(stack.getItem() instanceof IRelicItem relic) || player == null)
            return;

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return;

        AbilityData abilityData = relic.getAbilityData(ability);

        if (abilityData == null)
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

        int percentage = (int) (player.totalExperience / ((player.totalExperience / player.experienceProgress) / 100));

        blit(pPoseStack, x + 30, y + 82, 301, 124, (int) Math.ceil(percentage / 100F * 206), 3, texWidth, texHeight);

        boolean hoveredVanillaExperience = ScreenUtils.isHovered(x + 30, y + 81, 206, 3, pMouseX, pMouseY);

        if (hoveredVanillaExperience) {
            RenderSystem.setShaderTexture(0, RelicDescriptionScreen.VANILLA_EXPERIENCE_HIGHLIGHT);

            RenderSystem.enableBlend();

            RenderUtils.renderAnimatedTextureFromCenter(pPoseStack, x + 134F, y + 83.5F, 210, 98, 210, 7, 1F, AnimationData.builder()
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

        MutableComponent experience = new TextComponent(String.valueOf(player.experienceLevel));

        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135.75F) * 1.33F), ((y + 81) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 134.25F) * 1.33F), ((y + 81) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135) * 1.33F), ((y + 81.75F) * 1.33F), 0x054503, false);
        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135) * 1.33F), ((y + 80.25F) * 1.33F), 0x054503, false);

        ScreenUtils.drawCenteredString(pPoseStack, MC.font, experience, ((x + 135) * 1.33F), ((y + 81) * 1.33F), 0x7efc20, false);

        pPoseStack.popPose();

        pPoseStack.pushPose();

        pPoseStack.scale(0.5F, 0.5F, 0.5F);

        int level = relic.getAbilityPoints(stack, ability);
        int maxLevel = abilityData.getMaxLevel() == -1 ? (relicData.getLeveling().getMaxLevel() / abilityData.getRequiredPoints()) : abilityData.getMaxLevel();

        MutableComponent name = new TranslatableComponent("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + ability);

        if (!abilityData.getStats().isEmpty())
            name.append(new TranslatableComponent("tooltip.relics.relic.ability.level", level, maxLevel == -1 ? "∞" : maxLevel));

        MC.font.draw(pPoseStack, name.withStyle(ChatFormatting.BOLD), (x + 62) * 2, (y + 20) * 2 - 1, 0x412708);

        List<FormattedCharSequence> lines = MC.font.split(new TranslatableComponent("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + ability + ".description"), 350);

        for (int i = 0; i < lines.size(); i++) {
            MC.font.draw(pPoseStack, lines.get(i), (x + 62) * 2, (y + 28 + (i * 5)) * 2, 0x412708);
        }

        pPoseStack.popPose();

        int yOff = 0;
        int xOff = 0;

        boolean isLocked = !relic.canUseAbility(stack, ability);

        boolean isHoveredUpgrade = !isLocked && upgradeButton.isHoveredOrFocused();
        boolean isHoveredReroll = !isLocked && rerollButton.isHoveredOrFocused();
        boolean isHoveredReset = !isLocked && resetButton.isHoveredOrFocused();

        for (String stat : relic.getAbilityInitialValues(stack, ability).keySet()) {
            StatData statData = relic.getStatData(ability, stat);

            if (statData != null) {
                MutableComponent cost = new TextComponent(String.valueOf(statData.getFormatValue().apply(relic.getAbilityValue(stack, ability, stat))));

                if (isHoveredUpgrade && level < maxLevel) {
                    cost.append(" ➠ " + statData.getFormatValue().apply(relic.getAbilityValue(stack, ability, stat, level + 1)));
                }

                if (isHoveredReroll) {
                    cost.append(" ➠ ").append(new TextComponent("X.XXX").withStyle(ChatFormatting.OBFUSCATED));
                }

                if (isHoveredReset && level > 0) {
                    cost.append(" ➠ " + statData.getFormatValue().apply(relic.getAbilityValue(stack, ability, stat, 0)));
                }

                pPoseStack.pushPose();

                pPoseStack.scale(0.5F, 0.5F, 0.5F);

                MC.font.draw(pPoseStack, new TranslatableComponent("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + ability + ".stat." + stat + ".title"), (x + 35) * 2, (y + yOff + 102) * 2, 0x412708);

                MC.font.draw(pPoseStack, new TextComponent(" ● ").append(new TranslatableComponent("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + ability + ".stat." + stat + ".value", cost)), (x + 40) * 2, (y + yOff + 107) * 2, 0x412708);

                pPoseStack.popPose();

                RenderSystem.setShaderTexture(0, TEXTURE);

                for (int i = 0; i < 5; i++) {
                    blit(pPoseStack, x + xOff + 202, y + yOff + 102, 398, 15, 4, 4, 512, 512);

                    xOff += 5;
                }

                xOff = 0;

                manager.bindForSetup(TEXTURE);

                for (int i = 1; i < relic.getStatQuality(stack, ability, stat) + 1; i++) {
                    boolean isAliquot = i % 2 == 1;

                    blit(pPoseStack, x + xOff + 202, y + yOff + 102, (isLocked ? 407 : 398) + (isAliquot ? 0 : 2), 10, isAliquot ? 2 : 3, 4, 512, 512);

                    xOff += (isAliquot ? 2 : 3);
                }

                yOff += 14;
                xOff = 0;
            }
        }

        ResourceLocation card = new ResourceLocation(Reference.MODID, "textures/gui/description/cards/" + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + "/" + abilityData.getIcon().apply(player, stack, ability) + ".png");

        RenderSystem.setShaderTexture(0, card);

        manager.bindForSetup(card);

        if (GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT) == 29) {
            if (isLocked)
                RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, 1F);

            blit(pPoseStack, x + 17, y + 11, 36, 50, 0, 0, 20, 29, 20, 29);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        }

        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        if (isLocked)
            blit(pPoseStack, x + 12, y + 7, 349, 0, 47, 69, texWidth, texHeight);
        else
            blit(pPoseStack, x + 13, y + 7, 302, 0, 46, 69, texWidth, texHeight);

        for (int i = 1; i < relic.getAbilityQuality(stack, ability) + 1; i++) {
            boolean isAliquot = i % 2 == 1;

            blit(pPoseStack, x + 15 + xOff, y + 63, (isLocked ? 407 : 397) + (isAliquot ? 0 : 5), 0, isAliquot ? 5 : 4, 9, texWidth, texHeight);

            xOff += isAliquot ? 5 : 3;
        }

        int points = relic.getPoints(stack);

        if (points > 0) {
            pPoseStack.pushPose();

            MutableComponent value = new TextComponent(String.valueOf(points)).withStyle(ChatFormatting.BOLD);

            manager.bindForSetup(RelicDescriptionScreen.LEVELING_POINT);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, RelicDescriptionScreen.LEVELING_POINT);

            blit(pPoseStack, x + backgroundWidth + 5, y - 2, 0, 0, 50, 31, 50, 31);

            MC.font.draw(pPoseStack, value, x + backgroundWidth + 39 - (MC.font.width(value) / 2F), y + 10, 0xffce96);

            pPoseStack.popPose();
        }

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

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
            int renderY = y + 87;

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

            RenderUtils.renderAnimatedTextureFromCenter(pPoseStack, x + backgroundWidth + 5 + (50 / 2f), y - 2 + (31 / 2f), 64, 768, 64, 64, 1F, AnimationData.builder()
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