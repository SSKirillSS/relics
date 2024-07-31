package it.hurts.sskirillss.relics.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;

public class RenderUtils {
    public static void renderAnimatedTextureFromCenter(PoseStack matrix, float centerX, float centerY, float texWidth, float texHeight, float patternWidth, float patternHeight, float scale, AnimationData animation) {
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null)
            return;

        renderAnimatedTextureFromCenter(matrix, centerX, centerY, texWidth, texHeight, patternWidth, patternHeight, scale, animation, level.getGameTime());
    }

    public static void renderAnimatedTextureFromCenter(PoseStack matrix, float centerX, float centerY, float texWidth, float texHeight, float patternWidth, float patternHeight, float scale, AnimationData animation, long ticks) {
        Pair<Integer, Integer> pair = animation.getFrameByTime(ticks);

        renderTextureFromCenter(matrix, centerX, centerY, 0, patternHeight * pair.getKey(), texWidth, texHeight, patternWidth, patternHeight, scale);
    }

    public static void renderTextureFromCenter(PoseStack matrix, float centerX, float centerY, float width, float height, float scale) {
        renderTextureFromCenter(matrix, centerX, centerY, 0, 0, width, height, width, height, scale);
    }

    public static void renderTextureFromCenter(PoseStack matrix, float centerX, float centerY, float texOffX, float texOffY, float texWidth, float texHeight, float width, float height, float scale) {
        BufferBuilder builder = Tesselator.getInstance().getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        matrix.pushPose();

        matrix.translate(centerX, centerY, 0);
        matrix.scale(scale, scale, scale);

        Matrix4f m = matrix.last().pose();

        float u1 = texOffX / texWidth;
        float u2 = (texOffX + width) / texWidth;
        float v1 = texOffY / texHeight;
        float v2 = (texOffY + height) / texHeight;

        float w2 = width / 2F;
        float h2 = height / 2F;

        builder.vertex(m, -w2, +h2, 0).uv(u1, v2).endVertex();
        builder.vertex(m, +w2, +h2, 0).uv(u2, v2).endVertex();
        builder.vertex(m, +w2, -h2, 0).uv(u2, v1).endVertex();
        builder.vertex(m, -w2, -h2, 0).uv(u1, v1).endVertex();

        matrix.popPose();

        BufferUploader.drawWithShader(builder.end());
    }
}