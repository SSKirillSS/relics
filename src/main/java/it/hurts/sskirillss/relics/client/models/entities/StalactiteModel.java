package it.hurts.sskirillss.relics.client.models.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class StalactiteModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart stone;

    public StalactiteModel() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition stone = partdefinition.addOrReplaceChild("stone", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -10.0F, 0.0F, 16.0F, 20.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, -16).addBox(0.0F, -10.0F, -8.0F, 0.0F, 20.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 0.0F));

        PartDefinition g4_r1 = stone.addOrReplaceChild("g4_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -10.0F, 0.0F, 16.0F, 20.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, -16).addBox(0.0F, -10.0F, -8.0F, 0.0F, 20.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        this.stone = LayerDefinition.create(meshdefinition, 64, 64).bakeRoot().getChild("stone");;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        stone.render(poseStack, buffer, packedLight, packedOverlay);
    }
}