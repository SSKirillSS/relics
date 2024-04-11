package it.hurts.sskirillss.relics.client.models.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

public class DissectionModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart center;

    private final ModelPart ring1;
    private final ModelPart ring2;
    private final ModelPart ring3;
    private final ModelPart ring4;

    public DissectionModel() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("ring1", CubeListBuilder.create().texOffs(0, 0).addBox(-24.0F, -24.0F, -1.5F, 48.0F, 48.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.5F));
        partdefinition.addOrReplaceChild("ring2", CubeListBuilder.create().texOffs(0, 95).addBox(-24.0F, -24.0F, -1.0F, 48.0F, 48.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.5F));
        partdefinition.addOrReplaceChild("center", CubeListBuilder.create().texOffs(0, 48).addBox(-24.0F, -24.0F, -0.5F, 48.0F, 48.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.5F));
        partdefinition.addOrReplaceChild("ring3", CubeListBuilder.create().texOffs(96, 95).addBox(-24.0F, -24.0F, 0.0F, 48.0F, 48.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.5F));
        partdefinition.addOrReplaceChild("ring4", CubeListBuilder.create().texOffs(96, 0).addBox(-24.0F, -24.0F, 0.5F, 48.0F, 48.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.5F));

        this.ring1 = LayerDefinition.create(meshdefinition, 256, 256).bakeRoot().getChild("ring1");
        this.ring2 = LayerDefinition.create(meshdefinition, 256, 256).bakeRoot().getChild("ring2");
        this.center = LayerDefinition.create(meshdefinition, 256, 256).bakeRoot().getChild("center");
        this.ring3 = LayerDefinition.create(meshdefinition, 256, 256).bakeRoot().getChild("ring3");
        this.ring4 = LayerDefinition.create(meshdefinition, 256, 256).bakeRoot().getChild("ring4");
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Minecraft MC = Minecraft.getInstance();
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null)
            return;

        float time = (float) Math.sin((player.tickCount + (MC.isPaused() ? 0 : MC.getFrameTime())) / 20F) * 50F;

        poseStack.pushPose();

        poseStack.mulPose(Vector3f.ZP.rotationDegrees(time));

        ring1.render(poseStack, buffer, packedLight, packedOverlay);
        ring4.render(poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.mulPose(Vector3f.ZN.rotationDegrees(time));

        ring2.render(poseStack, buffer, packedLight, packedOverlay);
        ring3.render(poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.mulPose(Vector3f.ZN.rotationDegrees(time));

        center.render(poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}