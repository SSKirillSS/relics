package it.hurts.sskirillss.relics.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.hurts.sskirillss.relics.init.RelicsCoreShaders;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.List;

public class RenderUtils {


    public static void renderRevealingPanel(PoseStack matrices, float x, float y, float sizeX, float sizeY, List<Vector2f> points, List<Float> revealRadiuses, List<Float> noiseSpreads, float time){
        RenderSystem.enableBlend();

        float[] arr = new float[32]; // 16 points max
        float[] radiuses = new float[16];
        float[] noiseSpreadsArr = new float[16];

        for (int i = 0; i < arr.length;i+= 2){

            float lmx;
            float lmy;

            if (i / 2 < points.size()){
                Vector2f v = points.get(i / 2);
                lmx = (v.x - x) / sizeX;
                lmy = (v.y - y) / sizeY;

                radiuses[i / 2] = revealRadiuses.get(i / 2);
                noiseSpreadsArr[i / 2] = noiseSpreads.get(i / 2);


            }else{
                radiuses[i / 2] = 0.0001f;
                noiseSpreadsArr[i / 2] = 0.000001f;
                lmx = -100;
                lmy = -100;
            }
            arr[i] = lmx;
            arr[i + 1] = lmy;
        }



        Matrix4f mat = matrices.last().pose();



        RenderSystem.setShader(()-> RelicsCoreShaders.REVEAL_SHADER);
        RelicsCoreShaders.REVEAL_SHADER.safeGetUniform("revealRadiuses").set(radiuses);
        RelicsCoreShaders.REVEAL_SHADER.safeGetUniform("noiseSpreads").set(noiseSpreadsArr);
        RelicsCoreShaders.REVEAL_SHADER.safeGetUniform("positions").set(arr);
        RelicsCoreShaders.REVEAL_SHADER.safeGetUniform("pixelCount").set(110F);


        RelicsCoreShaders.REVEAL_SHADER.safeGetUniform("greenRadius").set(0.035f);
        RelicsCoreShaders.REVEAL_SHADER.safeGetUniform("size").set(sizeX,sizeY);
        RelicsCoreShaders.REVEAL_SHADER.safeGetUniform("time").set(time);
        RelicsCoreShaders.REVEAL_SHADER.safeGetUniform("col1").set(0.25F,1F,0f);
        RelicsCoreShaders.REVEAL_SHADER.safeGetUniform("col2").set(0.25F,1F,0f);


        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX);

        builder.addVertex(mat,x,y,0).setUv(0,0);
        builder.addVertex(mat,x + sizeX,y,0).setUv(1,0);
        builder.addVertex(mat,x + sizeX,y + sizeY,0).setUv(1,1);
        builder.addVertex(mat,x,y + sizeY,0).setUv(0,1);
        builder.addVertex(mat,x,y + sizeY,0).setUv(0,1);
        builder.addVertex(mat,x + sizeX,y + sizeY,0).setUv(1,1);
        builder.addVertex(mat,x + sizeX,y,0).setUv(1,0);
        builder.addVertex(mat,x,y,0).setUv(0,0);


        BufferUploader.drawWithShader(builder.buildOrThrow());

        RenderSystem.disableBlend();
    }



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
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

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

        builder.addVertex(m, -w2, +h2, 0).setUv(u1, v2);
        builder.addVertex(m, +w2, +h2, 0).setUv(u2, v2);
        builder.addVertex(m, +w2, -h2, 0).setUv(u2, v1);
        builder.addVertex(m, -w2, -h2, 0).setUv(u1, v1);

        matrix.popPose();

        BufferUploader.drawWithShader(builder.buildOrThrow());
    }
}