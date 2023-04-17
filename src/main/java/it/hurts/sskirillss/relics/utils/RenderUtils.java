package it.hurts.sskirillss.relics.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

import java.awt.*;
import java.util.Random;

public class RenderUtils {
    public static void renderTextureFromCenter(PoseStack matrices, float centerX, float centerY, float width, float height, float scale) {
        BufferBuilder builder = Tesselator.getInstance().getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        matrices.pushPose();

        matrices.translate(centerX, centerY, 0);
        matrices.scale(scale, scale, scale);

        Matrix4f m = matrices.last().pose();

        float w2 = width / 2;
        float h2 = height / 2;

        builder.vertex(m, -w2, +h2, 0).uv(0, 1).endVertex();
        builder.vertex(m, +w2, +h2, 0).uv(1, 1).endVertex();
        builder.vertex(m, +w2, -h2, 0).uv(1, 0).endVertex();
        builder.vertex(m, -w2, -h2, 0).uv(0, 0).endVertex();

        matrices.popPose();

        BufferUploader.drawWithShader(builder.end());
    }

    public static void renderBeams(PoseStack matrixStack, MultiBufferSource bufferIn, float partialTicks, int amount, float size, Color color) {
        matrixStack.pushPose();

        Random random = new Random(1488);

        for (int i = 1; i < amount; ++i) {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0F + partialTicks));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0F + partialTicks));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0F + partialTicks));

            renderBeam(matrixStack, bufferIn, partialTicks, size, color);
        }

        matrixStack.popPose();
    }

    public static void renderBeam(PoseStack matrixStack, MultiBufferSource bufferIn, float partialTicks, float size, Color color) {
        VertexConsumer builder = bufferIn.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = matrixStack.last().pose();

        float length = size * 0.2F;

        int red = Mth.clamp(color.getRed(), 0, 255);
        int green = Mth.clamp(color.getGreen(), 0, 255);
        int blue = Mth.clamp(color.getBlue(), 0, 255);
        int alpha = (int) (255.0F * (1.0F - partialTicks / 200.0F));

        builder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
        builder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
        builder.vertex(matrix4f, -(float) (Math.sqrt(3.0D) / 2.0D) * length, size, -0.5F * length).color(red, green, blue, 0).endVertex();
        builder.vertex(matrix4f, (float) (Math.sqrt(3.0D) / 2.0D) * length, size, -0.5F * length).color(red, green, blue, 0).endVertex();
        builder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
        builder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
        builder.vertex(matrix4f, (float) (Math.sqrt(3.0D) / 2.0D) * length, size, -0.5F * length).color(red, green, blue, 0).endVertex();
        builder.vertex(matrix4f, 0.0F, size, length).color(red, green, blue, 0).endVertex();
        builder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
        builder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
        builder.vertex(matrix4f, 0.0F, size, length).color(red, green, blue, 0).endVertex();
        builder.vertex(matrix4f, -(float) (Math.sqrt(3.0D) / 2.0D) * length, size, -0.5F * length).color(red, green, blue, 0).endVertex();
    }
}