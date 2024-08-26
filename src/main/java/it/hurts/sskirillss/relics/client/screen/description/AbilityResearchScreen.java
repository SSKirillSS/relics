package it.hurts.sskirillss.relics.client.screen.description;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IAutoScaledScreen;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.IRelicScreenProvider;
import it.hurts.sskirillss.relics.client.screen.description.data.BurnPoint;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionUtils;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.LogoWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.LuckPlateWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.PlayerExperiencePlateWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.PointsPlateWidget;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AbilityResearchScreen extends Screen implements IAutoScaledScreen, IRelicScreenProvider {
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

    public int ticksExisted = 0;

    private List<BurnPoint> points = new ArrayList<>();

    public AbilityResearchScreen(Player player, int container, int slot, Screen screen, String ability) {
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

        this.addRenderableWidget(new LogoWidget(x + 313, y + 57, this));

        this.addRenderableWidget(new PointsPlateWidget(x + 313, y + 77, this));
        this.addRenderableWidget(new PlayerExperiencePlateWidget(x + 313, y + 102, this));
        this.addRenderableWidget(new LuckPlateWidget(x + 313, y + 127, this));
    }

    @Override
    public void tick() {
        super.tick();

        ticksExisted++;

        stack = DescriptionUtils.gatherRelicStack(minecraft.player, slot);

        for (BurnPoint point : points) {
            if (point.getLifeTime() > 0) {
                point.setLifeTime(point.getLifeTime() - 1);

                point.setXO(point.getX());
                point.setYO(point.getY());

                point.setX(point.getX() + point.getDeltaX());
                point.setY(point.getY() + point.getDeltaY());
            }
        }
    }

    private BurnPoint addPoint(BurnPoint point) {
        var optional = points.stream().filter(entry -> entry.getLifeTime() == 0).findFirst();

        if (optional.isEmpty())
            points.add(point);
        else optional.get().set(point);

        return point;
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

            float color = (float) (0.75F + (Math.sin((player.tickCount + pPartialTick) * 0.2F) * 0.25F));

            RenderSystem.setShaderColor(color, color, color, 1F);

            guiGraphics.blit(card, x + 67, y + 54, 0, 0, 110, 155, 110, 155);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            RenderSystem.setShaderTexture(0, ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/test_fog.png"));

            addPoint(new BurnPoint(pMouseX, pMouseY, 0F, 0F, 5, 0.15F));

            List<Vector2f> positions = Lists.newArrayList(new Vector2f(pMouseX, pMouseY));
            List<Float> scales = Lists.newArrayList(0.2F);
            List<Float> noises = Lists.newArrayList(10F);

            for (BurnPoint point : points) {
                boolean shouldRender = point.getLifeTime() > 0;

                float diff = Mth.clamp(point.getLifeTime() - pPartialTick, 0.01F, point.getMaxLifeTime()) / point.getMaxLifeTime();

                positions.add(new Vector2f(shouldRender ? Mth.lerp(pPartialTick, point.getXO(), point.getX()) : -100, shouldRender ? Mth.lerp(pPartialTick, point.getYO(), point.getY()) : -100));
                scales.add(point.getScale() * diff);
                noises.add(10F * diff);
            }

            RenderUtils.renderRevealingPanel(poseStack, x + 67, y + 54, 110, 155, positions, scales, noises, (ticksExisted + pPartialTick) / 50F);

            guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/test_background.png"), x + 60, y + 45, 0, 0, 242, 176, 242, 176);
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