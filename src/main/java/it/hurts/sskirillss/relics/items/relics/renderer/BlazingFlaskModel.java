package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class BlazingFlaskModel extends BipedModel<LivingEntity> {
    public BlazingFlaskModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        body = new ModelRenderer(this);

        ModelRenderer cube_1 = new ModelRenderer(this);
        cube_1.setPos(-6.0F, 24.0F, 0.0F);
        body.addChild(cube_1);

        ModelRenderer cube_2 = new ModelRenderer(this);
        cube_2.setPos(0.0F, 0.0F, 0.0F);
        cube_1.addChild(cube_2);
        setRotationAngle(cube_2, 0.0F, -0.2182F, 0.0F);
        cube_2.texOffs(0, 9).addBox(0.5F, -14.0F, -5.5F, 3.0F, 4.0F, 3.0F, 0.0F, false);
        cube_2.texOffs(12, 0).addBox(0.5F, -15.5F, -5.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);
        cube_2.texOffs(0, 0).addBox(0.0F, -14.5F, -6.0F, 4.0F, 5.0F, 4.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}