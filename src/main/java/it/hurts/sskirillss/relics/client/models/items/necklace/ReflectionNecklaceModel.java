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

public class ReflectionNecklaceModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(Reference.MODID, "reflection_necklace"), "reflection_necklace");

    public ModelPart model;

    public ReflectionNecklaceModel(ModelPart root) {
        super(root);

        this.model = root.getChild("bone");
    }

    public static LayerDefinition createLayer() {
        CubeDeformation cube = new CubeDeformation(0.4F);
        MeshDefinition mesh = HumanoidModel.createMesh(cube, 0.0F);

        PartDefinition part = mesh.getRoot();

        PartDefinition bone = part.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -1.15F, -4.15F, 16.0F, 7.0F, 8.0F, new CubeDeformation(0.5F))
                .texOffs(0, 0).addBox(-1.5F, 5.1F, -5.15F, 3.0F, 5.0F, 1.0F, new CubeDeformation(-0.075F))
                .texOffs(0, 15).addBox(-1.0F, 6.35F, -5.775F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(6, 15).addBox(0.1F, -2.1F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.6F, -4.65F, 0.0F, 0.0F, -0.7854F));

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