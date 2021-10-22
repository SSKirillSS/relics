package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class SporeSackModel extends BipedModel<LivingEntity> {
    public SporeSackModel() {
        super(1.0F, 0, 16, 16);

        setAllVisible(false);

        body = new ModelRenderer(this);

        ModelRenderer model = new ModelRenderer(this);
        model.setPos(0.0F, 24.0F, 0.0F);
        body.addChild(model);
        model.texOffs(7, 7).addBox(1.0F, -14.25F, -5.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        model.texOffs(0, 6).addBox(0.5F, -14.25F, -6.5F, 3.0F, 0.0F, 3.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r1);
        cube_r1.texOffs(0, 0).addBox(0.5F, -12.75F, -6.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}