package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class RageGloveModel extends BipedModel<LivingEntity> {
    public RageGloveModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        rightArm = new ModelRenderer(this);
        ModelRenderer right = new ModelRenderer(this);
        right.setPos(4.5F, 22.0F, 0.0F);
        rightArm.addChild(right);
        right.texOffs(0, 10).addBox(-8.0F, -16.75F, -2.5F, 5.0F, 5.0F, 5.0F, 0.0F, false);
        right.texOffs(0, 20).addBox(-8.5F, -16.75F, -3.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
        right.texOffs(15, 6).addBox(-6.875F, -16.625F, -2.625F, 4.0F, 5.0F, 4.0F, 0.0F, false);
        right.texOffs(1, 1).addBox(-8.125F, -16.6249F, -2.6251F, 4.0F, 5.0F, 4.0F, 0.0F, false);

        leftArm = new ModelRenderer(this);
        ModelRenderer left = new ModelRenderer(this);
        left.setPos(6.5F, 22.0F, 0.0F);
        leftArm.addChild(left);
        left.texOffs(0, 10).addBox(-8.0F, -16.75F, -2.5F, 5.0F, 5.0F, 5.0F, 0.0F, false);
        left.texOffs(0, 20).addBox(-8.5F, -16.75F, -3.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
        left.texOffs(15, 6).addBox(-6.875F, -16.625F, -2.625F, 4.0F, 5.0F, 4.0F, 0.0F, false);
        left.texOffs(1, 1).addBox(-8.125F, -16.6249F, -2.6251F, 4.0F, 5.0F, 4.0F, 0.0F, false);
    }
}