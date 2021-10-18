package it.hurts.sskirillss.relics.items.relics.renderer;

import it.hurts.sskirillss.relics.utils.RenderUtils;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class AmphibianBootModel extends BipedModel<LivingEntity> {
    public AmphibianBootModel() {
        super(1.0F, 0, 64, 64);

        setAllVisible(false);

        leftLeg = new ModelRenderer(this);
        rightLeg = new ModelRenderer(this);
        ModelRenderer model = new ModelRenderer(this);
        model.setPos(-2.0F, 12.0F, 0.0F);
        leftLeg.addChild(model);
        rightLeg.addChild(model);
        model.texOffs(0, 9).addBox(-1.0F, -5.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
        model.texOffs(18, 9).addBox(-1.0F, -2.0F, -5.0F, 6.0F, 3.0F, 2.0F, 0.0F, false);
        model.texOffs(0, 0).addBox(-1.5F, -7.0F, -3.5F, 7.0F, 2.0F, 7.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r1);
        RenderUtils.setRotationAngle(cube_r1, 0.0F, 0.4363F, 0.0F);
        cube_r1.texOffs(4, 21).addBox(2.275F, -4.8F, 4.325F, 1.0F, 4.0F, 3.0F, 0.0F, true);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, 0.0F, 0.0F);
        model.addChild(cube_r2);
        RenderUtils.setRotationAngle(cube_r2, 0.0F, -0.4363F, 0.0F);
        cube_r2.texOffs(21, 0).addBox(0.35F, -4.975F, 2.6F, 1.0F, 4.0F, 3.0F, 0.0F, false);
    }
}