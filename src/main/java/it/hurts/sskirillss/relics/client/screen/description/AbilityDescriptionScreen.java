package it.hurts.sskirillss.relics.client.screen.description;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.ability.AbilityRerollButtonWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.ability.AbilityResetButtonWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.ability.AbilityUpgradeButtonWidget;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.QualityUtils;
import it.hurts.sskirillss.relics.tiles.ResearchingTableTile;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.opengl.GL11;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AbilityDescriptionScreen extends Screen {
    private final Minecraft MC = Minecraft.getInstance();

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/description/ability_background.png");
    public static final ResourceLocation WIDGETS = new ResourceLocation(Reference.MODID, "textures/gui/description/relic_widgets.png");

    public final BlockPos pos;
    public ItemStack stack;
    public final String ability;

    public int backgroundHeight = 177;
    public int backgroundWidth = 256;

    public AbilityUpgradeButtonWidget upgradeButton;
    public AbilityRerollButtonWidget rerollButton;
    public AbilityResetButtonWidget resetButton;

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

        this.upgradeButton = new AbilityUpgradeButtonWidget(((this.width - backgroundWidth) / 2) + 209, ((this.height - backgroundHeight) / 2) + 98, this, ability);
        this.rerollButton = new AbilityRerollButtonWidget(((this.width - backgroundWidth) / 2) + 209, ((this.height - backgroundHeight) / 2) + 118, this, ability);
        this.resetButton = new AbilityResetButtonWidget(((this.width - backgroundWidth) / 2) + 209, ((this.height - backgroundHeight) / 2) + 138, this, ability);

        this.addRenderableWidget(upgradeButton);
        this.addRenderableWidget(rerollButton);
        this.addRenderableWidget(resetButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        ClientLevel world = MC.level;

        if (world == null || !(world.getBlockEntity(pos) instanceof ResearchingTableTile tile))
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

        pPoseStack.pushPose();

        pPoseStack.scale(0.5F, 0.5F, 0.5F);

        int level = AbilityUtils.getAbilityPoints(stack, ability);
        int maxLevel = abilityData.getMaxLevel() == -1 ? (relicData.getLevelingData().getMaxLevel() / abilityData.getRequiredPoints()) : abilityData.getMaxLevel();

        MutableComponent name = Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(relic).getPath() + ".ability." + ability);

        if (abilityData.getStats().size() > 0)
            name.append(Component.translatable("tooltip.relics.relic.ability.level", level, maxLevel == -1 ? "∞" : maxLevel));

        guiGraphics.drawString(MC.font, name.withStyle(ChatFormatting.BOLD), x * 2 + 71 * 2, y * 2 + 26 * 2 - 1, 0x412708, false);

        List<FormattedCharSequence> lines = MC.font.split(Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + ability + ".description"), 305);

        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(MC.font, lines.get(i), x * 2 + 71 * 2, y * 2 + i * 9 + 35 * 2, 0x412708, false);
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
                RenderSystem.setShaderTexture(0, TEXTURE);

                manager.bindForSetup(TEXTURE);

                guiGraphics.blit(TEXTURE, x + 32, y + 103 + yOff / 2, 302, 44, 36, 8, texWidth, texHeight);

                for (int i = 1; i < QualityUtils.getStatQuality(stack, ability, stat) + 1; i++) {
                    boolean isAliquot = i % 2 == 1;

                    guiGraphics.blit(TEXTURE, x + 33 + xOff, y + 104 + yOff / 2, (isLocked ? 312 : 303) + (isAliquot ? 0 : 4), 54, isAliquot ? 4 : 3, 7, texWidth, texHeight);

                    xOff += isAliquot ? 4 : 3;
                }

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

                guiGraphics.drawString(MC.font, Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + ability + ".stat." + stat + ".title"), x * 2 + 94 * 2, y * 2 + yOff + 102 * 2, 0x412708, false);

                yOff += 10;

                guiGraphics.drawString(MC.font, Component.literal(" ● ").append(Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + ability + ".stat." + stat + ".value", cost)), x * 2 + 94 * 2, y * 2 + yOff + 102 * 2, 0x412708, false);

                pPoseStack.popPose();

                yOff += 17;
                xOff = 0;
            }
        }

        int points = LevelingUtils.getPoints(stack);

        if (points > 0) {
            manager.bindForSetup(WIDGETS);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, WIDGETS);

            guiGraphics.blit(WIDGETS, x + backgroundWidth - 3, y + 17, 0, 0, 40, 25, texWidth, texHeight);
            guiGraphics.blit(WIDGETS, x + backgroundWidth + 16, y + 22, 0, 27, 16, 13, texWidth, texHeight);

            String value = String.valueOf(points);

            guiGraphics.drawString(MC.font, value, x + backgroundWidth + 7 - font.width(value) / 2, y + 25, 0xFFFFFF);
        }

        ResourceLocation card = new ResourceLocation(Reference.MODID, "textures/gui/description/cards/" + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + "/" + ability + ".png");

        RenderSystem.setShaderTexture(0, card);

        manager.bindForSetup(card);

        if (GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT) == 29) {
            if (isLocked)
                RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, 1F);

            guiGraphics.blit(card, x + 29, y + 19, 30, 43, 0, 0, 20, 29, 20, 29);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        }

        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        guiGraphics.blit(TEXTURE, x + 26, y + 25, 356, 0, 38, 48, texWidth, texHeight);

        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        if (isLocked)
            guiGraphics.blit(TEXTURE, x + 24, y + 16, 258, 101, 42, 59, texWidth, texHeight);
        else
            guiGraphics.blit(TEXTURE, x + 24, y + 16, 258, 40, 42, 59, texWidth, texHeight);

        for (int i = 1; i < QualityUtils.getAbilityQuality(stack, ability) + 1; i++) {
            boolean isAliquot = i % 2 == 1;

            guiGraphics.blit(TEXTURE, x + 27 + xOff, y + 63, (isLocked ? 312 : 303) + (isAliquot ? 0 : 4), 54, isAliquot ? 4 : 3, 7, texWidth, texHeight);

            xOff += isAliquot ? 4 : 3;
        }

        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        for (GuiEventListener listener : this.children()) {
            if (listener instanceof AbstractButton button && button.isHovered()
                    && button instanceof IHoverableWidget widget)
                widget.onHovered(guiGraphics, pMouseX, pMouseY);
        }
    }

    @Override
    public void onClose() {
        MC.setScreen(new RelicDescriptionScreen(pos, stack));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}