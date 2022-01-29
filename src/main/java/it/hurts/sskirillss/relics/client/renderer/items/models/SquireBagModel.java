package it.hurts.sskirillss.relics.client.renderer.items.models;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class SquireBagModel extends BipedModel<LivingEntity> {

    public SquireBagModel() {
        super(1.0F, 0, 64, 64);

        setAllVisible(false);

        body = new ModelRenderer(this);

        ModelRenderer model = new ModelRenderer(this);
        model.setPos(0.0F, 24.0F, 0.0F);
        body.addChild(model);
        model.texOffs(26, 24).addBox(-4.0F, -22.0F, 2.5F, 8.0F, 3.0F, 4.0F, 0.0F, false);
        model.texOffs(25, 18).addBox(1.0F, -16.0F, 7.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);
        model.texOffs(0, 0).addBox(-4.5F, -24.0F, -2.5F, 9.0F, 12.0F, 5.0F, 0.0F, false);
        model.texOffs(25, 12).addBox(-4.5F, -23.0F, 2.0F, 9.0F, 1.0F, 5.0F, 0.0F, false);
        model.texOffs(0, 17).addBox(-5.0F, -19.0F, 2.0F, 10.0F, 6.0F, 5.0F, 0.0F, false);
        model.texOffs(23, 2).addBox(-4.0F, -16.0F, 7.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);
        model.texOffs(21, 0).addBox(-4.5F, -16.0F, 6.5F, 9.0F, 0.0F, 2.0F, 0.0F, false);
    }
}