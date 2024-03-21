package it.hurts.sskirillss.relics.items.relics.feet;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.sskirillss.relics.api.events.common.FluidCollisionEvent;
import it.hurts.sskirillss.relics.client.models.items.CurioModel;
import it.hurts.sskirillss.relics.client.models.items.SidedCurioModel;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
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
import it.hurts.sskirillss.relics.items.relics.base.data.style.misc.Backgrounds;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class AquaWalkerItem extends RelicItem implements IRenderableCurio {
    public static final String TAG_DRENCH = "drench";

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("walking")
                                .stat(StatData.builder("time")
                                        .initialValue(30D, 60D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 0)))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .style(StyleData.builder()
                        .background(Backgrounds.AQUATIC)
                        .borders(0x488376, 0x163d30)
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.AQUATIC)
                        .build())
                .build();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        int drench = NBTUtils.getInt(stack, TAG_DRENCH, 0);

        if (!(entity instanceof Player player) || player.tickCount % 20 != 0)
            return;

        if (drench > 0 && !player.isInWater() && !player.level.getFluidState(player.blockPosition().below()).is(FluidTags.WATER))
            NBTUtils.setInt(stack, TAG_DRENCH, --drench);
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
                .texOffs(32, 0).addBox(-2.9F, 8.0F, -2.5F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.125F))
                .texOffs(0, 1).addBox(-2.9F, 5.5F, -2.5F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.175F))
                .texOffs(18, 9).addBox(-2.9F, 9.5F, -4.5F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.5F));

        rightLeg.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, -1).addBox(1.5F, 6.5F, 4.4F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, 0.0F));
        rightLeg.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, -1).mirror().addBox(-1.325F, 6.5F, 4.2F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3927F, 0.0F));

        PartDefinition leftLeg = mesh.getRoot().addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 9).addBox(-2.9F, 5.5F, -2.5F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-2.9F, 8.0F, -2.5F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.125F))
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

    @SubscribeEvent
    public static void onFluidCollide(FluidCollisionEvent event) {
        ItemStack stack = EntityUtils.findEquippedCurio(event.getEntity(), ItemRegistry.AQUA_WALKER.get());

        if (!(stack.getItem() instanceof IRelicItem relic))
            return;

        int drench = NBTUtils.getInt(stack, TAG_DRENCH, 0);

        if (!(event.getEntity() instanceof Player player) || drench > relic.getAbilityValue(stack, "walking", "time")
                || !event.getFluid().is(FluidTags.WATER) || player.isShiftKeyDown())
            return;

        if (player.tickCount % 20 == 0) {
            NBTUtils.setInt(stack, TAG_DRENCH, ++drench);

            if (drench % 5 == 0)
                relic.addExperience(player, stack, 1);
        }

        event.setCanceled(true);
    }
}