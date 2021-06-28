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

        ModelRenderer petal_1 = new ModelRenderer(this);
        petal_1.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(petal_1);
        setRotationAngle(petal_1, 0.3491F, 0.0F, -0.6545F);
        petal_1.texOffs(0, 0).addBox(7.5F, -7.5F, -2.7F, 2.0F, 2.0F, 0.0F, 0.0F, false);

        ModelRenderer petal_2 = new ModelRenderer(this);
        petal_2.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(petal_2);
        setRotationAngle(petal_2, 0.2618F, -0.5672F, 0.0873F);
        petal_2.texOffs(0, 2).addBox(-2.2F, -11.25F, -2.7F, 2.0F, 2.0F, 0.0F, 0.0F, false);

        ModelRenderer petal_3 = new ModelRenderer(this);
        petal_3.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(petal_3);
        setRotationAngle(petal_3, 0.0F, 0.0F, -0.7854F);
        petal_3.texOffs(3, 3).addBox(7.6F, -3.5F, -5.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer petal_4 = new ModelRenderer(this);
        petal_4.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(petal_4);
        setRotationAngle(petal_4, 0.2182F, 0.5672F, 0.7418F);
        petal_4.texOffs(4, 0).addBox(-0.25F, -11.25F, -3.0F, 2.0F, 2.0F, 0.0F, 0.0F, false);

        ModelRenderer petal_5 = new ModelRenderer(this);
        petal_5.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(petal_5);
        setRotationAngle(petal_5, 0.2182F, -0.5672F, -0.7418F);
        petal_5.texOffs(0, 5).addBox(2.0F, -6.5F, -7.0F, 2.0F, 2.0F, 0.0F, 0.0F, false);

        ModelRenderer petal_6 = new ModelRenderer(this);
        petal_6.setPos(0.0F, -24.0F, 0.0F);
        center.addChild(petal_6);
        setRotationAngle(petal_6, -0.3927F, 0.0F, -0.1745F);
        petal_6.texOffs(4, 5).addBox(3.9F, -4.75F, -6.8F, 2.0F, 2.0F, 0.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}