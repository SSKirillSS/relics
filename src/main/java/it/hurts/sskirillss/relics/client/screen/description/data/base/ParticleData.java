package it.hurts.sskirillss.relics.client.screen.description.data.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.utils.data.GUIRenderer;
import it.hurts.sskirillss.relics.utils.data.SpriteOrientation;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Data
@Accessors(chain = true)
public class ParticleData {
    private final ResourceLocation texture;

    private final Color color;

    private final float xStart;
    private final float yStart;

    private final int maxLifeTime;

    private float scale;

    private int lifeTime;

    private float roll;

    private float deltaX;
    private float deltaY;

    private float xO;
    private float yO;

    private float x;
    private float y;
    private float z;

    public ParticleData(ResourceLocation texture, Color color, float xStart, float yStart, float scale, int lifeTime) {
        this.texture = texture;

        this.color = color;

        this.xStart = xStart;
        this.yStart = yStart;

        this.scale = scale;

        this.lifeTime = lifeTime;

        this.maxLifeTime = lifeTime;

        this.roll = 0F;

        this.deltaX = 0F;
        this.deltaY = 0F;

        this.xO = xStart;
        this.yO = yStart;

        this.x = xStart;
        this.y = yStart;
        this.z = 0F;
    }

    public void tick(Screen screen) {
        this.xO = x;
        this.yO = y;
    }

    public void render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        RenderSystem.setShaderColor(getColor().getRed() / 255F, getColor().getGreen() / 255F, getColor().getBlue() / 255F, getColor().getAlpha());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        poseStack.translate(Mth.lerp(partialTick, getXO(), getX()), Mth.lerp(partialTick, getYO(), getY()), 0);

        poseStack.mulPose(Axis.ZP.rotationDegrees(getRoll()));

        GUIRenderer.begin(getTexture(), guiGraphics.pose())
                .scale(getScale())
                .pos(0, 0)
                .texSize(1, 1)
                .orientation(SpriteOrientation.CENTER)
                .end();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }
}