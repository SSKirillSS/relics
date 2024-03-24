package it.hurts.sskirillss.relics.items.relics.feet;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.sskirillss.relics.client.models.items.CurioModel;
import it.hurts.sskirillss.relics.client.models.items.SidedCurioModel;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.IRenderableCurio;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.misc.Backgrounds;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.List;

public class AmphibianBootItem extends RelicItem implements IRenderableCurio {
    private static final String TAG_SWIMMING_DURATION = "swimming_duration";
    private static final String TAG_SLIPPING_DURATION = "slipping_duration";

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("swimming")
                                .stat(StatData.builder("speed")
                                        .initialValue(0.005D, 0.01D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> MathUtils.round(value * 100 * 4, 1))
                                        .build())
                                .stat(StatData.builder("duration")
                                        .initialValue(15D, 35D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value / 5, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("slipping")
                                .stat(StatData.builder("speed")
                                        .initialValue(0.005D, 0.01D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value * 100 * 4, 1))
                                        .build())
                                .stat(StatData.builder("duration")
                                        .initialValue(15D, 25D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value / 5, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("gills")
                                .stat(StatData.builder("chance")
                                        .initialValue(0.01D, 0.1D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.35D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .style(StyleData.builder()
                        .background(Backgrounds.AQUATIC)
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.AQUATIC)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        int swimmingDuration = NBTUtils.getInt(stack, TAG_SWIMMING_DURATION, 0);

        if (player.isSwimming()) {
            if (player.tickCount % 20 == 0)
                addExperience(player, stack, 1);

            if (swimmingDuration < getAbilityValue(stack, "swimming", "duration"))
                NBTUtils.setInt(stack, TAG_SWIMMING_DURATION, swimmingDuration + 1);
        } else if (swimmingDuration > 0)
            NBTUtils.setInt(stack, TAG_SWIMMING_DURATION, --swimmingDuration);

        EntityUtils.removeAttribute(player, stack, ForgeMod.SWIM_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (swimmingDuration > 0)
            EntityUtils.applyAttribute(player, stack, ForgeMod.SWIM_SPEED.get(), (float) (swimmingDuration * getAbilityValue(stack, "swimming", "speed")), AttributeModifier.Operation.MULTIPLY_TOTAL);

        int slippingDuration = NBTUtils.getInt(stack, TAG_SLIPPING_DURATION, 0);

        if (player.isSprinting() && player.getLevel().isRainingAt(player.blockPosition()) && !player.isShiftKeyDown() && !player.isInWater() && !player.isInLava()) {
            if (player.tickCount % 20 == 0)
                addExperience(player, stack, 1);

            if (slippingDuration < getAbilityValue(stack, "slipping", "duration") && player.tickCount % 4 == 0)
                NBTUtils.setInt(stack, TAG_SLIPPING_DURATION, slippingDuration + 1);
        } else if (slippingDuration > 0)
            NBTUtils.setInt(stack, TAG_SLIPPING_DURATION, --slippingDuration);

        EntityUtils.removeAttribute(player, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (slippingDuration > 0) {
            EntityUtils.applyAttribute(player, stack, Attributes.MOVEMENT_SPEED, (float) (slippingDuration * getAbilityValue(stack, "slipping", "speed")), AttributeModifier.Operation.MULTIPLY_TOTAL);
            EntityUtils.applyAttribute(player, stack, ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6F, AttributeModifier.Operation.ADDITION);
        } else
            EntityUtils.removeAttribute(player, stack, ForgeMod.STEP_HEIGHT_ADDITION.get(), AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (stack.getItem() == newStack.getItem())
            return;

        EntityUtils.removeAttribute(slotContext.entity(), stack, ForgeMod.SWIM_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
        EntityUtils.removeAttribute(slotContext.entity(), stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL);
        EntityUtils.removeAttribute(slotContext.entity(), stack, ForgeMod.STEP_HEIGHT_ADDITION.get(), AttributeModifier.Operation.ADDITION);
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.UNCOMMON;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public CurioModel getModel(ItemStack stack) {
        return new SidedCurioModel(stack.getItem());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        CurioModel model = getModel(stack);

        if (!(model instanceof SidedCurioModel sidedModel))
            return;

        sidedModel.setSlot(slotContext.index());

        matrixStack.pushPose();

        LivingEntity entity = slotContext.entity();

        sidedModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        sidedModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        ICurioRenderer.followBodyRotations(entity, sidedModel);

        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(getTexture(stack)), false, stack.hasFoil());

        matrixStack.translate(0, 0, -0.025F);

        sidedModel.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

        matrixStack.popPose();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public LayerDefinition constructLayerDefinition() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0.0F);

        PartDefinition rightLeg = mesh.getRoot().addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 9).addBox(-2.9F, 5.5F, -2.5F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 1).addBox(-2.9F, 5.5F, -2.5F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.175F))
                .texOffs(18, 9).addBox(-2.9F, 9.5F, -4.5F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.5F));

        rightLeg.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, -1).addBox(1.5F, 6.5F, 4.4F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, 0.0F));
        rightLeg.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, -1).mirror().addBox(-1.325F, 6.5F, 4.2F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3927F, 0.0F));

        PartDefinition leftLeg = mesh.getRoot().addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 9).addBox(-2.9F, 5.5F, -2.5F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 1).addBox(-2.9F, 5.5F, -2.5F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.175F))
                .texOffs(18, 9).addBox(-2.9F, 9.5F, -4.5F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.5F));

        leftLeg.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, -1).addBox(1.5F, 6.5F, 4.4F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, 0.0F));
        leftLeg.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, -1).mirror().addBox(-1.325F, 6.5F, 4.2F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3927F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public List<String> bodyParts() {
        return Lists.newArrayList("right_leg", "left_leg");
    }

    public static boolean canBreathe(final LivingEntity entity) {
        ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.AMPHIBIAN_BOOT.get());

        if (!(stack.getItem() instanceof IRelicItem relic))
            return false;

        return entity.getRandom().nextDouble() <= relic.getAbilityValue(stack, "gills", "chance");
    }
}