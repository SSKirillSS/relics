package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class IceSkatesModel extends BipedModel<LivingEntity> {
    public IceSkatesModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        leftLeg = new ModelRenderer(this);
        rightLeg = new ModelRenderer(this);
        ModelRenderer model = new ModelRenderer(this);
        model.setPos(-2.0F, 12.0F, 0.0F);
        leftLeg.addChild(model);
        rightLeg.addChild(model);
        model.texOffs(0, 8).addBox(-0.5F, -5.999F, -2.5F, 5.0F, 6.0F, 5.0F, 0.0F, false);
        model.texOffs(18, 0).addBox(-0.5F, -3.0F, -4.5F, 5.0F, 3.0F, 2.0F, 0.0F, false);
        model.texOffs(13, 12).addBox(1.5F, 0.0F, -4.0F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        model.texOffs(0, 3).addBox(1.5F, -1.425F, -5.415F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        model.texOffs(0, 0).addBox(-1.0F, -7.0F, -3.0F, 6.0F, 2.0F, 6.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(-2.0F, -11.0F, 0.0F);
        model.addChild(cube_r1);
        setRotationAngle(cube_r1, -0.7854F, 0.0F, 0.0F);
        cube_r1.texOffs(0, 0).addBox(3.5F, 10.315F, 3.655F, 1.0F, 1.0F, 2.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}