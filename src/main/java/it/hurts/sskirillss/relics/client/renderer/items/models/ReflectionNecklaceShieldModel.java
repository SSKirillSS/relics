package it.hurts.sskirillss.relics.client.renderer.items.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ReflectionNecklaceShieldModel extends Model {
    private final ModelRenderer bone;
    private final ModelRenderer bone2;
    private final ModelRenderer bone3;

    public ReflectionNecklaceShieldModel() {
        super(RenderType::entityTranslucent);
        texWidth = 128;
        texHeight = 128;

        bone = new ModelRenderer(this);
        bone.setPos(0.0F, 25.0F, 0.0F);
        bone.texOffs(16, 66).addBox(-18.392F, -48.496F, -0.001F, 4.0F, 24.0F, 4.0F, 0.0F, false);
        bone.texOffs(0, 66).addBox(14.392F, -48.496F, -0.001F, 4.0F, 24.0F, 4.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 0.0F, 0.0F);
        bone.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, 1.0472F);
        cube_r1.texOffs(76, 74).addBox(-51.2F, -28.32F, 0.0F, 4.0F, 20.0F, 4.0F, 0.0F, false);
        cube_r1.texOffs(82, 28).addBox(-51.21F, -29.56F, 0.001F, 4.0F, 4.0F, 4.0F, 0.0F, false);
        cube_r1.texOffs(80, 38).addBox(-8.0F, -14.312F, -0.001F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, 0.0F, 0.0F);
        bone.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, -1.0472F);
        cube_r2.texOffs(76, 20).addBox(47.21F, -29.56F, 0.001F, 4.0F, 4.0F, 4.0F, 0.0F, false);
        cube_r2.texOffs(80, 54).addBox(4.0F, -14.312F, -0.001F, 4.0F, 12.0F, 4.0F, 0.0F, false);
        cube_r2.texOffs(32, 66).addBox(47.2F, -28.32F, 0.0F, 4.0F, 20.0F, 4.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(0.0F, 0.0F, 0.0F);
        bone.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.0F, -0.5236F);
        cube_r3.texOffs(48, 78).addBox(-3.68F, -30.4F, 0.0F, 4.0F, 16.0F, 4.0F, 0.0F, false);

        ModelRenderer cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(0.0F, 0.0F, 0.0F);
        bone.addChild(cube_r4);
        setRotationAngle(cube_r4, 0.0F, 0.0F, 0.5236F);
        cube_r4.texOffs(80, 0).addBox(-0.32F, -30.4F, 0.0F, 4.0F, 16.0F, 4.0F, 0.0F, false);

        bone2 = new ModelRenderer(this);
        bone2.setPos(0.0F, 8.0F, 7.0F);
        bone2.texOffs(64, 78).addBox(7.196F, -24.248F, -0.381F, 2.0F, 12.0F, 2.0F, 0.0F, false);
        bone2.texOffs(32, 90).addBox(-9.196F, -24.248F, -0.381F, 2.0F, 12.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r5 = new ModelRenderer(this);
        cube_r5.setPos(0.0F, 0.0F, 0.0F);
        bone2.addChild(cube_r5);
        setRotationAngle(cube_r5, 0.0F, 0.0F, -0.5236F);
        cube_r5.texOffs(92, 70).addBox(-1.84F, -15.2F, -0.38F, 2.0F, 8.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r6 = new ModelRenderer(this);
        cube_r6.setPos(0.0F, 0.0F, 0.0F);
        bone2.addChild(cube_r6);
        setRotationAngle(cube_r6, 0.0F, 0.0F, -1.0472F);
        cube_r6.texOffs(92, 80).addBox(2.0F, -7.156F, -0.381F, 2.0F, 6.0F, 2.0F, 0.0F, false);
        cube_r6.texOffs(34, 14).addBox(23.6F, -14.16F, -0.38F, 2.0F, 10.0F, 2.0F, 0.0F, false);
        cube_r6.texOffs(28, 66).addBox(23.61F, -14.78F, -0.379F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r7 = new ModelRenderer(this);
        cube_r7.setPos(0.0F, 0.0F, 0.0F);
        bone2.addChild(cube_r7);
        setRotationAngle(cube_r7, 0.0F, 0.0F, 1.0472F);
        cube_r7.texOffs(92, 20).addBox(-4.0F, -7.156F, -0.381F, 2.0F, 6.0F, 2.0F, 0.0F, false);
        cube_r7.texOffs(40, 90).addBox(-25.6F, -14.16F, -0.38F, 2.0F, 10.0F, 2.0F, 0.0F, false);
        cube_r7.texOffs(12, 66).addBox(-25.61F, -14.78F, -0.379F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r8 = new ModelRenderer(this);
        cube_r8.setPos(0.0F, 0.0F, 0.0F);
        bone2.addChild(cube_r8);
        setRotationAngle(cube_r8, 0.0F, 0.0F, 0.5236F);
        cube_r8.texOffs(64, 92).addBox(-0.16F, -15.2F, -0.38F, 2.0F, 8.0F, 2.0F, 0.0F, false);

        bone3 = new ModelRenderer(this);
        bone3.setPos(0.0F, 24.0F, 0.0F);
        bone3.texOffs(0, 0).addBox(-7.5F, -44.0F, 6.61F, 15.0F, 24.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r9 = new ModelRenderer(this);
        cube_r9.setPos(0.0F, 0.0F, 0.0F);
        bone3.addChild(cube_r9);
        setRotationAngle(cube_r9, 0.0F, 0.3491F, 1.0472F);
        cube_r9.texOffs(44, 14).addBox(-18.3F, -14.0F, 0.45F, 14.0F, 12.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r10 = new ModelRenderer(this);
        cube_r10.setPos(0.0F, 0.0F, 0.0F);
        bone3.addChild(cube_r10);
        setRotationAngle(cube_r10, 0.0F, -0.3491F, -1.0472F);
        cube_r10.texOffs(50, 28).addBox(4.3F, -14.0F, 0.45F, 14.0F, 12.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r11 = new ModelRenderer(this);
        cube_r11.setPos(0.0F, 0.0F, 0.0F);
        bone3.addChild(cube_r11);
        setRotationAngle(cube_r11, 0.0F, 0.3927F, 0.5236F);
        cube_r11.texOffs(52, 42).addBox(-9.8F, -29.4F, 3.19F, 12.0F, 16.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r12 = new ModelRenderer(this);
        cube_r12.setPos(0.0F, 0.0F, 0.0F);
        bone3.addChild(cube_r12);
        setRotationAngle(cube_r12, 0.0F, -0.3927F, -0.5236F);
        cube_r12.texOffs(52, 60).addBox(-2.2F, -29.4F, 3.19F, 12.0F, 16.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r13 = new ModelRenderer(this);
        cube_r13.setPos(0.0F, 0.0F, 0.0F);
        bone3.addChild(cube_r13);
        setRotationAngle(cube_r13, 0.0F, 0.4363F, 0.0F);
        cube_r13.texOffs(0, 40).addBox(3.99F, -47.496F, 9.375F, 11.0F, 24.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r14 = new ModelRenderer(this);
        cube_r14.setPos(0.0F, 0.0F, 0.0F);
        bone3.addChild(cube_r14);
        setRotationAngle(cube_r14, 0.0F, -0.4363F, 0.0F);
        cube_r14.texOffs(26, 40).addBox(-14.99F, -47.496F, 9.375F, 11.0F, 24.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r15 = new ModelRenderer(this);
        cube_r15.setPos(0.0F, 0.0F, 0.0F);
        bone3.addChild(cube_r15);
        setRotationAngle(cube_r15, 0.3927F, 0.0F, 0.5236F);
        cube_r15.texOffs(0, 26).addBox(-29.0F, -45.0F, 20.88F, 21.0F, 12.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_r16 = new ModelRenderer(this);
        cube_r16.setPos(0.0F, 0.0F, 0.0F);
        bone3.addChild(cube_r16);
        setRotationAngle(cube_r16, 0.3927F, 0.0F, -0.5236F);
        cube_r16.texOffs(34, 0).addBox(8.0F, -45.0F, 20.88F, 21.0F, 12.0F, 2.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(matrixStack, buffer, packedLight, packedOverlay);
        bone2.render(matrixStack, buffer, packedLight, packedOverlay);
        bone3.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}