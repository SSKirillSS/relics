package it.hurts.sskirillss.relics.client.screen.description.data;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.description.data.base.ParticleData;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Random;

public class ExperienceParticleData extends ParticleData {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/description/experience_particle.png");

    public ExperienceParticleData(Color color, float xStart, float yStart, float scale, int lifeTime) {
        super(TEXTURE, color, xStart, yStart, scale, lifeTime);
    }

    @Override
    public void tick(Screen screen) {
        LocalPlayer player = screen.getMinecraft().player;

        if (player == null)
            return;

        Random random = player.getRandom();

        setX((float) (getX() + (Math.sin(getLifeTime() * 0.15F) * (0.1F + (random.nextFloat() * 0.25F)))));
        setY(getY() - 0.2F);
    }

    @Override
    public void render(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft MC = screen.getMinecraft();

        float lifePercentage = 1F - ((getMaxLifeTime() - getLifeTime()) / 100F);

        RenderSystem.setShaderColor(getColor().getRed() / 255F, getColor().getGreen() / 255F, getColor().getBlue() / 255F, 1F * lifePercentage);
        RenderSystem.setShaderTexture(0, getTexture());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        MC.getTextureManager().getTexture(getTexture()).setBlurMipmap(true, false);

        RenderUtils.renderTextureFromCenter(poseStack, getX(), getY(), 8, 8, getScale() * lifePercentage);

        MC.getTextureManager().getTexture(getTexture()).restoreLastBlurMipmap();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }
}