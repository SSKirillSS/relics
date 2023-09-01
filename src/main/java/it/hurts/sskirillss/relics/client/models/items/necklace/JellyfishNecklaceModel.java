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

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 1).addBox(1.2078F, 0.2117F, -1.4935F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6151F, 0.1387F, -0.0065F, 0.0F, 0.0F, -0.7854F));
        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 3).addBox(-1.2078F, 0.2117F, -1.4935F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6151F, 0.1387F, -0.0065F, 0.0F, 0.0F, 0.7854F));
        bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 2).addBox(-1.4694F, -1.1311F, 0.1273F, 3.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6151F, 0.1387F, -0.0065F, -0.7854F, 0.0F, 0.0F));
        bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.4694F, -1.1061F, -2.1523F, 3.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6151F, 0.1387F, -0.0065F, 0.7854F, 0.0F, 0.0F));
        bone.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(9, 15).addBox(-1.0104F, 0.5429F, -1.05F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6047F, -2.0151F, 0.05F, 0.0F, 0.0F, -0.004F));
        bone.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 15).addBox(-1.5104F, -2.4571F, -1.55F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6047F, -1.5151F, 0.05F, 0.0F, 0.0F, -0.004F));

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