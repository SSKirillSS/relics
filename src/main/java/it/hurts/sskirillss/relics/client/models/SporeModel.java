package it.hurts.sskirillss.relics.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class SporeModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart spore;

    public SporeModel() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition spore = partdefinition.addOrReplaceChild("spore", CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -26F, -2F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        this.spore = LayerDefinition.create(meshdefinition, 16, 16).bakeRoot().getChild("spore");;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        spore.render(poseStack, buffer, packedLight, packedOverlay);
    }
}