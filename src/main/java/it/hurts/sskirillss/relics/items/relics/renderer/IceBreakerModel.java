package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class IceBreakerModel extends BipedModel<LivingEntity> {
    public IceBreakerModel() {
        super(1.0F, 0, 64, 64);

        setAllVisible(false);

        leftLeg = new ModelRenderer(this);
        rightLeg = new ModelRenderer(this);
        ModelRenderer model = new ModelRenderer(this);
        model.setPos(-2.0F, 12.0F, 0.0F);
        leftLeg.addChild(model);
        rightLeg.addChild(model);
        model.texOffs(0, 9).addBox(-1.0F, -5.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
        model.texOffs(21, 0).addBox(-1.0F, -2.0F, -5.0F, 6.0F, 3.0F, 2.0F, 0.0F, false);
        model.texOffs(0, 0).addBox(-1.5F, -7.0F, -3.5F, 7.0F, 2.0F, 7.0F, 0.0F, false);
        model.texOffs(17, 14).addBox(-0.5F, 1.0F, -4.5F, 5.0F, 1.0F, 7.0F, 0.0F, false);
        model.texOffs(0, 21).addBox(0.5F, 1.0F, -3.5F, 3.0F, 1.0F, 5.0F, 0.0F, false);
    }
}