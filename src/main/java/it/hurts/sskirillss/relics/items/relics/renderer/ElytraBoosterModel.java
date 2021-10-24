package it.hurts.sskirillss.relics.items.relics.renderer;

import it.hurts.sskirillss.relics.utils.RenderUtils;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class ElytraBoosterModel extends BipedModel<LivingEntity> {
    public ElytraBoosterModel() {
        super(1.0F, 0, 64, 64);

        setAllVisible(false);

        body = new ModelRenderer(this);
        ModelRenderer model = new ModelRenderer(this);
        model.setPos(0.0F, 24.0F, 0.0F);
        body.addChild(model);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, -24.0F, 0.0F);
        model.addChild(cube_r1);
        RenderUtils.setRotationAngle(cube_r1, -0.9F, 0.0F, 0.0F);
        cube_r1.texOffs(0, 8).addBox(-1.0F, -5.5F, -0.5F, 2.0F, 2.0F, 7.0F, 0.0F, false);
        cube_r1.texOffs(13, 12).addBox(-1.5F, -6.0F, 0.5F, 3.0F, 3.0F, 5.0F, 0.0F, false);
        cube_r1.texOffs(0, 0).addBox(-7.0F, -4.5F, 0.5F, 14.0F, 3.0F, 5.0F, 0.0F, false);
    }
}