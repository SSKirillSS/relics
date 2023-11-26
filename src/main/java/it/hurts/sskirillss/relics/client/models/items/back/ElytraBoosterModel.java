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

public class ElytraBoosterModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(Reference.MODID, "elytra_booster"), "elytra_booster");

    public ElytraBoosterModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0.0F);

        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bone = body.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-5.0F, -0.075F, 1.25F, 10.0F, 6.0F, 4.0F, new CubeDeformation(0.025F)).mirror(false)
                .texOffs(0, 23).addBox(-2.0F, -2.0F, 3.75F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.25F, 0.0F, 0.0F));

        PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(28, 0).addBox(-3.75F, -11.0F, -1.001F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 10).mirror().addBox(-3.75F, -8.0F, -1.0F, 6.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.0F, 11.25F, 2.25F, 0.0F, 0.0F, 0.1309F));
        PartDefinition cube_r2 = bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(28, 0).mirror().addBox(2.75F, -11.0F, -1.001F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 10).addBox(-2.25F, -8.0F, -1.0F, 6.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 11.25F, 2.25F, 0.0F, 0.0F, -0.1309F));
        PartDefinition cube_r3 = bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(28, 7).mirror().addBox(-0.15F, -2.975F, -1.8F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-8.7786F, 3.4288F, 4.25F, 0.0F, 0.0F, 0.5236F));
        PartDefinition cube_r4 = bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(28, 7).addBox(-3.875F, -2.975F, -1.8F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.7786F, 3.4288F, 4.25F, 0.0F, 0.0F, -0.5236F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body);
    }
}