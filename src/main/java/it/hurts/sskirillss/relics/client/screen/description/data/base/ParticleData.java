package it.hurts.sskirillss.relics.client.screen.description.data.base;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Data;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

@Data
public class ParticleData {
    private final ResourceLocation texture;

    private final Color color;

    private final float xStart;
    private final float yStart;

    private final int maxLifeTime;

    private float scale;

    private int lifeTime;

    private float x;
    private float y;

    public ParticleData(ResourceLocation texture, Color color, float xStart, float yStart, float scale, int lifeTime) {
        this.texture = texture;

        this.color = color;

        this.xStart = xStart;
        this.yStart = yStart;

        this.scale = scale;

        this.lifeTime = lifeTime;

        this.maxLifeTime = lifeTime;

        this.x = xStart;
        this.y = yStart;
    }

    public void tick(Screen screen) {

    }

    public void render(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTick) {

    }
}