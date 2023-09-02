package it.hurts.sskirillss.relics.client.models.items.necklace;

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

public class JellyfishNecklaceModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(Reference.MODID, "jellyfish_necklace"), "jellyfish_necklace");

    public ModelPart model;

    public JellyfishNecklaceModel(ModelPart root) {
        super(root);

        this.model = root.getChild("bone");
    }

    public static LayerDefinition createLayer() {
        CubeDeformation cube = new CubeDeformation(0.4F);
        MeshDefinition mesh = HumanoidModel.createMesh(cube, 0.0F);

        PartDefinition part = mesh.getRoot();

        PartDefinition bone = part.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-9.6401F, -10.4222F, 0.475F, 16.0F, 7.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(1.6401F, 10.4222F, -4.625F));

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(10, 16).addBox(-0.675F, 0.575F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6109F, -2.1891F, 0.025F, 0.0F, 0.0F, -0.7854F));
        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(10, 16).addBox(0.0F, -2.5F, -0.475F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6109F, -2.1891F, 0.025F, 0.0F, 0.0F, 1.5708F));
        bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(10, 16).addBox(1.5F, -2.25F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6109F, -2.1891F, 0.025F, 0.0F, 0.0F, 0.7854F));
        bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(10, 16).addBox(-1.0104F, 0.5429F, -0.55F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.225F)), PartPose.offsetAndRotation(-1.6047F, -3.2401F, 0.05F, 0.0F, 0.0F, -0.004F));
        bone.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(1, 16).addBox(-1.5104F, -2.4571F, -1.05F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6047F, -2.7401F, 0.05F, 0.0F, 0.0F, -0.004F));

        return LayerDefinition.create(mesh, 64, 64);
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