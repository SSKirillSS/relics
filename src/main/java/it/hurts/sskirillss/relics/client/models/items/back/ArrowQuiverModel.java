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

public class ArrowQuiverModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(Reference.MODID, "arrow_quiver"), "arrow_quiver");

    public ArrowQuiverModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0.0F);

        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(16, 16).addBox(-1.5F, -3.0F, -1.75F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 21).addBox(-1.0F, -2.0F, -1.25F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.25F))
                .texOffs(0, 16).addBox(-1.5F, 2.0F, -1.75F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 2.5F, 3.5F, 0.0F, 0.0F, -0.7854F));

        PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(27, 10).addBox(-1.2F, -0.25F, 0.175F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.4534F, -1.2997F, 3.75F, 2.9686F, -0.8855F, 2.0959F));
        PartDefinition cube_r3 = body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(27, 10).addBox(-0.825F, -0.25F, 0.175F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.4534F, -1.2997F, 3.75F, 3.0018F, 0.6732F, 1.874F));
        PartDefinition cube_r4 = body.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(29, 12).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.4534F, -0.4497F, 3.75F, 3.0018F, 0.6732F, 1.874F));
        PartDefinition cube_r5 = body.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(29, 12).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.4534F, -0.4497F, 3.75F, 2.9686F, -0.8855F, 2.0959F));
        PartDefinition cube_r6 = body.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(27, 5).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.7784F, -2.1997F, 5.375F, -0.5251F, -0.8705F, -0.3867F));
        PartDefinition cube_r7 = body.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(27, 5).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.7784F, -2.1997F, 5.375F, -2.7418F, -0.5916F, 2.1065F));
        PartDefinition cube_r8 = body.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(29, 7).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4534F, -0.9497F, 4.75F, -2.7418F, -0.5916F, 2.1065F));
        PartDefinition cube_r9 = body.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(29, 7).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4534F, -0.9497F, 4.75F, -0.5251F, -0.8705F, -0.3867F));
        PartDefinition cube_r10 = body.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(27, 0).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.3784F, -4.1997F, 2.4F, 1.0472F, -1.3526F, -1.3963F));
        PartDefinition cube_r11 = body.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(27, 0).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.3784F, -4.1997F, 2.4F, 0.1897F, 0.1084F, -0.3387F));
        PartDefinition cube_r12 = body.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(29, 2).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.7034F, -2.4497F, 2.75F, 1.0472F, -1.3526F, -1.3963F));
        PartDefinition cube_r13 = body.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(29, 2).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.7034F, -2.4497F, 2.75F, 0.1897F, 0.1084F, -0.3387F));

        return LayerDefinition.create(mesh, 32, 32);
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