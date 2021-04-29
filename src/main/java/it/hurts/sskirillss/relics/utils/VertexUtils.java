package it.hurts.sskirillss.relics.utils;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;

public class VertexUtils {
    public static void addVertexPoint(IVertexBuilder builder, Matrix4f matrix4f, Matrix3f matrix3f, int light, float x, int y, int u, int v) {
        builder.vertex(matrix4f, x - 0.5F, (float) y - 0.25F, 0.0F)
                .color(255, 255, 255, 255)
                .uv((float) u, (float) v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}