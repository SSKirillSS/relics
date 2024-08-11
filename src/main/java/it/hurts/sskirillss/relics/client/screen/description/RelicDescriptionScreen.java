package it.hurts.sskirillss.relics.client.screen.description;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IAutoScaledScreen;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.IRelicScreenProvider;
import it.hurts.sskirillss.relics.client.screen.description.data.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionUtils;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.PlayerExperiencePlateWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.PointsPlateWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.relic.AbilityCardWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.relic.BigRelicCardWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.relic.RelicExperienceWidget;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class RelicDescriptionScreen extends Screen implements IAutoScaledScreen, IRelicScreenProvider {
    public final Screen screen;

    @Getter
    public final int container;
    @Getter
    public final int slot;
    @Getter
    public ItemStack stack;

    private final int backgroundHeight = 256;
    private final int backgroundWidth = 418;

    public RelicDescriptionScreen(Player player, int container, int slot, Screen screen) {
        super(Component.empty());

        this.container = container;
        this.slot = slot;
        this.screen = screen;

        stack = DescriptionUtils.gatherRelicStack(player, slot);
    }

    @Override
    protected void init() {
        if (stack == null || !(stack.getItem() instanceof IRelicItem relic))
            return;

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        this.addRenderableWidget(new BigRelicCardWidget(x + 60, y + 47, this));

        this.addRenderableWidget(new PointsPlateWidget(x + 313, y + 57, this));
        this.addRenderableWidget(new PlayerExperiencePlateWidget(x + 313, y + 82, this));

        this.addRenderableWidget(new RelicExperienceWidget(x + 127, y + 121, this));

        Set<String> abilities = relic.getRelicData().getAbilities().getAbilities().keySet();

        int cardWidth = 32;
        int containerWidth = 209;

        int count = Math.min(5, abilities.size());

        int spacing = cardWidth + 8 + (3 * (5 - count));

        int xOffset = (containerWidth / 2) - (((cardWidth * count) + ((spacing - cardWidth) * Math.max(count - 1, 0))) / 2);

        for (String ability : abilities) {
            this.addRenderableWidget(new AbilityCardWidget(x + 77 + xOffset, y + 153, this, ability));

            xOffset += spacing;
        }
    }

    @Override
    public void tick() {
        super.tick();

        stack = DescriptionUtils.gatherRelicStack(minecraft.player, slot);

        LocalPlayer player = minecraft.player;

        if (player == null || stack == null || !(stack.getItem() instanceof IRelicItem))
            return;

        RandomSource random = player.getRandom();

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        if (player.tickCount % 3 == 0) {
            ParticleStorage.addParticle(this, new ExperienceParticleData(
                    new Color(140, random.nextInt(50), 255),
                    x + 73 + random.nextInt(20), y + 73 + random.nextInt(20),
                    1.5F + (random.nextFloat() * 0.5F), 100 + random.nextInt(50)));
        }
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

        int level = relic.getLevel(stack);

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
            guiGraphics.blit(DescriptionTextures.RELIC_BACKGROUND, x + 60, y + 47, 0, 0, 243, 90, 243, 90);

            poseStack.pushPose();

            int quality = relic.getRelicQuality(stack);
            boolean isAliquot = quality % 2 == 1;

            for (int i = 0; i < Math.floor(quality / 2D); i++) {
                guiGraphics.blit(DescriptionTextures.BIG_STAR_ACTIVE, x + xOff + 64, y + 110, 0, 0, 8, 7, 8, 7);

                xOff += 8;
            }

            if (isAliquot)
                guiGraphics.blit(DescriptionTextures.BIG_STAR_ACTIVE, x + xOff + 64, y + 110, 0, 0, 4, 7, 8, 7);

            poseStack.popPose();

            poseStack.pushPose();

            float scale = 1.75F;

            poseStack.translate(x + 70, y + 69 + Math.sin((player.tickCount + pPartialTick) * 0.15F), 0);
            poseStack.scale(scale, scale, scale);

            guiGraphics.renderItem(stack, 0, 0);

            poseStack.popPose();

            poseStack.pushPose();

            MutableComponent levelComponent = Component.literal(String.valueOf(level)).withStyle(ChatFormatting.BOLD);

            poseStack.scale(0.75F, 0.75F, 1F);

            guiGraphics.drawString(minecraft.font, levelComponent, (int) (((x + 85.5F) * 1.33F) - (minecraft.font.width(levelComponent) / 2F)), (int) ((y + 51) * 1.33F), 0xFFE278, true);

            poseStack.popPose();

            poseStack.pushPose();

            poseStack.scale(0.75F, 0.75F, 0.75F);

            guiGraphics.drawString(minecraft.font, Component.literal(stack.getDisplayName().getString()
                            .replace("[", "").replace("]", ""))
                    .withStyle(ChatFormatting.BOLD), (int) ((x + 113) * 1.33F), (int) ((y + 66) * 1.33F), 0x662f13, false);

            poseStack.popPose();

            poseStack.pushPose();

            poseStack.scale(0.5F, 0.5F, 0.5F);

            yOff = 9;

            for (FormattedCharSequence line : minecraft.font.split(Component.translatable("tooltip.relics." + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + ".description"), 340)) {
                guiGraphics.drawString(minecraft.font, line, (x + 112) * 2, (y + 73) * 2 + yOff, 0x662f13, false);

                yOff += 9;
            }

            poseStack.popPose();
        }

        {
            guiGraphics.blit(DescriptionTextures.ABILITIES_BACKGROUND, x + 60, y + 133, 0, 0, 243, 88, 243, 88);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        for (GuiEventListener listener : this.children()) {
            if (listener instanceof AbstractButton button && button.isHovered()
                    && button instanceof IHoverableWidget widget)
                widget.onHovered(guiGraphics, pMouseX, pMouseY);
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
        Minecraft.getInstance().setScreen(screen);
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