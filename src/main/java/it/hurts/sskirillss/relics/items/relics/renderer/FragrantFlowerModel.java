package it.hurts.sskirillss.relics.items.relics.renderer;

import it.hurts.sskirillss.relics.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class FragrantFlowerModel extends BipedModel<LivingEntity> {
    public FragrantFlowerModel() {
        super(1.0F, 0, 64, 64);

        ClientPlayerEntity player = Minecraft.getInstance().player;

        setAllVisible(false);

        head = new ModelRenderer(this);

        ModelRenderer model = new ModelRenderer(this);
        model.setPos(0.0F, player != null && player.hasItemInSlot(EquipmentSlotType.HEAD) ? -8.5F : - 7.5F, 0.0F);
        head.addChild(model);
        model.texOffs(-18, 0).addBox(-9.0F, -2.2F, -9.0F, 18.0F, 0.0F, 18.0F, 0.0F, false);
        model.texOffs(1, 50).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 0.0F, 4.0F, 0.0F, false);

        ModelRenderer cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, 0.1F, 0.0F);
        model.addChild(cube_r1);
        RenderUtils.setRotationAngle(cube_r1, 0.0F, 0.7854F, 0.0F);
        cube_r1.texOffs(-18, 27).addBox(-9.0F, -1.0F, -9.0F, 18.0F, 0.0F, 18.0F, 0.0F, false);
        cube_r1.texOffs(21, 43).addBox(0.0F, -4.0F, -4.0F, 0.0F, 3.0F, 8.0F, 0.0F, false);

        ModelRenderer cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, 0.1F, 0.0F);
        model.addChild(cube_r2);
        RenderUtils.setRotationAngle(cube_r2, -3.1416F, 0.7854F, 3.1416F);
        cube_r2.texOffs(21, 43).addBox(0.0F, -4.0F, -4.0F, 0.0F, 3.0F, 8.0F, 0.0F, false);

        ModelRenderer cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(0.0F, 0.1F, 0.0F);
        model.addChild(cube_r3);
        RenderUtils.setRotationAngle(cube_r3, 1.1781F, 0.0F, 0.0F);
        cube_r3.texOffs(0, 21).addBox(-3.0F, -6.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, false);

        ModelRenderer cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(0.0F, 0.1F, 0.0F);
        model.addChild(cube_r4);
        RenderUtils.setRotationAngle(cube_r4, 0.0F, 0.0F, 1.1781F);
        cube_r4.texOffs(6, 15).addBox(0.0F, -6.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

        ModelRenderer cube_r5 = new ModelRenderer(this);
        cube_r5.setPos(0.0F, 0.1F, 0.0F);
        model.addChild(cube_r5);
        RenderUtils.setRotationAngle(cube_r5, 0.0F, 0.0F, -1.1781F);
        cube_r5.texOffs(0, 15).addBox(0.0F, -6.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

        ModelRenderer cube_r6 = new ModelRenderer(this);
        cube_r6.setPos(0.0F, 0.1F, 0.0F);
        model.addChild(cube_r6);
        RenderUtils.setRotationAngle(cube_r6, -1.1781F, 0.0F, 0.0F);
        cube_r6.texOffs(6, 21).addBox(-3.0F, -6.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, false);
    }
}