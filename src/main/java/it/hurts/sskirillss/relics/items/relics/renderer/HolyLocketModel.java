package it.hurts.sskirillss.relics.items.relics.renderer;

import it.hurts.sskirillss.relics.utils.RenderUtils;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class HolyLocketModel extends BipedModel<LivingEntity> {
    public HolyLocketModel() {
        super(1.0F, 0, 32, 32);

        setAllVisible(false);

        body = new ModelRenderer(this);
        ModelRenderer model = new ModelRenderer(this);
        model.setPos(0.0F, 23.0F, 0.0F);
        body.addChild(model);
        model.texOffs(0, 0).addBox(-4.0F, -23.15F, -2.15F, 8.0F, 5.0F, 4.0F, 0.0F, false);
        model.texOffs(26, 0).addBox(-1.0F, -18.65F, -2.65F, 2.0F, 6.0F, 1.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r1);
        RenderUtils.setRotationAngle(cube_r1, 0.0F, 0.0F, 0.2182F);
        cube_r1.texOffs(0, 8).addBox(-6.575F, -16.725F, -2.55F, 6.0F, 1.0F, 1.0F, 0.0F, false);
    }
}