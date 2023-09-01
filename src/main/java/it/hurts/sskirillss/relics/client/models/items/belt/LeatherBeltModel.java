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

public class LeatherBeltModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(Reference.MODID, "leather_belt"), "leather_belt");

    public ModelPart model;

    public LeatherBeltModel(ModelPart root) {
        super(root);

        this.model = root.getChild("bone");
    }

    public static LayerDefinition createLayer() {
        CubeDeformation cube = new CubeDeformation(0.4F);
        MeshDefinition mesh = HumanoidModel.createMesh(cube, 0.0F);

        PartDefinition part = mesh.getRoot();

        PartDefinition bone = part.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, 9.0F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 7).addBox(-2.05F, -1.55F, -0.675F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, -2.5F, -0.1516F, 0.0512F, -0.0859F));

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