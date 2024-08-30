package it.hurts.sskirillss.relics.utils.data;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.function.Supplier;

@Setter
@NoArgsConstructor
@OnlyIn(Dist.CLIENT)
@Accessors(fluent = true, chain = true)
public final class GUIRenderer {
    private static final GUIRenderer INSTANCE = new GUIRenderer();

    @Setter(AccessLevel.NONE)
    private ResourceLocation texture;
    @Setter(AccessLevel.NONE)
    private PoseStack pose;

    private float posX;
    private float posY;

    private int texWidth;
    private int texHeight;

    private int patternWidth;
    private int patternHeight;

    private int texOffX;
    private int texOffY;

    private float scale;

    private Supplier<Long> time;
    private AnimationData animation;

    private SpriteOrientation orientation;

    public static GUIRenderer begin(ResourceLocation texture, PoseStack pose) {
        var renderer = INSTANCE;

        renderer.texture = texture;
        renderer.pose = pose;

        renderer.posX = 0F;
        renderer.posY = 0F;

        renderer.texWidth = -1;
        renderer.texHeight = -1;

        renderer.patternWidth = -1;
        renderer.patternHeight = -1;

        renderer.texOffX = 0;
        renderer.texOffY = 0;

        renderer.scale = 1F;

        renderer.time = () -> {
            ClientLevel level = Minecraft.getInstance().level;

            return level == null ? 0 : level.getGameTime();
        };
        renderer.animation = AnimationData.builder()
                .frame(0, Integer.MAX_VALUE);

        renderer.orientation = SpriteOrientation.TOP_LEFT;

        return renderer;
    }

    public GUIRenderer pos(float posX, float posY) {
        var renderer = INSTANCE;

        renderer.posX = posX;
        renderer.posY = posY;

        return renderer;
    }

    public GUIRenderer texSize(int texWidth, int texHeight) {
        var renderer = INSTANCE;

        renderer.texWidth = texWidth;
        renderer.texHeight = texHeight;

        return renderer;
    }

    public GUIRenderer patternSize(int patternWidth, int patternHeight) {
        var renderer = INSTANCE;

        renderer.patternWidth = patternWidth;
        renderer.patternHeight = patternHeight;

        return renderer;
    }

    public GUIRenderer texOff(int texOffX, int texOffY) {
        var renderer = INSTANCE;

        renderer.texOffX = texOffX;
        renderer.texOffY = texOffY;

        return renderer;
    }

    public void end() {
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        Minecraft.getInstance().getTextureManager().getTexture(texture).bind();

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.disableCull();

        if (texHeight == -1)
            texHeight = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        if (texWidth == -1)
            texWidth = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);

        if (patternHeight == -1)
            patternHeight = texHeight;

        if (patternWidth == -1)
            patternWidth = texWidth;

        texOffY = patternHeight * animation.getFrameByTime(time.get()).getKey();

        pose.pushPose();

        float xOff = 0F;
        float yOff = 0F;

        switch (orientation) {
            case CENTER -> {
                xOff = patternWidth / 2F * scale;
                yOff = patternHeight / 2F * scale;
            }
        }

        pose.translate(posX - xOff, posY - yOff, 0);
        pose.scale(scale, scale, 0);

        Matrix4f m = pose.last().pose();

        float u1 = (float) texOffX / texWidth;
        float u2 = (float) (texOffX + patternWidth) / texWidth;
        float v1 = (float) (texOffY + patternHeight) / texHeight;
        float v2 = (float) texOffY / texHeight;

        builder.addVertex(m, 0, patternHeight, 0).setUv(u1, v1);
        builder.addVertex(m, patternWidth, patternHeight, 0).setUv(u2, v1);
        builder.addVertex(m, patternWidth, 0, 0).setUv(u2, v2);
        builder.addVertex(m, 0, 0, 0).setUv(u1, v2);

        pose.popPose();

        BufferUploader.drawWithShader(builder.buildOrThrow());

        RenderSystem.enableCull();
    }
}