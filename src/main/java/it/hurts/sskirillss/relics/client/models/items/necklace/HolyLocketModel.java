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

public class HolyLocketModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(Reference.MODID, "holy_locket"), "holy_locket");

    public ModelPart model;

    public HolyLocketModel(ModelPart root) {
        super(root);

        this.model = root.getChild("bone");
    }

    public static LayerDefinition createLayer() {
        CubeDeformation cube = new CubeDeformation(0.4F);
        MeshDefinition mesh = HumanoidModel.createMesh(cube, 0.0F);

        PartDefinition part = mesh.getRoot();

        PartDefinition bone = part.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 3).addBox(-8.0F, -1.15F, -4.15F, 16.0F, 7.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 18).addBox(-2.5917F, -0.8822F, -0.525F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0354F, 7.9071F, -4.575F, 0.0F, 0.0F, 0.2568F));
        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(12, 18).addBox(-2.95F, -3.025F, 2.425F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0347F, 10.3F, -8.125F, 0.0F, 0.0F, 0.7854F));
        bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0104F, -2.6071F, -0.55F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0354F, 7.9071F, -4.575F, 0.0F, 0.0F, -0.004F));

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