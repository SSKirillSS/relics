package it.hurts.sskirillss.relics.client.screen.description.ability;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.badges.base.AbilityBadge;
import it.hurts.sskirillss.relics.client.screen.base.IAutoScaledScreen;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.IRelicScreenProvider;
import it.hurts.sskirillss.relics.client.screen.description.ability.widgets.*;
import it.hurts.sskirillss.relics.client.screen.description.general.widgets.*;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionUtils;
import it.hurts.sskirillss.relics.client.screen.description.relic.RelicDescriptionScreen;
import it.hurts.sskirillss.relics.init.BadgeRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import it.hurts.sskirillss.relics.utils.data.GUIRenderer;
import it.hurts.sskirillss.relics.utils.data.SpriteOrientation;
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
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;

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
        if (stack == null || !(stack.getItem() instanceof IRelicItem relic))
            return;

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        this.addRenderableWidget(new BigAbilityCardWidget(x + 60, y + 47, this, ability));

        this.addRenderableWidget(new LogoWidget(x + 313, y + 57, this));

        this.addRenderableWidget(new PointsPlateWidget(x + 313, y + 77, this));
        this.addRenderableWidget(new PlayerExperiencePlateWidget(x + 313, y + 102, this));
        this.addRenderableWidget(new LuckPlateWidget(x + 313, y + 127, this));

        int xOff = 0;

        for (AbilityBadge badge : BadgeRegistry.BADGES.getEntries().stream().map(DeferredHolder::get).filter(entry -> entry instanceof AbilityBadge).map(entry -> (AbilityBadge) entry).toList()) {
            if (!badge.isVisible(stack, ability))
                continue;

            this.addRenderableWidget(new AbilityBadgeWidget(x + 270 - xOff, y + 63, this, badge, ability));

            xOff += 15;
        }

        int yOff = 0;

        for (Map.Entry<String, StatData> entry : relic.getAbilityData(ability).getStats().entrySet()) {
            this.addRenderableWidget(new StatWidget(x + 77, y + yOff + 148, this, entry.getKey()));

            yOff += 14;
        }

        this.upgradeButton = this.addRenderableWidget(new UpgradeActionWidget(x + 288, y + 152, this, ability));
        this.rerollButton = this.addRenderableWidget(new RerollActionWidget(x + 288, y + 170, this, ability));
        this.resetButton = this.addRenderableWidget(new ResetActionWidget(x + 288, y + 188, this, ability));
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

        int level = relic.getAbilityLevel(stack, ability);

        PoseStack poseStack = guiGraphics.pose();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, DescriptionTextures.SPACE_BACKGROUND);

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        int yOff = 0;
        int xOff = 0;

        GUIRenderer.begin(DescriptionTextures.SPACE_BACKGROUND, poseStack)
                .texSize(418, 4096)
                .patternSize(backgroundWidth, backgroundHeight)
                .pos(x + (backgroundWidth / 2F), y + (backgroundHeight / 2F))
                .animation(AnimationData.builder()
                        .frame(0, 2).frame(1, 2).frame(2, 2)
                        .frame(3, 2).frame(4, 2).frame(5, 2)
                        .frame(6, 2).frame(7, 2).frame(8, 2)
                        .frame(9, 2).frame(10, 2).frame(11, 2)
                        .frame(12, 2).frame(13, 2).frame(14, 2)
                        .frame(15, 2))
                .end();

        GUIRenderer.begin(DescriptionTextures.TOP_BACKGROUND, poseStack)
                .orientation(SpriteOrientation.TOP_LEFT)
                .pos(x + 60, y + 47)
                .end();

        GUIRenderer.begin(DescriptionTextures.BOTTOM_BACKGROUND, poseStack)
                .orientation(SpriteOrientation.TOP_LEFT)
                .pos(x + 60, y + 133)
                .end();

        ResourceLocation card = ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/abilities/" + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + "/" + relic.getAbilityData(ability).getIcon().apply(minecraft.player, stack, ability) + ".png");

        float color = (float) (1.05F + (Math.sin((player.tickCount + (ability.length() * 10)) * 0.2F) * 0.1F));

        GUIRenderer.begin(card, poseStack)
                .orientation(SpriteOrientation.TOP_LEFT)
                .color(color, color, color, 1F)
                .pos(x + 67, y + 57)
                .texSize(34, 49)
                .end();

        int quality = relic.getAbilityQuality(stack, ability);
        boolean isAliquot = quality % 2 == 1;

        for (int i = 0; i < Math.floor(quality / 2D); i++) {
            GUIRenderer.begin(DescriptionTextures.BIG_STAR_ACTIVE, poseStack)
                    .orientation(SpriteOrientation.TOP_LEFT)
                    .pos(x + xOff + 64, y + 110)
                    .end();

            xOff += 8;
        }

        if (isAliquot)
            GUIRenderer.begin(DescriptionTextures.BIG_STAR_ACTIVE, poseStack)
                    .orientation(SpriteOrientation.TOP_LEFT)
                    .pos(x + xOff + 64, y + 110)
                    .patternSize(4, 7)
                    .texSize(8, 7)
                    .end();

        poseStack.pushPose();

        MutableComponent pointsComponent = Component.literal(String.valueOf(level)).withStyle(ChatFormatting.BOLD);

        poseStack.scale(0.75F, 0.75F, 1F);

        guiGraphics.drawString(minecraft.font, pointsComponent, (int) (((x + 85.5F) * 1.33F) - (minecraft.font.width(pointsComponent) / 2F)), (int) ((y + 51) * 1.33F), 0xFFE278, true);

        guiGraphics.drawString(minecraft.font, Component.translatable("tooltip.relics." + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + ".ability." + ability)
                .withStyle(ChatFormatting.BOLD), (int) ((x + 113) * 1.33F), (int) ((y + 67) * 1.33F), 0x662f13, false);

        yOff = 9;

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.scale(0.5F, 0.5F, 0.5F);

        for (FormattedCharSequence line : minecraft.font.split(Component.translatable("tooltip.relics." + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath() + ".ability." + ability + ".description"), 340)) {
            guiGraphics.drawString(minecraft.font, line, (x + 112) * 2, (y + 74) * 2 + yOff, 0x662f13, false);

            yOff += 9;
        }

        poseStack.popPose();
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