package it.hurts.sskirillss.relics.items.relics.necklace;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.sskirillss.relics.client.models.items.CurioModel;
import it.hurts.sskirillss.relics.entities.LifeEssenceEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
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
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.List;
import java.util.Random;

public class HolyLocketItem extends RelicItem implements IRenderableCurio {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()

                        .ability(AbilityData.builder("belief")
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .build())
                                .stat(StatData.builder("radius")
                                        .initialValue(2D, 6D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("amount")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 3) * 100))
                                        .build())
                                .build())

                        .ability(AbilityData.builder("buffer")
                                .stat(StatData.builder("radius")
                                        .initialValue(2D, 6D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.35D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("capacity")
                                        .initialValue(2D, 5D)
                                        .upgradeModifier(UpgradeOperation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .stat(StatData.builder("chance")
                                        .initialValue(0.025D, 0.075D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, 0.1)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .build())

                        .ability(AbilityData.builder("blessing")
                                .active(CastData.builder()
                                        .type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("radius")
                                        .initialValue(2D, 6D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("amount")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 3) * 100))
                                        .build())
                                .build())
                        .build())

                .leveling(new LevelingData(100, 10, 200))
                .style(StyleData.builder()
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.DESERT)
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (ability.equals("belief")) {
            boolean switchable = NBTUtils.getBoolean(stack, "switchable", true);
            NBTUtils.setBoolean(stack, "switchable", !switchable);
        }

    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) ||
                !((player.tickCount % 80 == 0)))
            return;
        Level level = player.getCommandSenderWorld();
        List<Monster> monsters = level.getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(getAbilityValue(stack, "buffer", "radius")));

        if (!monsters.isEmpty() && level.isClientSide) {
            addCharge(stack, 1);
            spreadExperience(player, stack, monsters.size() / 2);
        }

        for (Monster entities : monsters) {
            entities.hurt(level.damageSources().generic(), 2);
            player.tickCount = 0;
        }

    }

    public int getMaxCharges(ItemStack stack) {
        return (int) getAbilityValue(stack, "buffer", "capacity");
    }

    public void setCharges(ItemStack stack, int amount) {
        NBTUtils.setInt(stack, "buffer", Mth.clamp(amount, 0, getMaxCharges(stack)));
    }

    public void addCharge(ItemStack stack, int amount) {
        if (amount < 0 && new Random().nextFloat() <= getAbilityValue(stack, "buffer", "chance"))
            return;
        setCharges(stack, getCharges(stack) + amount);
    }

    public int getCharges(ItemStack stack) {
        return NBTUtils.getInt(stack, "buffer", 0);
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

        PartDefinition bone = mesh.getRoot().addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 3).addBox(-8.0F, -1.15F, -4.15F, 16.0F, 7.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 1.15F, 0.0F));

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 18).addBox(-2.6096F, -0.8646F, -0.2F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0571F, 6.6447F, -4.9F, 0.0F, 0.0F, 0.2568F));
        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(12, 18).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0877F, 6.2393F, -5.2F, 0.0F, 0.0F, 0.7854F));
        bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0322F, -2.5947F, -0.225F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0571F, 6.6447F, -4.9F, 0.0F, 0.0F, -0.004F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public List<String> headParts() {
        return Lists.newArrayList("body");
    }

    @Mod.EventBusSubscriber
    static class Events {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.HOLY_LOCKET.get());

            if (!(stack.getItem() instanceof HolyLocketItem relic) || !(NBTUtils.getBoolean(stack, "switchable", true)))
                return;
            Level level = player.level();

            if (level.isClientSide())
                return;
            int amount = (int) Math.max((event.getAmount() * relic.getAbilityValue(stack, "belief", "amount")), 1);

            if (event.getAmount() >= 1)
                relic.spreadExperience(player, stack, 1 + amount / 2);

            relic.addCharge(stack, amount);
            event.setAmount(event.getAmount() - amount);

        }

        @SubscribeEvent
        public static void onLivingHeal(LivingHealEvent event) {
            LivingEntity entity = event.getEntity();
            Level level = entity.getCommandSenderWorld();
            Player player = Minecraft.getInstance().player;
            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.HOLY_LOCKET.get());

            if ((NBTUtils.getBoolean(stack, "switchable", true)) || !(stack.getItem() instanceof HolyLocketItem relic))
                return;

            int buffer = relic.getCharges(stack);
            if (entity.getStringUUID().equals(player.getStringUUID()) && buffer >= 1) {
                int bufferMax = relic.getMaxCharges(stack);

                double fillPercentage = (double) buffer / bufferMax;
                int healAmount = (int) Math.ceil(fillPercentage * 2);


                relic.addCharge(stack, -healAmount);

                event.setAmount(event.getAmount() + healAmount);
                return;
            }

            for (LivingEntity creatures : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(relic.getAbilityValue(stack, "belief", "radius")))) {
                if (entity.getStringUUID().equals(creatures.getStringUUID()))
                    continue;

                int amount = (int) Math.max((event.getAmount() * relic.getAbilityValue(stack, "belief", "amount")), 1);

                LifeEssenceEntity essence = new LifeEssenceEntity(creatures, amount);
                essence.setPos(entity.position().add(0, entity.getBbHeight() / 2, 0));
                essence.setOwner(creatures);
                level.addFreshEntity(essence);

                relic.addCharge(stack, amount);
                event.setAmount(event.getAmount() - amount);
                if (event.getAmount() >= 1)
                    relic.spreadExperience(creatures, stack, 1 + amount);

            }
        }

    }
}