package it.hurts.sskirillss.relics.items.relics.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SporeSackModel extends Model {
    private final ModelRenderer model;

    public SporeSackModel() {
        super(RenderType::entityTranslucent);
        texWidth = 32;
        texHeight = 32;

        model = new ModelRenderer(this);
        model.setPos(0.0F, 24.0F, 0.0F);
        model.texOffs(0, 0).addBox(3.9F, -12.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);
        model.texOffs(4, 5).addBox(4.0F, -13.5F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        model.texOffs(3, 12).addBox(4.799F, -11.9999F, -0.11F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        model.texOffs(8, 11).addBox(4.0F, -11.9999F, 0.4999F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        model.texOffs(9, 8).addBox(4.7999F, -11.9999F, -0.89F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        model.texOffs(0, 2).addBox(4.0001F, -14.5F, -1.5F, 0.0F, 1.0F, 3.0F, 0.0F, false);
        model.texOffs(5, 0).addBox(3.9002F, -10.423F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        model.texOffs(0, 4).addBox(5.9F, -11.5F, -1.0F, 0.0F, 1.0F, 2.0F, 0.0F, false);
        model.texOffs(0, 1).addBox(4.11F, -11.5F, 1.61F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        model.texOffs(0, 0).addBox(4.11F, -11.5F, -1.61F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        model.texOffs(12, 3).addBox(4.8F, -10.9998F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        model.texOffs(11, 6).addBox(4.8F, -12.0002F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        model.texOffs(5, 9).addBox(4.0F, -11.9999F, -1.4999F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, -0.6545F, 0.0F);
        cube_r1.texOffs(0, 2).addBox(3.07F, -11.5F, -4.38F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        cube_r1.texOffs(0, 10).addBox(3.055F, -12.0F, -4.235F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.6545F, 0.0F);
        cube_r2.texOffs(5, 0).addBox(3.07F, -11.5F, 4.38F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        cube_r2.texOffs(11, 0).addBox(3.055F, -12.0F, 3.235F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r3);
        setRotationAngle(cube_r3, 1.0472F, 0.0F, 0.0F);
        cube_r3.texOffs(12, 10).addBox(3.9001F, -4.846F, 8.658F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        cube_r3.texOffs(13, 8).addBox(3.999F, -7.2F, 9.6F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r4);
        setRotationAngle(cube_r4, -1.0472F, 0.0F, 0.0F);
        cube_r4.texOffs(12, 12).addBox(3.9001F, -4.846F, -9.658F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        cube_r4.texOffs(0, 14).addBox(3.999F, -7.185F, -10.6F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r5 = new ModelRenderer(this);
        cube_r5.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r5);
        setRotationAngle(cube_r5, 0.0F, 0.0F, -0.7854F);
        cube_r5.texOffs(0, 7).addBox(10.12F, -4.2F, -1.0001F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r6 = new ModelRenderer(this);
        cube_r6.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r6);
        setRotationAngle(cube_r6, 0.5236F, -0.7854F, 0.0F);
        cube_r6.texOffs(5, 1).addBox(2.3F, -14.8F, 3.15F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        ModelRenderer cube_r7 = new ModelRenderer(this);
        cube_r7.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r7);
        setRotationAngle(cube_r7, -0.5236F, 0.7854F, 0.0F);
        cube_r7.texOffs(4, 6).addBox(2.35F, -14.8F, -3.15F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        ModelRenderer cube_r8 = new ModelRenderer(this);
        cube_r8.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r8);
        setRotationAngle(cube_r8, 0.5236F, 0.0F, 0.0F);
        cube_r8.texOffs(0, 7).addBox(4.0F, -13.16F, 5.9F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        ModelRenderer cube_r9 = new ModelRenderer(this);
        cube_r9.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r9);
        setRotationAngle(cube_r9, -0.5236F, 0.0F, 0.0F);
        cube_r9.texOffs(0, 8).addBox(4.0F, -13.16F, -5.88F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        ModelRenderer cube_r10 = new ModelRenderer(this);
        cube_r10.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r10);
        setRotationAngle(cube_r10, 0.0F, 0.0F, 0.5236F);
        cube_r10.texOffs(8, 4).addBox(-2.41F, -15.16F, -1.0F, 0.0F, 1.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r11 = new ModelRenderer(this);
        cube_r11.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r11);
        setRotationAngle(cube_r11, 0.0F, 0.0F, 0.7854F);
        cube_r11.texOffs(8, 3).addBox(-5.5F, -12.45F, -1.0001F, 1.0F, 1.0F, 2.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        model.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}