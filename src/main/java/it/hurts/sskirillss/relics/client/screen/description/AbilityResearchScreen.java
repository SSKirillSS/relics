package it.hurts.sskirillss.relics.client.screen.description;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.client.screen.base.IAutoScaledScreen;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.base.IRelicScreenProvider;
import it.hurts.sskirillss.relics.client.screen.description.data.BurnPoint;
import it.hurts.sskirillss.relics.client.screen.description.data.ResearchParticleData;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionTextures;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionUtils;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.LogoWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.LuckPlateWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.PlayerExperiencePlateWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.general.PointsPlateWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.research.StarWidget;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.items.relics.base.data.research.StarData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.research.PacketManageLink;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import it.hurts.sskirillss.relics.utils.data.GUIRenderer;
import it.hurts.sskirillss.relics.utils.data.SpriteOrientation;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    public int x;
    public int y;

    @Nullable
    public StarData selectedStar;

    private List<BurnPoint> points = new ArrayList<>();

    private List<StarWidget> stars = new ArrayList<>();

    public AbilityResearchScreen(Player player, int container, int slot, Screen screen, String ability) {
        super(Component.empty());

        this.container = container;
        this.slot = slot;
        this.screen = screen;

        this.ability = ability;

        stack = DescriptionUtils.gatherRelicStack(player, slot);
    }

    public int getTotalConnectionsCount(StarData star) {
        if (!(stack.getItem() instanceof IRelicItem relic))
            return 0;

        return relic.getAbilityData(ability).getResearchData().getConnectedStars(star).size();
    }

    public int getOccupiedConnectionsCount(StarData star) {
        if (!(stack.getItem() instanceof IRelicItem relic))
            return 0;

        int index = star.getIndex();

        return (int) relic.getResearchLinks(stack, ability).entries().stream()
                .filter(entry -> entry.getKey() == index || entry.getValue() == index)
                .map(entry -> entry.getKey() < entry.getValue()
                        ? entry.getKey() + "-" + entry.getValue()
                        : entry.getValue() + "-" + entry.getKey())
                .distinct()
                .count();
    }

    @Override
    protected void init() {
        this.x = (this.width - backgroundWidth) / 2;
        this.y = (this.height - backgroundHeight) / 2;

        if (stack == null || !(stack.getItem() instanceof IRelicItem relic))
            return;

        this.addRenderableWidget(new LogoWidget(x + 313, y + 57, this));

        this.addRenderableWidget(new PointsPlateWidget(x + 313, y + 77, this));
        this.addRenderableWidget(new PlayerExperiencePlateWidget(x + 313, y + 102, this));
        this.addRenderableWidget(new LuckPlateWidget(x + 313, y + 127, this));

        int starSize = 17;

        for (var entry : relic.getAbilityData(ability).getResearchData().getStars().values())
            stars.add(this.addWidget(new StarWidget((int) (x + 67 + (entry.getX() * 5F) - starSize / 2F), (int) (y + 54 + (entry.getY() * 5F) - starSize / 2F), this, entry)));
    }

    @Override
    public void tick() {
        super.tick();

        stack = DescriptionUtils.gatherRelicStack(minecraft.player, slot);

        for (BurnPoint point : points) {
            point.getTicker().accept(point);

            if (point.getLifeTime() > 0) {
                point.setLifeTime(point.getLifeTime() - 1);

                point.setXO(point.getX());
                point.setYO(point.getY());
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

            float color = (float) (0.5F + (Math.sin((player.tickCount + pPartialTick) * 0.1F) * 0.1F));

            RenderSystem.setShaderColor(color, color, color, 1F);

            guiGraphics.blit(card, x + 67, y + 54, 0, 0, 110, 155, 110, 155);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        }

        {
            poseStack.pushPose();

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            RenderSystem.setShaderTexture(0, ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/test_fog.png"));

            addPoint(BurnPoint.builder(pMouseX, pMouseY, 5, 0.15F).build());

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

            poseStack.translate(0, 0, 5000);

            RenderUtils.renderRevealingPanel(poseStack, x + 67, y + 54, 110, 155, positions, scales, noises, (player.tickCount + pPartialTick) / 50F);

            guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/test_background.png"), x + 60, y + 45, 0, 0, 242, 176, 242, 176);

            poseStack.popPose();
        }

        {
            ResearchData researchData = relic.getAbilityData(ability).getResearchData();

            for (var link : relic.getResearchLinks(stack, ability).entries()) {
                var start = researchData.getStars().get(link.getKey()).getPos();
                var end = researchData.getStars().get(link.getValue()).getPos();

                drawLink(poseStack, getScaledPos(start), getScaledPos(end), pMouseX, pMouseY, pPartialTick);
            }

            if (selectedStar != null) {
                var pos = selectedStar.getPos();

                drawLink(poseStack, getScaledPos(pos), new Vec2(pMouseX, pMouseY), pMouseX, pMouseY, pPartialTick);
            }
        }

        {
            for (StarWidget widget : stars) {
                widget.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
            }
        }
    }

    public Vec2 getScaledPos(Vec2 pos) {
        int scale = 5;

        return new Vec2(x + 67 + (pos.x * scale), y + 54 + (pos.y * scale));
    }

    private void drawLink(PoseStack poseStack, Vec2 start, Vec2 end, int mouseX, int mouseY, float partialTick) {
        poseStack.pushPose();

        float offset = (float) (Math.sin(((minecraft.player.tickCount + partialTick + start.length()) * 0.25F)) * 0.25F);
        float color = 1.25F + offset;

        if (isHoveringConnection(start, end, mouseX, mouseY) && stars.stream().noneMatch(AbstractWidget::isHovered))
            RenderSystem.setShaderColor(color, 0.25F, 0.25F, 0.75F + offset);
        else
            RenderSystem.setShaderColor(color, color, color, 0.75F + offset);

        RenderSystem.enableBlend();

        int width = 6;
        int height = 4;

        int distance = (int) Math.sqrt(start.distanceToSqr(end));

        poseStack.translate(start.x, start.y, 0);

        poseStack.mulPose(Axis.ZP.rotationDegrees(getAngle(start, end)));

        poseStack.translate(-(width / 2F), -(height / 2F), 0);

        RenderSystem.setShaderTexture(0, ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/research/line.png"));

        GUIRenderer.begin(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/research/line.png"), poseStack)
                .pos(0, 0)
                .texSize(width, height * 6)
                .patternSize(distance, height)
                .orientation(SpriteOrientation.TOP_LEFT)
                .animation(AnimationData.builder()
                        .frame(0, 2).frame(1, 2).frame(2, 2)
                        .frame(3, 2).frame(4, 2).frame(5, 2)
                )
                .end();

        poseStack.mulPose(Axis.ZP.rotationDegrees(-getAngle(start, end)));

        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private void executeForConnection(Vec2 start, Vec2 end, float step, Consumer<Vec2> task) {
        int steps = (int) (Math.sqrt(start.distanceToSqr(end)) / step);

        Vec2 direction = new Vec2(end.x - start.x, end.y - start.y).normalized();

        for (int i = 0; i <= steps; i++) {
            Vec2 point = new Vec2(direction.x * step * i, direction.y * step * i).add(start);

            task.accept(getScaledPos(point));
        }
    }

    private boolean isHoveringConnection(Vec2 start, Vec2 end, int mouseX, int mouseY) {
        float minDistance = 7F;
        float thickness = 4F;

        float x1 = start.x;
        float y1 = start.y;

        float x2 = end.x;
        float y2 = end.y;

        double distanceToStart = Math.hypot(mouseX - x1, mouseY - y1);
        double distanceToEnd = Math.hypot(mouseX - x2, mouseY - y2);

        if (distanceToStart < minDistance || distanceToEnd < minDistance)
            return false;

        double collinearity = (x2 - x1) * (mouseY - y1) - (y2 - y1) * (mouseX - x1);

        double lineLength = Math.hypot(x2 - x1, y2 - y1);
        double distanceFromLine = Math.abs(collinearity / lineLength);

        if (distanceFromLine > thickness / 2)
            return false;

        return Math.min(x1, x2) - thickness / 2 <= mouseX && mouseX <= Math.max(x1, x2) + thickness / 2
                && Math.min(y1, y2) - thickness / 2 <= mouseY && mouseY <= Math.max(y1, y2) + thickness / 2;
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
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (stack.getItem() instanceof IRelicItem relic) {
            ResearchData researchData = relic.getResearchData(ability);

            if (pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                Pair<Integer, Integer> toRemove = null;

                if (stars.stream().noneMatch(AbstractWidget::isHovered)) {
                    for (var link : relic.getResearchLinks(stack, ability).entries())
                        if (isHoveringConnection(getScaledPos(researchData.getStars().get(link.getKey()).getPos()), getScaledPos(researchData.getStars().get(link.getValue()).getPos()), (int) pMouseX, (int) pMouseY))
                            toRemove = Pair.of(link.getKey(), link.getValue());

                    RandomSource random = minecraft.player.getRandom();

                    if (toRemove != null) {
                        int start = toRemove.getKey();
                        int end = toRemove.getValue();

                        NetworkHandler.sendToServer(new PacketManageLink(container, slot, ability, PacketManageLink.Operation.REMOVE, start, end));

                        executeForConnection(researchData.getStars().get(start).getPos(), researchData.getStars().get(end).getPos(), 0.1F, point -> {
                            ParticleStorage.addParticle(this, new ResearchParticleData(new Color(100 + random.nextInt(150), random.nextInt(25), 200 + random.nextInt(50)),
                                    point.x + MathUtils.randomFloat(random), point.y + MathUtils.randomFloat(random), 1F + (random.nextFloat() * 0.25F), 10 + random.nextInt(50), random.nextFloat() * 0.01F));
                        });

                        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.DISCONNECT_STARS.get(), 0.75F + minecraft.player.getRandom().nextFloat() * 0.5F, 0.75F));
                    }
                }
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (selectedStar != null) {
                RandomSource random = minecraft.player.getRandom();

                for (StarWidget widget : stars) {
                    if (!widget.isHovered())
                        continue;

                    int start = selectedStar.getIndex();
                    int end = widget.getStar().getIndex();

                    StarData star = widget.getStar();

                    if (start == end || getOccupiedConnectionsCount(star) >= getTotalConnectionsCount(star))
                        continue;

                    NetworkHandler.sendToServer(new PacketManageLink(container, slot, ability, PacketManageLink.Operation.ADD, start, end));

                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.CONNECT_STARS.get(), (float) (0.25F + (1F - (Math.sqrt(selectedStar.getPos().distanceToSqr(widget.getStar().getPos())) / 35F))), 0.75F));

                    executeForConnection(selectedStar.getPos(), widget.getStar().getPos(), 0.25F, point -> {
                        ParticleStorage.addParticle(this, new ResearchParticleData(new Color(100 + random.nextInt(150), random.nextInt(25), 200 + random.nextInt(50)),
                                point.x + MathUtils.randomFloat(random), point.y + MathUtils.randomFloat(random), 1F + (random.nextFloat() * 0.25F), 20 + random.nextInt(60), random.nextFloat() * 0.025F));
                    });

                    break;
                }

                selectedStar = null;
            }
        }

        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void rebuildWidgets() {
        stars.clear();

        super.rebuildWidgets();
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

    public float getAngle(Vec2 from, Vec2 to) {
        float angle = (float) Math.toDegrees(Math.atan2(to.y - from.y, to.x - from.x));

        if (angle < 0)
            angle += 360;

        return angle;
    }
}