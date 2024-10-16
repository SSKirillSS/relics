package it.hurts.sskirillss.relics.items.relics.feet;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.sskirillss.relics.client.models.items.CurioModel;
import it.hurts.sskirillss.relics.client.models.items.SidedCurioModel;
import it.hurts.sskirillss.relics.init.BlockRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRenderableCurio;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.WorldUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.List;

import static it.hurts.sskirillss.relics.init.DataComponentRegistry.TIME;
import static it.hurts.sskirillss.relics.init.DataComponentRegistry.TOGGLED;

public class PhantomBootItem extends RelicItem implements IRenderableCurio {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("bridge")
                                .active(CastData.builder()
                                        .type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("duration")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(0.25D, 1D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 200))
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xff5b0591)
                                .borderBottom(0xff3d0068)
                                .textured(true)
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.END)
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        var level = player.level();

        if (ability.equals("bridge")) {
            if (stage == CastStage.START) {
                Vec3 motion = player.getDeltaMovement();

                if (motion.y <= -0.5D)
                    player.setDeltaMovement(motion.x, -motion.y, motion.z);
            }

            if (!level.isClientSide() && stage == CastStage.TICK && isToggled(stack)) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        var relativePos = player.blockPosition().offset(x, -1, z);

                        if (!level.isEmptyBlock(relativePos))
                            continue;

                        level.setBlockAndUpdate(relativePos, BlockRegistry.PHANTOM_BLOCK.get().defaultBlockState());
                    }
                }
            }
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        var level = player.getCommandSenderWorld();

        if (level.isClientSide())
            return;

        var block = BlockRegistry.PHANTOM_BLOCK.get();

        var onBridge = level.getBlockState(player.blockPosition().atY((int) Math.floor(WorldUtils.getGroundHeight(player, player.position(), 8)))).getBlock() == block
                || player.isColliding(player.blockPosition(), block.defaultBlockState());

        var time = getTime(stack);

        if (isToggled(stack)) {
            if (onBridge) {
                if (player.getKnownMovement().multiply(1F, 0F, 1F).length() > 0) {
                    if (time > 0)
                        addTime(stack, -1);
                } else {
                    if (time < getMaxTime(stack))
                        addTime(stack, 1);
                    else setToggled(stack, false);
                }
            } else if (time > 0)
                addTime(stack, -1);
        } else {
            if (player.onGround() && !onBridge)
                setToggled(stack, true);
        }
    }

    public int getMaxTime(ItemStack stack) {
        return (int) Math.round(getStatValue(stack, "bridge", "duration") * 20D);
    }

    public int getTime(ItemStack stack) {
        return stack.getOrDefault(TIME, 0);
    }

    public void setTime(ItemStack stack, int time) {
        stack.set(TIME, Mth.clamp(time, 0, getMaxTime(stack)));
    }

    public void addTime(ItemStack stack, int time) {
        setTime(stack, getTime(stack) + time);
    }

    public boolean isToggled(ItemStack stack) {
        return stack.getOrDefault(TOGGLED, true);
    }

    public void setToggled(ItemStack stack, boolean toggled) {
        stack.set(TOGGLED, toggled);
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

        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(getTexture(stack)), stack.hasFoil());

        sidedModel.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);

        matrixStack.popPose();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public LayerDefinition constructLayerDefinition() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0.0F);

        PartDefinition right_leg = mesh.getRoot().addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.6F, 5.0F, -2.0F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.2F))
                .texOffs(20, 0).addBox(-2.6F, 9.0F, -5.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.205F))
                .texOffs(0, 12).addBox(-2.6F, 5.0F, -2.0F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.4F))
                .texOffs(20, 7).addBox(-2.6F, 9.0F, -5.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.4F)), PartPose.offset(-2.4F, 12.0F, 2.0F));

        PartDefinition left_leg = mesh.getRoot().addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.6F, 5.0F, -2.0F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.2F))
                .texOffs(20, 0).addBox(-2.6F, 9.0F, -5.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.205F))
                .texOffs(0, 12).addBox(-2.6F, 5.0F, -2.0F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.4F))
                .texOffs(20, 7).addBox(-2.6F, 9.0F, -5.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.4F)), PartPose.offset(-2.4F, 12.0F, 2.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public List<String> bodyParts() {
        return Lists.newArrayList("right_leg", "left_leg");
    }
}