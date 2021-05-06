package it.hurts.sskirillss.relics.items.relics.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ArrowQuiverModel extends Model {
    private final ModelRenderer quiver;
    private final ModelRenderer sling;
    private final ModelRenderer arrow1;
    private final ModelRenderer arrow2;
    private final ModelRenderer arrow3;
    private final ModelRenderer line1;
    private final ModelRenderer line2;
    private final ModelRenderer line3;
    private final ModelRenderer line4;

    public ArrowQuiverModel() {
        super(RenderType::entityTranslucent);
        texWidth = 32;
        texHeight = 32;

        quiver = new ModelRenderer(this);
        quiver.setPos(0.0F, 24.0F, 0.0F);


        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 0.0F, 0.0F);
        quiver.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, -0.7854F);
        cube_r1.texOffs(9, 10).addBox(10.65F, -18.0F, 1.999F, 4.0F, 2.0F, 3.0F, 0.0F, true);
        cube_r1.texOffs(14, 1).addBox(10.65F, -18.0F, 5.001F, 4.0F, 2.0F, 0.0F, 0.0F, true);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(-1.9F, -12.0F, 0.0F);
        quiver.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, -0.7854F);
        cube_r2.texOffs(0, 0).addBox(4.0F, -8.0F, 2.0F, 3.0F, 10.0F, 3.0F, 0.0F, true);

        sling = new ModelRenderer(this);
        sling.setPos(0.0F, 24.0F, 0.0F);
        sling.texOffs(22, 9).addBox(-3.0F, -24.0F, -2.1F, 1.0F, 1.0F, 0.0F, 0.0F, true);
        sling.texOffs(20, 10).addBox(-4.05F, -15.28F, 1.05F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        sling.texOffs(12, 5).addBox(-4.0F, -15.25F, 2.1F, 6.0F, 1.0F, 0.0F, 0.0F, true);
        sling.texOffs(9, 0).addBox(-4.0F, -16.0F, -2.1F, 8.0F, 1.0F, 0.0F, 0.0F, true);
        sling.texOffs(8, 18).addBox(-4.1F, -15.28F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, true);
        sling.texOffs(0, 1).addBox(-4.1F, -16.0F, -2.0F, 0.0F, 1.0F, 1.0F, 0.0F, true);
        sling.texOffs(13, 22).addBox(3.0F, -17.1F, 2.1F, 1.0F, 1.0F, 0.0F, 0.0F, true);
        sling.texOffs(9, 1).addBox(4.1F, -16.03F, -2.0F, 0.0F, 1.0F, 1.0F, 0.0F, true);
        sling.texOffs(9, 0).addBox(4.1F, -16.994F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, true);
        sling.texOffs(20, 12).addBox(-1.0F, -16.0F, -2.2F, 2.0F, 1.0F, 0.0F, 0.0F, true);
        sling.texOffs(21, 7).addBox(-3.0F, -24.05F, 1.05F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        sling.texOffs(0, 0).addBox(-3.0F, -24.0F, 2.1F, 1.0F, 2.0F, 0.0F, 0.0F, true);
        sling.texOffs(8, 1).addBox(-3.0F, -24.1F, -2.0F, 1.0F, 0.0F, 4.0F, 0.0F, true);
        sling.texOffs(5, 21).addBox(-3.0F, -24.05F, -2.05F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        sling.texOffs(21, 3).addBox(3.05F, -16.0F, -2.05F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        sling.texOffs(20, 19).addBox(3.05F, -17.0F, 1.05F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        sling.texOffs(16, 20).addBox(-4.05F, -16.0F, -2.05F, 1.0F, 1.0F, 1.0F, 0.0F, true);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(0.0F, 0.0F, 0.0F);
        sling.addChild(cube_r3);
        setRotationAngle(cube_r3, -0.2618F, 0.0F, 0.0F);
        cube_r3.texOffs(12, 6).addBox(-4.0999F, -15.1F, -5.5F, 0.0F, 1.0F, 3.0F, 0.0F, true);

        ModelRenderer cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(0.0F, 0.0F, 0.0F);
        sling.addChild(cube_r4);
        setRotationAngle(cube_r4, 0.0F, 0.0F, -0.6109F);
        cube_r4.texOffs(0, 13).addBox(11.0F, -20.65F, -2.075F, 1.0F, 10.0F, 0.0F, 0.0F, true);

        ModelRenderer cube_r5 = new ModelRenderer(this);
        cube_r5.setPos(0.0F, 0.0F, 0.0F);
        sling.addChild(cube_r5);
        setRotationAngle(cube_r5, 0.3491F, 0.0F, 0.0F);
        cube_r5.texOffs(8, 12).addBox(4.09F, -15.55F, 4.0F, 0.0F, 1.0F, 3.0F, 0.0F, true);

        arrow1 = new ModelRenderer(this);
        arrow1.setPos(-1.0F, 23.0F, 0.0F);


        ModelRenderer cube_r6 = new ModelRenderer(this);
        cube_r6.setPos(1.0F, 1.0F, 0.0F);
        arrow1.addChild(cube_r6);
        setRotationAngle(cube_r6, 0.0F, -0.7854F, -0.7854F);
        cube_r6.texOffs(21, 6).addBox(11.0F, -19.25F, -7.501F, 2.0F, 1.0F, 0.0F, 0.0F, true);
        cube_r6.texOffs(10, 16).addBox(12.001F, -19.25F, -8.5F, 0.0F, 1.0F, 2.0F, 0.0F, true);
        cube_r6.texOffs(14, 16).addBox(11.999F, -19.25F, -8.5F, 0.0F, 1.0F, 2.0F, 0.0F, true);
        cube_r6.texOffs(20, 18).addBox(11.0F, -19.25F, -7.499F, 2.0F, 1.0F, 0.0F, 0.0F, true);

        ModelRenderer cube_r7 = new ModelRenderer(this);
        cube_r7.setPos(2.0F, 0.0F, 0.0F);
        arrow1.addChild(cube_r7);
        setRotationAngle(cube_r7, 0.0F, -0.7854F, -0.7854F);
        cube_r7.texOffs(12, 18).addBox(11.0F, -18.25F, -7.0F, 0.0F, 1.0F, 1.0F, 0.0F, true);
        cube_r7.texOffs(15, 22).addBox(10.5F, -18.25F, -6.5F, 1.0F, 1.0F, 0.0F, 0.0F, true);

        arrow2 = new ModelRenderer(this);
        arrow2.setPos(0.0F, 24.0F, 0.0F);


        ModelRenderer bone4 = new ModelRenderer(this);
        bone4.setPos(0.0F, 0.0F, 0.0F);
        arrow2.addChild(bone4);


        ModelRenderer cube_r8 = new ModelRenderer(this);
        cube_r8.setPos(0.0F, 0.0F, 0.0F);
        bone4.addChild(cube_r8);
        setRotationAngle(cube_r8, 0.0F, 0.0F, -0.7854F);
        cube_r8.texOffs(21, 15).addBox(11.9F, -19.8F, 3.999F, 2.0F, 1.0F, 0.0F, 0.0F, true);
        cube_r8.texOffs(22, 1).addBox(11.9F, -20.8F, 4.001F, 2.0F, 2.0F, 0.0F, 0.0F, true);
        cube_r8.texOffs(2, 17).addBox(12.915F, -19.8F, 3.0F, 0.0F, 1.0F, 2.0F, 0.0F, true);
        cube_r8.texOffs(6, 15).addBox(12.914F, -20.8F, 3.0F, 0.0F, 2.0F, 2.0F, 0.0F, true);

        ModelRenderer bone2 = new ModelRenderer(this);
        bone2.setPos(-1.0F, -1.0F, 0.0F);
        arrow2.addChild(bone2);


        ModelRenderer cube_r9 = new ModelRenderer(this);
        cube_r9.setPos(2.0F, 0.0F, 0.0F);
        bone2.addChild(cube_r9);
        setRotationAngle(cube_r9, 0.0F, 0.0F, -0.7854F);
        cube_r9.texOffs(17, 22).addBox(11.0F, -19.0F, 4.0F, 1.0F, 1.0F, 0.0F, 0.0F, true);
        cube_r9.texOffs(2, 21).addBox(11.5F, -19.0F, 3.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);

        arrow3 = new ModelRenderer(this);
        arrow3.setPos(0.0F, 24.0F, 0.0F);


        ModelRenderer cube_r10 = new ModelRenderer(this);
        cube_r10.setPos(-2.0F, 1.0F, 0.0F);
        arrow3.addChild(cube_r10);
        setRotationAngle(cube_r10, 0.0F, -0.7854F, -0.7854F);
        cube_r10.texOffs(9, 21).addBox(11.0F, -19.8F, -7.501F, 2.0F, 2.0F, 0.0F, 0.0F, true);
        cube_r10.texOffs(14, 13).addBox(12.001F, -19.8F, -8.5F, 0.0F, 2.0F, 2.0F, 0.0F, true);
        cube_r10.texOffs(2, 15).addBox(11.999F, -19.8F, -8.5F, 0.0F, 2.0F, 2.0F, 0.0F, true);
        cube_r10.texOffs(20, 21).addBox(11.0F, -19.8F, -7.499F, 2.0F, 2.0F, 0.0F, 0.0F, true);

        ModelRenderer cube_r11 = new ModelRenderer(this);
        cube_r11.setPos(0.0F, 0.0F, 0.0F);
        arrow3.addChild(cube_r11);
        setRotationAngle(cube_r11, 0.0F, -0.7854F, -0.7854F);
        cube_r11.texOffs(13, 20).addBox(10.5F, -18.75F, -6.5F, 0.0F, 1.0F, 1.0F, 0.0F, true);
        cube_r11.texOffs(22, 16).addBox(10.0F, -18.75F, -6.0F, 1.0F, 1.0F, 0.0F, 0.0F, true);

        line1 = new ModelRenderer(this);
        line1.setPos(0.0F, 24.0F, 0.0F);


        ModelRenderer cube_r12 = new ModelRenderer(this);
        cube_r12.setPos(0.0F, 0.0F, 0.0F);
        line1.addChild(cube_r12);
        setRotationAngle(cube_r12, 0.0F, 0.0F, -0.7854F);
        cube_r12.texOffs(18, 6).addBox(10.6F, -17.5F, 4.05F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        cube_r12.texOffs(18, 8).addBox(13.7F, -17.5F, 4.05F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        cube_r12.texOffs(14, 3).addBox(10.65F, -17.5F, 5.1F, 4.0F, 1.0F, 0.0F, 0.0F, true);
        cube_r12.texOffs(2, 10).addBox(14.8F, -17.5F, 2.0F, 0.0F, 1.0F, 3.0F, 0.0F, true);
        cube_r12.texOffs(2, 11).addBox(10.5F, -17.5F, 2.0F, 0.0F, 1.0F, 3.0F, 0.0F, true);

        line2 = new ModelRenderer(this);
        line2.setPos(0.0F, 24.0F, 0.0F);


        ModelRenderer cube_r13 = new ModelRenderer(this);
        cube_r13.setPos(0.0F, 0.0F, 0.0F);
        line2.addChild(cube_r13);
        setRotationAngle(cube_r13, 0.0F, 0.0F, -0.7854F);
        cube_r13.texOffs(18, 15).addBox(11.1F, -16.0F, 4.1F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        cube_r13.texOffs(14, 4).addBox(11.15F, -16.0F, 5.2F, 3.0F, 1.0F, 0.0F, 0.0F, true);
        cube_r13.texOffs(17, 18).addBox(13.2F, -16.03F, 4.1F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        cube_r13.texOffs(2, 12).addBox(11.0F, -16.0F, 2.0F, 0.0F, 1.0F, 3.0F, 0.0F, true);

        ModelRenderer cube_r14 = new ModelRenderer(this);
        cube_r14.setPos(-1.9F, -12.0F, 0.0F);
        line2.addChild(cube_r14);
        setRotationAngle(cube_r14, 0.0F, 0.0F, -0.7854F);
        cube_r14.texOffs(12, 3).addBox(7.1F, -6.2F, 2.0F, 0.0F, 1.0F, 3.0F, 0.0F, true);

        line3 = new ModelRenderer(this);
        line3.setPos(0.0F, 24.0F, 0.0F);


        ModelRenderer cube_r15 = new ModelRenderer(this);
        cube_r15.setPos(0.0F, 0.0F, 0.0F);
        line3.addChild(cube_r15);
        setRotationAngle(cube_r15, 0.0F, 0.0F, -0.7854F);
        cube_r15.texOffs(12, 4).addBox(11.0F, -13.0F, 2.0F, 0.0F, 1.0F, 3.0F, 0.0F, true);
        cube_r15.texOffs(5, 19).addBox(11.1F, -13.0F, 4.1F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        cube_r15.texOffs(10, 17).addBox(11.15F, -13.0F, 5.2F, 3.0F, 1.0F, 0.0F, 0.0F, true);
        cube_r15.texOffs(9, 19).addBox(13.2F, -13.03F, 4.1F, 1.0F, 1.0F, 1.0F, 0.0F, true);

        ModelRenderer cube_r16 = new ModelRenderer(this);
        cube_r16.setPos(-1.9F, -12.0F, 0.0F);
        line3.addChild(cube_r16);
        setRotationAngle(cube_r16, 0.0F, 0.0F, -0.7854F);
        cube_r16.texOffs(12, 5).addBox(7.1F, -3.2F, 2.0F, 0.0F, 1.0F, 3.0F, 0.0F, true);

        line4 = new ModelRenderer(this);
        line4.setPos(0.0F, 24.0F, 0.0F);


        ModelRenderer cube_r17 = new ModelRenderer(this);
        cube_r17.setPos(0.0F, 0.0F, 0.0F);
        line4.addChild(cube_r17);
        setRotationAngle(cube_r17, 0.0F, 0.0F, -0.7854F);
        cube_r17.texOffs(2, 20).addBox(11.1F, -10.0F, 4.1F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        cube_r17.texOffs(13, 19).addBox(13.2F, -10.037F, 4.1F, 1.0F, 1.0F, 1.0F, 0.0F, true);
        cube_r17.texOffs(16, 17).addBox(11.15F, -10.0F, 5.2F, 3.0F, 1.0F, 0.0F, 0.0F, true);
        cube_r17.texOffs(8, 13).addBox(11.0F, -10.0F, 2.0F, 0.0F, 1.0F, 3.0F, 0.0F, true);

        ModelRenderer cube_r18 = new ModelRenderer(this);
        cube_r18.setPos(-1.9F, -12.0F, 0.0F);
        line4.addChild(cube_r18);
        setRotationAngle(cube_r18, 0.0F, 0.0F, -0.7854F);
        cube_r18.texOffs(2, 13).addBox(7.1F, -0.2F, 2.0F, 0.0F, 1.0F, 3.0F, 0.0F, true);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        quiver.render(matrixStack, buffer, packedLight, packedOverlay);
        sling.render(matrixStack, buffer, packedLight, packedOverlay);
        arrow1.render(matrixStack, buffer, packedLight, packedOverlay);
        arrow2.render(matrixStack, buffer, packedLight, packedOverlay);
        arrow3.render(matrixStack, buffer, packedLight, packedOverlay);
        line1.render(matrixStack, buffer, packedLight, packedOverlay);
        line2.render(matrixStack, buffer, packedLight, packedOverlay);
        line3.render(matrixStack, buffer, packedLight, packedOverlay);
        line4.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}