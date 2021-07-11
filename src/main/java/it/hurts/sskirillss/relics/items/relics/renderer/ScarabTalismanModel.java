package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class ScarabTalismanModel extends BipedModel<LivingEntity> {
    public ScarabTalismanModel() {
        super(1.0F, 0, 16, 16);

        setAllVisible(false);

        body = new ModelRenderer(this);
        ModelRenderer model = new ModelRenderer(this);
        body.addChild(model);
        model.setPos(0.0F, 24.0F, 0.0F);
        model.texOffs(8, 7).addBox(-5.0F, -15.0F, -1.0F, 1.0F, 3.0F, 2.0F, 0.0F, false);
        model.texOffs(0, 0).addBox(-4.51F, -16.0F, -2.0F, 1.0F, 5.0F, 4.0F, 0.0F, false);
        model.texOffs(0, 0).addBox(-4.999F, -15.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
    }
}