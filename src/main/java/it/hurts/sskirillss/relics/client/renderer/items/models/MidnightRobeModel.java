package it.hurts.sskirillss.relics.client.renderer.items.models;

import it.hurts.sskirillss.relics.utils.RenderUtils;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class MidnightRobeModel extends BipedModel<LivingEntity> {
    public MidnightRobeModel() {
        super(1.0F, 0, 64, 64);

        setAllVisible(false);

        leftArm = new ModelRenderer(this);
        ModelRenderer left = new ModelRenderer(this);
        left.setPos(-5.0F, 22.0F, 0.0F);
        leftArm.addChild(left);
        left.texOffs(34, 0).addBox(3.5F, -24.25F, -2.5F, 5.0F, 9.0F, 5.0F, 0.0F, false);

        rightArm = new ModelRenderer(this);
        ModelRenderer right = new ModelRenderer(this);
        right.setPos(-7.0F, 22.0F, 0.0F);
        rightArm.addChild(right);
        right.texOffs(33, 32).addBox(3.5F, -24.25F, -2.5F, 5.0F, 9.0F, 5.0F, 0.0F, false);

        head = new ModelRenderer(this);
        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 24.0F, 0.0F);
        head.addChild(cube_r1);
        RenderUtils.setRotationAngle(cube_r1, -0.1309F, 0.0F, 0.0F);
        cube_r1.texOffs(0, 26).addBox(-5.5F, -26.75F, -5.8F, 11.0F, 3.0F, 8.0F, 0.0F, false);

        body = new ModelRenderer(this);
        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, 24.0F, 0.0F);
        body.addChild(cube_r2);
        RenderUtils.setRotationAngle(cube_r2, 0.0436F, 0.0F, 0.0F);
        cube_r2.texOffs(0, 0).addBox(-5.5F, -24.25F, -1.35F, 11.0F, 20.0F, 6.0F, 0.0F, false);
    }
}