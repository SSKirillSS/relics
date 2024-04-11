package it.hurts.sskirillss.relics.client.models.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class ShadowGlaiveModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart bone10;

    public ShadowGlaiveModel() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone10 = partdefinition.addOrReplaceChild("bone10", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone11 = bone10.addOrReplaceChild("bone11", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bone = bone11.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(30, 31).mirror().addBox(-4.0032F, -2.0035F, -8.1399F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(26, 19).mirror().addBox(-8.1032F, -2.0035F, -4.0149F, 2.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(26, 19).addBox(6.1468F, -2.0035F, -4.0149F, 2.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-8.0032F, -1.5475F, -8.0149F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-8.0032F, 0.5719F, -8.0149F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(30, 31).mirror().addBox(-4.0032F, -2.0035F, 6.1351F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0032F, -12.9975F, 0.0149F));

        PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(6.575F, -1.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).addBox(-8.5F, -1.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 10).addBox(-3.0F, -1.0F, -8.525F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 10).mirror().addBox(-3.0F, -1.0F, 6.625F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.0032F, -1.0025F, -0.0149F, 0.0F, -0.7854F, 0.0F));

        PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(-6.3844F, -0.0244F, -2.6145F));
        PartDefinition cube_r2 = bone2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, 0.7F, -5.75F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.3812F, -2.9782F, 2.5996F, 0.1309F, -0.5672F, -0.1745F));
        PartDefinition cube_r3 = bone2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 17).addBox(-19.0F, 3.0F, -5.75F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.3812F, -2.9782F, 2.5996F, 0.0F, -0.5672F, 0.0F));
        PartDefinition cube_r4 = bone2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, -0.525F, -5.75F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.3812F, 2.7718F, 2.5996F, -0.1309F, -0.576F, 0.1745F));

        PartDefinition bone3 = bone.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(-2.1095F, -0.0331F, -6.5124F));
        PartDefinition cube_r5 = bone3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(2, 41).addBox(-18.425F, 2.8F, -6.2F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1063F, -2.9694F, 6.4976F, 0.1309F, -1.3526F, 0.1091F));
        PartDefinition cube_r6 = bone3.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 17).addBox(-19.0F, 2.999F, -5.75F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1063F, -2.9694F, 6.4976F, 0.0F, -1.3526F, 0.0F));
        PartDefinition cube_r7 = bone3.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(2, 41).addBox(-18.425F, -2.625F, -6.2F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1063F, 2.7806F, 6.4976F, -0.1309F, -1.3614F, -0.1091F));

        PartDefinition bone4 = bone.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offset(3.1738F, -0.0294F, -6.8454F));
        PartDefinition cube_r8 = bone4.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, 2.35F, -6.15F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1771F, -2.9731F, 6.8305F, 0.1309F, -2.0944F, 0.0349F));
        PartDefinition cube_r9 = bone4.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 17).addBox(-19.0F, 3.0F, -5.75F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1771F, -2.9731F, 6.8305F, 0.0F, -2.0944F, 0.0F));
        PartDefinition cube_r10 = bone4.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, -2.175F, -6.15F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1771F, 2.7769F, 6.8305F, -0.1309F, -2.1031F, -0.0349F));

        PartDefinition bone5 = bone.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(6.7812F, -0.0283F, -2.1706F));
        PartDefinition cube_r11 = bone5.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, -2.025F, -6.15F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.7844F, 2.7758F, 2.1558F, -0.1309F, -2.9322F, -0.0349F));
        PartDefinition cube_r12 = bone5.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(0, 17).addBox(-19.0F, 2.999F, -5.75F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.7844F, -2.9742F, 2.1558F, 0.0F, -2.8798F, 0.0F));
        PartDefinition cube_r13 = bone5.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, 2.2F, -6.15F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.7844F, -2.9742F, 2.1558F, 0.1309F, -2.9234F, 0.0349F));

        PartDefinition bone6 = bone.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offset(6.4378F, -0.0272F, 2.6973F));
        PartDefinition cube_r14 = bone6.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, -2.025F, -6.15F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.4411F, 2.7746F, -2.7122F, -0.1309F, 2.5656F, -0.0349F));
        PartDefinition cube_r15 = bone6.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(0, 17).addBox(-19.0F, 3.0F, -5.75F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.4411F, -2.9754F, -2.7122F, 0.0F, 2.5744F, 0.0F));
        PartDefinition cube_r16 = bone6.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, 2.2F, -6.15F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.4411F, -2.9754F, -2.7122F, 0.1309F, 2.5744F, 0.0349F));

        PartDefinition bone7 = bone.addOrReplaceChild("bone7", CubeListBuilder.create(), PartPose.offset(2.1086F, -0.0305F, 6.8512F));
        PartDefinition cube_r17 = bone7.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, -2.575F, -6.15F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1118F, 2.7779F, -6.8661F, -0.1309F, 1.7802F, 0.0524F));
        PartDefinition cube_r18 = bone7.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(0, 17).addBox(-19.0F, 2.999F, -5.75F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1118F, -2.9721F, -6.8661F, 0.0F, 1.789F, 0.0F));
        PartDefinition cube_r19 = bone7.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, 2.75F, -6.15F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1118F, -2.9721F, -6.8661F, 0.1309F, 1.789F, -0.0524F));

        PartDefinition bone8 = bone.addOrReplaceChild("bone8", CubeListBuilder.create(), PartPose.offset(-2.9435F, -0.0302F, 6.9542F));
        PartDefinition cube_r20 = bone8.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, -2.025F, -6.15F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.9403F, 2.7776F, -6.9691F, -0.1309F, 1.069F, 0.0524F));
        PartDefinition cube_r21 = bone8.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(0, 17).addBox(-19.0F, 3.0F, -5.75F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.9403F, -2.9724F, -6.9691F, 0.0F, 1.0472F, 0.0F));
        PartDefinition cube_r22 = bone8.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(2, 41).addBox(-19.0F, 2.2F, -6.15F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.9403F, -2.9724F, -6.9691F, 0.1309F, 1.0777F, -0.0524F));

        PartDefinition bone9 = bone.addOrReplaceChild("bone9", CubeListBuilder.create(), PartPose.offset(-6.1824F, -0.0285F, 2.4184F));
        PartDefinition cube_r23 = bone9.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(2, 41).addBox(-18.3F, -1.425F, -4.775F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.1792F, 2.776F, -2.4332F, -0.0524F, 0.1614F, 0.0916F));
        PartDefinition cube_r24 = bone9.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(0, 17).addBox(-19.0F, 2.999F, -5.75F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.1792F, -2.974F, -2.4332F, 0.0F, 0.2618F, 0.0F));
        PartDefinition cube_r25 = bone9.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(2, 41).addBox(-18.3F, 1.6F, -4.775F, 12.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.1792F, -2.974F, -2.4332F, 0.0524F, 0.1702F, -0.0916F));

        this.bone10 = LayerDefinition.create(meshdefinition, 64, 64).bakeRoot().getChild("bone10");
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone10.render(poseStack, buffer, packedLight, packedOverlay);
    }
}