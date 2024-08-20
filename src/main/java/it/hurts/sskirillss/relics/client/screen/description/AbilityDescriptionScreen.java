package it.hurts.sskirillss.relics.client.screen.description;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IAutoScaledScreen;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.IRelicScreenProvider;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionUtils;
import it.hurts.sskirillss.relics.client.screen.description.widgets.ability.BigAbilityCardWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.ability.RerollActionWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.ability.ResetActionWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.ability.UpgradeActionWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.LogoWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.LuckPlateWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.PlayerExperiencePlateWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.PointsPlateWidget;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcon;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class AbilityDescriptionScreen extends Screen implements IAutoScaledScreen, IRelicScreenProvider {
    public final Screen screen;

    @Getter
    public final int container;
    @Getter
    public final int slot;
    @Getter
    public ItemStack stack;

    public final String ability;

    public int backgroundHeight = 256;
    public int backgroundWidth = 418;

    public UpgradeActionWidget upgradeButton;
    public RerollActionWidget rerollButton;
    public ResetActionWidget resetButton;

    public AbilityDescriptionScreen(Player player, int container, int slot, Screen screen, String ability) {
        super(Component.empty());

        this.container = container;
        this.slot = slot;
        this.screen = screen;

        this.ability = ability;

        stack = DescriptionUtils.gatherRelicStack(player, slot);
    }

    @Override
    protected void init() {
        if (stack == null || !(stack.getItem() instanceof IRelicItem))
            return;

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        this.addRenderableWidget(new BigAbilityCardWidget(x + 60, y + 47, this, ability));

        this.addRenderableWidget(new LogoWidget(x + 313, y + 57, this));

        this.addRenderableWidget(new PointsPlateWidget(x + 313, y + 77, this));
        this.addRenderableWidget(new PlayerExperiencePlateWidget(x + 313, y + 102, this));
        this.addRenderableWidget(new LuckPlateWidget(x + 313, y + 127, this));

        this.upgradeButton = new UpgradeActionWidget(x + 288, y + 152, this, ability);
        this.rerollButton = new RerollActionWidget(x + 288, y + 170, this, ability);
        this.resetButton = new ResetActionWidget(x + 288, y + 188, this, ability);

        this.addRenderableWidget(upgradeButton);
        this.addRenderableWidget(rerollButton);
        this.addRenderableWidget(resetButton);
    }

    @Override
    public void tick() {
        super.tick();

        stack = DescriptionUtils.gatherRelicStack(minecraft.player, slot);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(guiGraphics, pMouseX, pMouseY, pPartialTick);

        LocalPlayer player = minecraft.player;

        if (stack == null || !(stack.getItem() instanceof IRelicItem relic) || player == null)
            return;

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return;

        int level = relic.getAbilityPoints(stack, ability);

        PoseStack poseStack = guiGraphics.pose();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, DescriptionTextures.SPACE_BACKGROUND);

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        int yOff = 0;
        int xOff = 0;

        RenderUtils.renderAnimatedTextureFromCenter(poseStack, x + (backgroundWidth / 2F), y + (backgroundHeight / 2F), 418, 4096, backgroundWidth, backgroundHeight, 1F, AnimationData.builder()
                .frame(0, 2).frame(1, 2).frame(2, 2)
                .frame(3, 2).frame(4, 2).frame(5, 2)
                .frame(6, 2).frame(7, 2).frame(8, 2)
                .frame(9, 2).frame(10, 2).frame(11, 2)
                .frame(12, 2).frame(13, 2).frame(14, 2)
                .frame(15, 2)
        );

        {
            ResourceLocation card = ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/abilities/" + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + "/" + relic.getAbilityData(ability).getIcon().apply(minecraft.player, stack, ability) + ".png");

            float color = (float) (1.05F + (Math.sin((player.tickCount + (ability.length() * 10)) * 0.2F) * 0.1F));

            RenderSystem.setShaderColor(color, color, color, 1F);

            guiGraphics.blit(card, x + 67, y + 57, 34, 49, 0, 0, 22, 31, 22, 31);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            guiGraphics.blit(DescriptionTextures.ABILITY_BACKGROUND, x + 60, y + 47, 0, 0, 243, 77, 243, 77);

            poseStack.pushPose();

            int quality = relic.getAbilityQuality(stack, ability);
            boolean isAliquot = quality % 2 == 1;

            for (int i = 0; i < Math.floor(quality / 2D); i++) {
                guiGraphics.blit(DescriptionTextures.BIG_STAR_ACTIVE, x + xOff + 64, y + 110, 0, 0, 8, 7, 8, 7);

                xOff += 8;
            }

            if (isAliquot)
                guiGraphics.blit(DescriptionTextures.BIG_STAR_ACTIVE, x + xOff + 64, y + 110, 0, 0, 4, 7, 8, 7);

            poseStack.popPose();

            poseStack.pushPose();

            MutableComponent pointsComponent = Component.literal(String.valueOf(level)).withStyle(ChatFormatting.BOLD);

            poseStack.scale(0.75F, 0.75F, 1F);

            guiGraphics.drawString(minecraft.font, pointsComponent, (int) (((x + 85.5F) * 1.33F) - (minecraft.font.width(pointsComponent) / 2F)), (int) ((y + 51) * 1.33F), 0xFFE278, true);

            poseStack.popPose();

            poseStack.pushPose();

            poseStack.scale(0.75F, 0.75F, 0.75F);

            guiGraphics.drawString(minecraft.font, Component.translatable("tooltip.relics." + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + ".ability." + ability)
                    .withStyle(ChatFormatting.BOLD), (int) ((x + 113) * 1.33F), (int) ((y + 66) * 1.33F), 0x662f13, false);

            yOff = 9;

            poseStack.popPose();

            poseStack.pushPose();

            poseStack.scale(0.5F, 0.5F, 0.5F);

            for (FormattedCharSequence line : minecraft.font.split(Component.translatable("tooltip.relics." + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + ".ability." + ability + ".description"), 340)) {
                guiGraphics.drawString(minecraft.font, line, (x + 112) * 2, (y + 73) * 2 + yOff, 0x662f13, false);

                yOff += 9;
            }

            poseStack.popPose();
        }

        {
            guiGraphics.blit(DescriptionTextures.STATS_BACKGROUND, x + 60, y + 133, 0, 0, 243, 88, 243, 88);
        }

        AbilityData abilityData = relicData.getAbilities().getAbilities().get(ability);

        int maxLevel = abilityData.getMaxLevel();

        boolean isLocked = !relic.canUseAbility(stack, ability);

        boolean isHoveredUpgrade = !isLocked && upgradeButton.isHovered();
        boolean isHoveredReroll = !isLocked && rerollButton.isHovered();
        boolean isHoveredReset = !isLocked && resetButton.isHovered();

        yOff = 0;

        int step = 0;

        for (var entry : relic.getAbilityComponent(stack, ability).stats().entrySet()) {
            String stat = entry.getKey();
            StatData statData = relic.getStatData(ability, stat);

            if (statData != null) {
                MutableComponent cost = Component.literal(String.valueOf(statData.getFormatValue().apply(relic.getStatValue(stack, ability, stat))));

                if (isHoveredUpgrade && level < maxLevel) {
                    cost.append(" ➠ " + statData.getFormatValue().apply(relic.getStatValue(stack, ability, stat, level + 1)));
                }

                if (isHoveredReroll) {
                    cost.append(" ➠ ").append(Component.literal("X.XXX").withStyle(ChatFormatting.OBFUSCATED));
                }

                if (isHoveredReset && level > 0) {
                    cost.append(" ➠ " + statData.getFormatValue().apply(relic.getStatValue(stack, ability, stat, 0)));
                }

                poseStack.pushPose();

                StatIcon icon = statData.getIcon();

                Color color = new Color(icon.getColor());

                float blinkOffset = (float) (Math.sin((player.tickCount + (step * 10)) * 0.2F) * 0.1F);

                RenderSystem.setShaderColor(color.getRed() / 255F + blinkOffset, color.getGreen() / 255F + blinkOffset, color.getBlue() / 255F + blinkOffset, 1F);
                RenderSystem.enableBlend();

                guiGraphics.blit(icon.getPath(), x + 82 + (step % 2) * 2, y + yOff + 148, 0, 0, 15, 16, 15, 16);

                RenderSystem.disableBlend();
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

                poseStack.popPose();

                poseStack.pushPose();

                poseStack.scale(0.5F, 0.5F, 0.5F);

                guiGraphics.drawString(minecraft.font, Component.translatable("tooltip.relics." + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + ".ability." + ability + ".stat." + stat + ".title").withStyle(ChatFormatting.BOLD), (x + 103) * 2, (y + yOff + 151) * 2, 0x662f13, false);

                guiGraphics.drawString(minecraft.font, Component.literal("● ").append(Component.translatable("tooltip.relics." + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + ".ability." + ability + ".stat." + stat + ".value", cost)), (x + 108) * 2, (y + yOff + 157) * 2, 0x662f13, false);

                poseStack.popPose();

                xOff = 0;

                for (int i = 0; i < 5; i++) {
                    guiGraphics.blit(DescriptionTextures.SMALL_STAR_HOLE, x + xOff + 254, y + yOff + 151, 0, 0, 4, 4, 4, 4);

                    xOff += 5;
                }

                xOff = 0;

                int quality = relic.getStatQuality(stack, ability, stat);
                boolean isAliquot = quality % 2 == 1;

                for (int i = 0; i < Math.floor(quality / 2D); i++) {
                    guiGraphics.blit(DescriptionTextures.SMALL_STAR_ACTIVE, x + xOff + 254, y + yOff + 151, 0, 0, 4, 4, 4, 4);

                    xOff += 5;
                }

                if (isAliquot)
                    guiGraphics.blit(DescriptionTextures.SMALL_STAR_ACTIVE, x + xOff + 254, y + yOff + 151, 0, 0, 2, 4, 4, 4);

                yOff += 14;

                step++;
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        for (GuiEventListener listener : this.children()) {
            if (listener instanceof AbstractButton button && button.isHovered()
                    && button instanceof IHoverableWidget widget) {
                guiGraphics.pose().translate(0, 0, 100);

                widget.onHovered(guiGraphics, pMouseX, pMouseY);
            }
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
            this.onClose();

            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(new RelicDescriptionScreen(minecraft.player, container, slot, screen));
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