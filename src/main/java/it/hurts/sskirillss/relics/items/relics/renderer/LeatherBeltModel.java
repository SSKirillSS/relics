package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class LeatherBeltModel extends BipedModel<LivingEntity> {
	public LeatherBeltModel() {
		super(1.0F, 0, 32, 32);

		setAllVisible(false);

		body = new ModelRenderer(this);
		ModelRenderer model = new ModelRenderer(this);
		model.setPos(0.0F, 25.0F, 0.0F);
		body.addChild(model);

		model.texOffs(0, 0).addBox(-4.5F, -15.5F, -2.5F, 9.0F, 3.0F, 5.0F, 0.0F, false);
		model.texOffs(0, 8).addBox(-1.5F, -15.5F, -3.0F, 3.0F, 3.0F, 1.0F, 0.0F, false);
	}
}