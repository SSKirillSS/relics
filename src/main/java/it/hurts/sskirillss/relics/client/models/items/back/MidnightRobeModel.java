package it.hurts.sskirillss.relics.client.models.items.back;

import com.google.common.collect.ImmutableList;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;

public class MidnightRobeModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(Reference.MODID, "midnight_robe"), "midnight_robe");

    public MidnightRobeModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0.0F);

        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 25).addBox(-5.0F, -1.275F, -1.75F, 10.0F, 3.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.5F, 0.3927F, 0.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -0.5F, -3.0F, 10.0F, 7.0F, 6.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 8).addBox(-5.0F, -6.6F, -1.6F, 10.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-5.0F, -6.6F, 4.4F, 10.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).mirror().addBox(-4.99F, -6.6F, -1.6F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 28).addBox(4.99F, -6.6F, -1.6F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 17.9046F, 1.5671F, 0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r3 = body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 13).addBox(-5.0F, -4.65F, -3.9F, 10.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.5F, 1.5F, 0.1309F, 0.0F, 0.0F));

        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 43).addBox(-1.0F, -2.25F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(6.0F, 2.0F, 0.0F));

        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 43).mirror().addBox(-3.0F, -2.25F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(-6.0F, 2.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.leftArm, this.rightArm);
    }
}