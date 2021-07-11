package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class JellyfishNecklaceModel extends BipedModel<LivingEntity> {
    public JellyfishNecklaceModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        body = new ModelRenderer(this);

        ModelRenderer model = new ModelRenderer(this);
        model.setPos(0.0F, 23.0F, 0.0F);
        body.addChild(model);
        model.texOffs(0, 9).addBox(-1.5F, -21.0F, -5.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        model.texOffs(10, 13).addBox(-1.0F, -18.0F, -4.5F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        model.texOffs(0, 0).addBox(-4.0F, -23.15F, -2.15F, 8.0F, 5.0F, 4.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, -23.0F, 0.0F);
        model.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, 0.5236F);
        cube_r1.texOffs(0, 0).addBox(1.825F, 5.15F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, -23.0F, 0.0F);
        model.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, -0.5236F);
        cube_r2.texOffs(0, 15).addBox(-2.825F, 5.15F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(0.0F, -23.0F, 0.0F);
        model.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.5236F, 0.0F, 0.0F);
        cube_r3.texOffs(4, 15).addBox(-0.5F, 3.4F, -5.85F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(0.0F, -23.0F, 0.0F);
        model.addChild(cube_r4);
        setRotationAngle(cube_r4, -0.5236F, 0.0F, 0.0F);
        cube_r4.texOffs(8, 16).addBox(-0.5F, 6.9F, -1.2F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}