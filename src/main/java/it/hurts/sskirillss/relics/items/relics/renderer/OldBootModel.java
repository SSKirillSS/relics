package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class OldBootModel extends BipedModel<LivingEntity> {
    public OldBootModel() {
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
        model.texOffs(0, 13).addBox(-1.0F, -5.25F, -3.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
        model.texOffs(18, 13).addBox(0.5F, -5.75F, -3.25F, 3.0F, 2.0F, 1.0F, 0.0F, false);
        model.texOffs(0, 21).addBox(-1.0F, -8.5F, -3.0F, 6.0F, 2.0F, 6.0F, 0.0F, false);
    }
}