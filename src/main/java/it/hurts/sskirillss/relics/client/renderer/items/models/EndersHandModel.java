package it.hurts.sskirillss.relics.client.renderer.items.models;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class EndersHandModel extends BipedModel<LivingEntity> {
    public EndersHandModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        rightArm = new ModelRenderer(this);
        ModelRenderer right = new ModelRenderer(this);
        right.setPos(-7.0F, 22.0F, 0.0F);
        rightArm.addChild(right);
        right.texOffs(0, 10).addBox(3.5F, -16.999F, -2.5F, 5.0F, 5.0F, 5.0F, 0.0F, false);
        right.texOffs(0, 0).addBox(3.5F, -16.75F, -2.75F, 5.0F, 5.0F, 5.0F, 0.0F, false);
        right.texOffs(0, 20).addBox(3.0F, -17.0F, -3.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
        right.texOffs(18, 23).addBox(5.0F, -15.0F, -2.75F, 2.0F, 2.0F, 1.0F, 0.0F, false);

        leftArm = new ModelRenderer(this);
        ModelRenderer left = new ModelRenderer(this);
        left.setPos(-5.0F, 22.0F, 0.0F);
        leftArm.addChild(left);
        left.texOffs(0, 10).addBox(3.5F, -16.999F, -2.5F, 5.0F, 5.0F, 5.0F, 0.0F, false);
        left.texOffs(0, 0).addBox(3.5F, -16.75F, -2.75F, 5.0F, 5.0F, 5.0F, 0.0F, false);
        left.texOffs(0, 20).addBox(3.0F, -17.0F, -3.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
        left.texOffs(18, 23).addBox(5.0F, -15.0F, -2.75F, 2.0F, 2.0F, 1.0F, 0.0F, false);
    }
}