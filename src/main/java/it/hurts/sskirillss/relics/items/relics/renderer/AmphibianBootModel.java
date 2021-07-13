package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class AmphibianBootModel extends BipedModel<LivingEntity> {
    public AmphibianBootModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        leftLeg = new ModelRenderer(this);
        rightLeg = new ModelRenderer(this);
        ModelRenderer model = new ModelRenderer(this);
        model.setPos(-2.0F, 12.0F, 0.0F);
        leftLeg.addChild(model);
        rightLeg.addChild(model);
        model.texOffs(0, 0).addBox(-0.5F, -7.999F, -2.5F, 5.0F, 8.0F, 5.0F, 0.0F, false);
        model.texOffs(15, 0).addBox(-0.5F, -3.0F, -4.5F, 5.0F, 3.0F, 2.0F, 0.0F, false);
        model.texOffs(0, 13).addBox(-1.0F, -8.5F, -3.0F, 6.0F, 2.0F, 6.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(-2.0F, -12.0F, 1.4142F);
        model.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.7854F, 0.0F);
        cube_r1.texOffs(0, 13).addBox(-1.7F, 5.5F, 0.8368F, 3.0F, 5.0F, 0.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(-2.0F, -12.0F, 1.4142F);
        model.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, -0.7854F, 0.0F);
        cube_r2.texOffs(18, 13).addBox(4.3F, 5.5F, -4.82F, 3.0F, 5.0F, 0.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}