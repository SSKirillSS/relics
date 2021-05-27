package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class OldBootModel extends BipedModel<LivingEntity> {
    public OldBootModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        leftLeg = new ModelRenderer(this);
        leftLeg.setPos(2.0F, 12.0F, 0.0F);
        leftLeg.texOffs(0, 0).addBox(-2.5F, 10.1F, -4.5F, 5.0F, 2.0F, 7.0F, 0.0F, false);
        leftLeg.texOffs(0, 9).addBox(-2.5F, 7.1F, -2.5F, 5.0F, 3.0F, 5.0F, 0.0F, false);

        rightLeg = new ModelRenderer(this);
        rightLeg.setPos(2.0F, 12.0F, 0.0F);
        rightLeg.texOffs(0, 0).addBox(-2.5F, 10.1F, -4.5F, 5.0F, 2.0F, 7.0F, 0.0F, false);
        rightLeg.texOffs(0, 9).addBox(-2.5F, 7.1F, -2.5F, 5.0F, 3.0F, 5.0F, 0.0F, false);
    }
}