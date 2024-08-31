package it.hurts.sskirillss.relics.client.screen.description.data.base;

import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

@Data
@Accessors(chain = true)
public abstract class ParticleData {
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

    public abstract void render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);
}