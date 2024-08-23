package it.hurts.sskirillss.relics.client.screen.description.data;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.client.screen.description.data.base.ParticleData;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ExperienceParticleData extends ParticleData {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/general/pixel_particle.png");

    public ExperienceParticleData(Color color, float xStart, float yStart, float scale, int lifeTime) {
        super(TEXTURE, color, xStart, yStart, scale, lifeTime);
    }

    @Override
    public void tick(Screen screen) {
        LocalPlayer player = screen.getMinecraft().player;

        if (player == null)
            return;

        RandomSource random = player.getRandom();

        float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);

        setX((float) (getX() + (Math.sin((getLifeTime() + partialTicks) * 0.15F) * (0.1F + (random.nextFloat() * 0.5F)))));
        setY(getY() - (0.2F + partialTicks));
    }

    @Override
    public void render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();

        float lifePercentage = 1F - ((getMaxLifeTime() - getLifeTime()) / 100F);

        float blinkOffset = 0.15F + (float) (Math.sin((getLifeTime() + partialTick) * 0.5F) * 0.3F);

        poseStack.pushPose();

        RenderSystem.setShaderColor(getColor().getRed() / 255F + blinkOffset, getColor().getGreen() / 255F + blinkOffset, getColor().getBlue() / 255F + blinkOffset, lifePercentage);
        RenderSystem.setShaderTexture(0, getTexture());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        poseStack.translate(getX(), getY(), 0);

        poseStack.mulPose(Axis.ZP.rotationDegrees((getLifeTime() + partialTick) * 10));

        RenderUtils.renderTextureFromCenter(guiGraphics.pose(), 0, 0, 1, 1, getScale() * lifePercentage);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }
}