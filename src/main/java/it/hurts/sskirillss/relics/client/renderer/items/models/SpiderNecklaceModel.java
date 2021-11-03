package it.hurts.sskirillss.relics.client.renderer.items.models;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class SpiderNecklaceModel extends BipedModel<LivingEntity> {
    public SpiderNecklaceModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        body = new ModelRenderer(this);

        ModelRenderer main = new ModelRenderer(this);
        main.setPos(0.0F, 24.0F, 0.0F);
        body.addChild(main);
        main.texOffs(0, 0).addBox(-4.0F, -23.15F, -2.15F, 8.0F, 5.0F, 4.0F, 0.0F, false);
        main.texOffs(0, 9).addBox(-1.5F, -21.0F, -5.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        main.texOffs(9, 9).addBox(-1.5F, -18.5F, -5.1F, 3.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, -24.0F, 0.0F);
        main.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.2618F, 0.4363F);
        cube_r1.texOffs(15, 16).addBox(4.0F, 4.0F, -3.75F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, -24.0F, 0.0F);
        main.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, -0.2618F, -0.4363F);
        cube_r2.texOffs(0, 17).addBox(-6.0F, 4.0F, -3.75F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(0.0F, -24.0F, 0.0F);
        main.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.0F, 0.5236F);
        cube_r3.texOffs(12, 11).addBox(3.0F, 3.5F, -4.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(0.0F, -24.0F, 0.0F);
        main.addChild(cube_r4);
        setRotationAngle(cube_r4, 0.0F, 0.0F, -0.5236F);
        cube_r4.texOffs(12, 13).addBox(-6.0F, 3.5F, -4.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r5 = new ModelRenderer(this);
        cube_r5.setPos(0.0F, -24.0F, 0.0F);
        main.addChild(cube_r5);
        setRotationAngle(cube_r5, 0.0F, -0.2618F, 0.4363F);
        cube_r5.texOffs(0, 15).addBox(2.0F, 4.0F, -4.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r6 = new ModelRenderer(this);
        cube_r6.setPos(0.0F, -24.0F, 0.0F);
        main.addChild(cube_r6);
        setRotationAngle(cube_r6, 0.0F, 0.2618F, -0.4363F);
        cube_r6.texOffs(8, 15).addBox(-5.0F, 4.0F, -4.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}