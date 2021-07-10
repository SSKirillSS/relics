package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class ReflectionNecklaceModel extends BipedModel<LivingEntity> {

    public ReflectionNecklaceModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        body = new ModelRenderer(this);
        
        ModelRenderer model = new ModelRenderer(this);
        model.setPos(0.0F, 23.0F, 0.0F);
        body.addChild(model);
        model.texOffs(0, 0).addBox(-4.0F, -23.15F, -2.15F, 8.0F, 5.0F, 4.0F, 0.0F, false);
        model.texOffs(0, 12).addBox(0.4F, -18.5F, -3.001F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        model.texOffs(11, 11).addBox(-1.4F, -18.5F, -3.001F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        model.texOffs(0, 0).addBox(-0.5F, -18.5F, -3.003F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, -24.0F, 0.0F);
        model.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, 0.7854F);
        cube_r1.texOffs(0, 9).addBox(4.25F, 4.25F, -3.002F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        cube_r1.texOffs(6, 9).addBox(2.9F, 2.9F, -3.002F, 2.0F, 2.0F, 1.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}