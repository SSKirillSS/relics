package it.hurts.sskirillss.relics.client.renderer.items.models;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class ArrowQuiverModel extends BipedModel<LivingEntity> {
    public ArrowQuiverModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        body = new ModelRenderer(this);

        ModelRenderer cube_1 = new ModelRenderer(this);
        cube_1.setPos(0.0F, 24.0F, 0.0F);
        body.addChild(cube_1);

        ModelRenderer cube_2 = new ModelRenderer(this);
        cube_2.setPos(0.0F, 0.0F, 0.0F);
        cube_1.addChild(cube_2);
        setRotationAngle(cube_2, 0.0F, 0.0F, -0.7854F);
        cube_2.texOffs(8, 9).addBox(10.65F, -18.0F, 1.5F, 4.0F, 2.0F, 4.0F, 0.0F, false);

        ModelRenderer cube_3 = new ModelRenderer(this);
        cube_3.setPos(-1.9F, -12.0F, 0.0F);
        cube_1.addChild(cube_3);
        setRotationAngle(cube_3, 0.0F, 0.0F, -0.7854F);
        cube_3.texOffs(0, 0).addBox(4.0F, -8.0F, 2.0F, 3.0F, 10.0F, 3.0F, 0.0F, false);

        ModelRenderer cube_4 = new ModelRenderer(this);
        cube_4.setPos(0.0F, 0.0F, 0.0F);
        cube_1.addChild(cube_4);

        ModelRenderer cube_5 = new ModelRenderer(this);
        cube_5.setPos(0.0F, 0.0F, 0.0F);
        cube_4.addChild(cube_5);
        setRotationAngle(cube_5, 0.0F, -0.7854F, -0.7854F);
        cube_5.texOffs(8, 15).addBox(11.0F, -19.25F, -7.501F, 2.0F, 1.0F, 0.0F, 0.0F, false);
        cube_5.texOffs(12, 3).addBox(12.001F, -19.25F, -8.5F, 0.0F, 1.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_6 = new ModelRenderer(this);
        cube_6.setPos(1.0F, -1.0F, 0.0F);
        cube_4.addChild(cube_6);
        setRotationAngle(cube_6, 0.0F, -0.7854F, -0.7854F);
        cube_6.texOffs(9, 2).addBox(10.5F, -18.25F, -6.5F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        cube_6.texOffs(9, 0).addBox(11.0F, -18.25F, -7.0F, 0.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_7 = new ModelRenderer(this);
        cube_7.setPos(0.0F, 0.0F, 0.0F);
        cube_1.addChild(cube_7);

        ModelRenderer cube_8 = new ModelRenderer(this);
        cube_8.setPos(1.0F, -1.0F, 0.0F);
        cube_7.addChild(cube_8);
        setRotationAngle(cube_8, 0.0F, 0.0F, -0.7854F);
        cube_8.texOffs(9, 0).addBox(11.0F, -19.0F, 4.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        cube_8.texOffs(0, 1).addBox(11.5F, -19.0F, 3.5F, 0.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_9 = new ModelRenderer(this);
        cube_9.setPos(0.0F, 0.0F, 0.0F);
        cube_7.addChild(cube_9);
        setRotationAngle(cube_9, 0.0F, 0.0F, -0.7854F);
        cube_9.texOffs(12, 8).addBox(11.9F, -19.8F, 3.999F, 2.0F, 1.0F, 0.0F, 0.0F, false);
        cube_9.texOffs(12, 2).addBox(12.915F, -19.8F, 3.0F, 0.0F, 1.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_10 = new ModelRenderer(this);
        cube_10.setPos(2.0F, 1.0F, 0.0F);
        cube_1.addChild(cube_10);

        ModelRenderer cube_11 = new ModelRenderer(this);
        cube_11.setPos(-4.0F, 0.0F, 0.0F);
        cube_10.addChild(cube_11);
        setRotationAngle(cube_11, 0.0F, -0.7854F, -0.7854F);
        cube_11.texOffs(14, 2).addBox(11.0F, -19.8F, -7.501F, 2.0F, 2.0F, 0.0F, 0.0F, false);
        cube_11.texOffs(2, 12).addBox(12.001F, -19.8F, -8.5F, 0.0F, 2.0F, 2.0F, 0.0F, false);

        ModelRenderer cube_12 = new ModelRenderer(this);
        cube_12.setPos(-2.0F, -1.0F, 0.0F);
        cube_10.addChild(cube_12);
        setRotationAngle(cube_12, 0.0F, -0.7854F, -0.7854F);
        cube_12.texOffs(0, 0).addBox(10.0F, -18.75F, -6.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        cube_12.texOffs(0, 0).addBox(10.5F, -18.75F, -6.5F, 0.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_13 = new ModelRenderer(this);
        cube_13.setPos(0.0F, 0.0F, 0.0F);
        cube_1.addChild(cube_13);

        ModelRenderer cube_14 = new ModelRenderer(this);
        cube_14.setPos(0.0F, 0.0F, 0.0F);
        cube_1.addChild(cube_14);

        ModelRenderer cube_15 = new ModelRenderer(this);
        cube_15.setPos(0.0F, 0.0F, 0.0F);
        cube_1.addChild(cube_15);

        ModelRenderer cube_16 = new ModelRenderer(this);
        cube_16.setPos(0.0F, 0.0F, 0.0F);
        cube_1.addChild(cube_16);

        ModelRenderer cube_17 = new ModelRenderer(this);
        cube_17.setPos(0.0F, 24.0F, 0.0F);
        body.addChild(cube_17);
        cube_17.texOffs(12, 15).addBox(-3.0F, -24.0F, 2.1F, 1.0F, 2.0F, 0.0F, 0.0F, false);
        cube_17.texOffs(12, 6).addBox(-3.0F, -24.05F, 1.05F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        cube_17.texOffs(8, 0).addBox(-3.0F, -24.1F, -2.0F, 1.0F, 0.0F, 4.0F, 0.0F, false);
        cube_17.texOffs(15, 5).addBox(-3.0F, -24.05F, -2.05F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        cube_17.texOffs(14, 15).addBox(-3.0F, -24.0F, -2.1F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        cube_17.texOffs(5, 15).addBox(3.05F, -16.0F, -2.05F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        cube_17.texOffs(6, 14).addBox(3.0F, -16.0F, -2.1F, 1.0F, 1.0F, 0.0F, 0.0F, false);
        cube_17.texOffs(0, 9).addBox(4.1F, -16.0F, -2.0F, 0.0F, 1.0F, 4.0F, 0.0F, false);
        cube_17.texOffs(14, 0).addBox(3.05F, -16.0F, 1.05F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_18 = new ModelRenderer(this);
        cube_18.setPos(0.0F, 0.0F, 0.0F);
        cube_17.addChild(cube_18);
        setRotationAngle(cube_18, 0.0F, 0.0F, -0.6545F);
        cube_18.texOffs(0, 14).addBox(11.8F, -20.32F, -2.075F, 1.0F, 10.0F, 0.0F, 0.0F, false);

        ModelRenderer cube_19 = new ModelRenderer(this);
        cube_19.setPos(0.0F, 24.0F, 0.0F);
        cube_19.texOffs(19, 2).addBox(-2.0F, -22.0F, 9.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}