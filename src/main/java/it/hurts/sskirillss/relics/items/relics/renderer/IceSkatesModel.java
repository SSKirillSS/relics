package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class IceSkatesModel extends BipedModel<LivingEntity> {
    public IceSkatesModel() {
        super(1.0F, 0, 64, 64);

        setAllVisible(false);

        leftLeg = new ModelRenderer(this);
        rightLeg = new ModelRenderer(this);
        ModelRenderer model = new ModelRenderer(this);
        model.setPos(-2.0F, 12.0F, 0.0F);
        leftLeg.addChild(model);
        rightLeg.addChild(model);
        model.texOffs(0, 9).addBox(-1.0F, -5.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
        model.texOffs(0, 21).addBox(-1.0F, -2.0F, -5.0F, 6.0F, 3.0F, 2.0F, 0.0F, false);
        model.texOffs(0, 0).addBox(-1.5F, -7.0F, -3.5F, 7.0F, 2.0F, 7.0F, 0.0F, false);
        model.texOffs(16, 13).addBox(1.5F, 0.745F, -5.005F, 1.0F, 1.0F, 8.0F, 0.0F, false);
        model.texOffs(28, 5).addBox(1.5F, -1.065F, -5.58F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 1.75F, 0.0F);
        model.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.6109F, 0.0F, 0.0F);
        cube_r1.texOffs(28, 5).addBox(1.5F, -3.875F, -4.1F, 1.0F, 1.0F, 1.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}