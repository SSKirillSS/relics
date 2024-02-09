package it.hurts.sskirillss.relics.items.relics.necklace;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.sskirillss.relics.client.models.items.CurioModel;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRenderableCurio;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.List;

public class JellyfishNecklaceItem extends RelicItem implements IRenderableCurio {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("unsinkable", RelicAbilityEntry.builder()
                                .maxLevel(0)
                                .build())
                        .ability("shock", RelicAbilityEntry.builder()
                                .active(AbilityCastType.TOGGLEABLE)
                                .stat("damage", RelicAbilityStat.builder()
                                        .initialValue(0.5D, 2.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability("paralysis", RelicAbilityEntry.builder()
                                .requiredLevel(5)
                                .stat("duration", RelicAbilityStat.builder()
                                        .initialValue(0.5D, 1.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 200))
                .styleData(RelicStyleData.builder()
                        .borders("#dc41ff", "#832698")
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        if (player.isEyeInFluid(FluidTags.WATER))
            EntityUtils.applyAttribute(player, stack, ForgeMod.ENTITY_GRAVITY.get(), -1F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        else
            EntityUtils.removeAttribute(player, stack, ForgeMod.ENTITY_GRAVITY.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);

        Level level = player.getCommandSenderWorld();

        if (!player.isSpectator() && isAbilityTicking(stack, "shock")) {
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox())) {
                if (entity == player)
                    continue;

                if (EntityUtils.hurt(entity, level.damageSources().playerAttack(player), (float) getAbilityValue(stack, "shock", "damage"))) {
                    addExperience(player, stack, 1);

                    if (canUseAbility(stack, "paralysis"))
                        entity.addEffect(new MobEffectInstance(EffectRegistry.PARALYSIS.get(), (int) Math.round(getAbilityValue(stack, "paralysis", "duration") * 20), 0));
                }
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttribute(slotContext.entity(), stack, ForgeMod.ENTITY_GRAVITY.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        CurioModel model = getModel(stack);

        matrixStack.pushPose();

        LivingEntity entity = slotContext.entity();

        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        ICurioRenderer.translateIfSneaking(matrixStack, entity);
        ICurioRenderer.rotateIfSneaking(matrixStack, entity);

        ICurioRenderer.followBodyRotations(entity, model);

        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(getTexture(stack)), false, stack.hasFoil());

        matrixStack.scale(0.5F, 0.5F, 0.5F);

        model.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.scale(2F, 2F, 2F);

        matrixStack.popPose();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public LayerDefinition constructLayerDefinition() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0.0F);

        PartDefinition bone = mesh.getRoot().addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-9.6401F, -10.4222F, 0.475F, 16.0F, 7.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(1.6401F, 10.4222F, -4.625F));

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(10, 16).addBox(-0.675F, 0.575F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6109F, -2.1891F, 0.025F, 0.0F, 0.0F, -0.7854F));
        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(10, 16).addBox(0.0F, -2.5F, -0.475F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6109F, -2.1891F, 0.025F, 0.0F, 0.0F, 1.5708F));
        bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(10, 16).addBox(1.5F, -2.25F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6109F, -2.1891F, 0.025F, 0.0F, 0.0F, 0.7854F));
        bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(10, 16).addBox(-1.0104F, 0.5429F, -0.55F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.225F)), PartPose.offsetAndRotation(-1.6047F, -3.2401F, 0.05F, 0.0F, 0.0F, -0.004F));
        bone.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(1, 16).addBox(-1.5104F, -2.4571F, -1.05F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6047F, -2.7401F, 0.05F, 0.0F, 0.0F, -0.004F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public List<String> headParts() {
        return Lists.newArrayList("body");
    }

}