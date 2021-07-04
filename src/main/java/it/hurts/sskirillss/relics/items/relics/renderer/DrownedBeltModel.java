package it.hurts.sskirillss.relics.items.relics.renderer;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class DrownedBeltModel extends BipedModel<LivingEntity> {
    public DrownedBeltModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        body = new ModelRenderer(this);

        ModelRenderer belt = new ModelRenderer(this);
        belt.setPos(0.0F, 24.0F, 0.0F);
        body.addChild(belt);

        belt.texOffs(0, 0).addBox(-4.5F, -14.0F, -2.5F, 9.0F, 2.0F, 5.0F, 0.0F, false);
        belt.texOffs(0, 9).addBox(-2.0F, -14.5F, -3.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        belt.texOffs(0, 0).addBox(1.0F, -14.5F, -3.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        belt.texOffs(6, 7).addBox(-1.0F, -12.5F, -3.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        belt.texOffs(0, 7).addBox(-1.0F, -14.5F, -3.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
    }
}