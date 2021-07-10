package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class FragrantFlowerModel extends BipedModel<LivingEntity> {
    public FragrantFlowerModel() {
        super(1.0F, 0, 16, 16);

        setAllVisible(false);

        head = new ModelRenderer(this);

        ModelRenderer center = new ModelRenderer(this);
        center.setPos(-1.0F, 25.0F, 0.0F);
        head.addChild(center);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.3491F, 0.0F, -0.6545F);
        cube_r1.texOffs(4, 4).addBox(7.5F, -7.5F, -2.7F, 2.0F, 2.0F, 0.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.2618F, -0.5672F, 0.0873F);
        cube_r2.texOffs(4, 2).addBox(-2.2F, -11.25F, -2.7F, 2.0F, 2.0F, 0.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.0F, -0.7854F);
        cube_r3.texOffs(4, 0).addBox(7.6F, -3.5F, -5.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(cube_r4);
        setRotationAngle(cube_r4, 0.2182F, 0.5672F, 0.7418F);
        cube_r4.texOffs(0, 4).addBox(-0.25F, -11.25F, -3.0F, 2.0F, 2.0F, 0.0F, 0.0F, false);

        ModelRenderer cube_r5 = new ModelRenderer(this);
        cube_r5.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(cube_r5);
        setRotationAngle(cube_r5, 0.2182F, -0.5672F, -0.7418F);
        cube_r5.texOffs(0, 2).addBox(2.0F, -6.5F, -7.0F, 2.0F, 2.0F, 0.0F, 0.0F, false);

        ModelRenderer cube_r6 = new ModelRenderer(this);
        cube_r6.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(cube_r6);
        setRotationAngle(cube_r6, -0.3927F, 0.0F, -0.1745F);
        cube_r6.texOffs(0, 0).addBox(3.9F, -4.75F, -6.8F, 2.0F, 2.0F, 0.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}