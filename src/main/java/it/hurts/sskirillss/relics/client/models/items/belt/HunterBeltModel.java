package it.hurts.sskirillss.relics.client.models.items.belt;

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

public class HunterBeltModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(Reference.MODID, "hunter_belt"), "hunter_belt");

    public ModelPart model;

    public HunterBeltModel(ModelPart root) {
        super(root);

        this.model = root.getChild("bone");
    }

    public static LayerDefinition createLayer() {
        CubeDeformation cube = new CubeDeformation(0.4F);
        MeshDefinition mesh = HumanoidModel.createMesh(cube, 0.0F);

        PartDefinition part = mesh.getRoot();

        PartDefinition bone = part.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, 9.0F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 7).addBox(-2.05F, -1.5F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, -2.5F, -0.0311F, -0.0648F, -0.1274F));
        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(10, 8).addBox(-0.0063F, -1.4881F, -0.4455F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.8521F, 11.4104F, -0.6228F, -0.0677F, 0.0301F, -0.2435F));
        bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(10, 4).addBox(-0.1392F, -2.2899F, -2.6272F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.8521F, 11.4104F, -0.6228F, -0.2608F, -0.0226F, -0.0843F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.model);
    }

    @Override
    @Nonnull
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of();
    }
}