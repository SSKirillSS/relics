package it.hurts.sskirillss.relics.client.screen.description.widgets.research;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.client.screen.base.ITickingWidget;
import it.hurts.sskirillss.relics.client.screen.description.AbilityResearchScreen;
import it.hurts.sskirillss.relics.client.screen.description.widgets.base.AbstractDescriptionWidget;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.research.StarData;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import it.hurts.sskirillss.relics.utils.data.GUIRenderer;
import it.hurts.sskirillss.relics.utils.data.SpriteOrientation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public class StarWidget extends AbstractDescriptionWidget implements ITickingWidget {
    private AbilityResearchScreen screen;
    private StarData star;

    public StarWidget(int x, int y, AbilityResearchScreen screen, StarData star) {
        super(x, y, 17, 17);

        this.screen = screen;
        this.star = star;
    }

    @Override
    public void onPress() {
        screen.selectedStar = star;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !(screen.getStack().getItem() instanceof IRelicItem relic))
            return;

        PoseStack poseStack = guiGraphics.pose();

        float time = (player.tickCount + pPartialTick + (star.getIndex() * 100F));

        {
            poseStack.pushPose();

            RenderSystem.enableBlend();

            poseStack.translate(getX() + width / 2F, getY() + height / 2F, 0);

            int count = relic.getAbilityData(screen.ability).getResearchData().getConnectedStars(star).size();

            float angle = time * 0.05F;

            float radius = (float) (9 + Math.sin(time * 0.1F) * 0.75F);

            float angleStep = (float) (2 * Math.PI / count);

            for (int i = 0; i < count; i++) {
                float color = (float) (1.25F + (Math.sin(time + (i * 10)) * 0.5F));

                RenderSystem.setShaderColor(color, color, color, 1F);

                float currentAngle = angle + i * angleStep;

                float x = (float) (radius * Math.cos(currentAngle));
                float y = (float) (radius * Math.sin(currentAngle));

                GUIRenderer.begin(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/research/dot.png"), poseStack)
                        .pos(x, y)
                        .texSize(3, 3)
                        .orientation(SpriteOrientation.CENTER)
                        .end();
            }

            RenderSystem.disableBlend();

            poseStack.popPose();
        }

        {
            poseStack.pushPose();

            //guiGraphics.drawString(MC.font, star.getIndex() + "", getX(),getY(), 0xFFFFFF);

            float color = (float) (1.1F + (Math.sin(time) * 0.2F));

            RenderSystem.setShaderColor(color, color, color, 1F);

            RenderSystem.enableBlend();

            poseStack.translate(getX() + width / 2F, getY() + height / 2F, 0);
            poseStack.mulPose(Axis.ZN.rotationDegrees((time * 0.75F) * (star.getIndex() % 2 == 0 ? 1 : -1)));

            GUIRenderer.begin(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/research/star.png"), poseStack)
                    .texSize(17, 136)
                    .patternSize(17, 17)
                    .orientation(SpriteOrientation.CENTER)
                    .scale((float) (1F + (Math.sin(time * 0.5F) * 0.1F)))
                    .animation(AnimationData.builder()
                            .frame(0, 2).frame(1, 2)
                            .frame(2, 2).frame(3, 2)
                            .frame(4, 2).frame(5, 2)
                            .frame(6, 2).frame(7, 2))
                    .end();

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            RenderSystem.disableBlend();

            poseStack.popPose();
        }
    }

    @Override
    public void onTick() {
//        if (!(provider.getStack().getItem() instanceof IRelicItem relic) || MC.player == null)
//            return;
//
//        RandomSource random = MC.player.getRandom();
//
//        if (MC.player.tickCount % 5 == 0) {
//            ParticleStorage.addParticle((Screen) provider, new ExperienceParticleData(new Color(200 + random.nextInt(50), 150 + random.nextInt(100), 0),
//                    getX() + random.nextInt(width), getY() + random.nextInt(3), 1F + (random.nextFloat() * 0.25F), 50 + random.nextInt(50)));
//        }
    }
}