package it.hurts.sskirillss.relics.items.relics.necklace;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.client.models.items.CurioModel;
import it.hurts.sskirillss.relics.components.DataComponent;
import it.hurts.sskirillss.relics.entities.DeathEssenceEntity;
import it.hurts.sskirillss.relics.entities.LifeEssenceEntity;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
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
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.sync.SyncTargetPacket;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

import static it.hurts.sskirillss.relics.init.DataComponentRegistry.CHARGE;
import static it.hurts.sskirillss.relics.init.DataComponentRegistry.TOGGLED;

public class HolyLocketItem extends RelicItem implements IRenderableCurio {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("belief")
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .build())
                                .icon((player, stack, ability) -> ability + (stack.getOrDefault(TOGGLED, true) ? "_holy" : "_wicked"))
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
                                .stat(StatData.builder("count")
                                        .initialValue(3, 3)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .formatValue(value -> (int) (MathUtils.round(value, 1)))
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
                                .build())
                        .ability(AbilityData.builder("blessing")
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .castPredicate("blessing", (player, stack) -> getCharges(stack) > 0)
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
        if (ability.equals("belief"))
            stack.set(TOGGLED, !stack.getOrDefault(TOGGLED, true));

        if (ability.equals("blessing"))
            setAbilityCooldown(stack, "blessing", getCharges(stack) * 20);
    }

    public List<Monster> gatherMonsters(Player player, ItemStack stack) {
        return player.getCommandSenderWorld().getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(getStatValue(stack, "buffer", "radius")));
    }

    public int getMaxCharges(ItemStack stack) {
        return (int) getStatValue(stack, "buffer", "capacity");
    }

    public void setCharges(ItemStack stack, int amount) {
        stack.set(CHARGE, Mth.clamp(amount, 0, getMaxCharges(stack)));
    }

    public void addCharge(ItemStack stack, int amount) {
        setCharges(stack, getCharges(stack) + amount);
    }

    public int getCharges(ItemStack stack) {
        return stack.getOrDefault(CHARGE, 0);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.getCommandSenderWorld().isClientSide())
            return;

        Level level = player.getCommandSenderWorld();

        if (getAbilityCooldown(stack, "blessing") == 1)
            addCharge(stack, -getCharges(stack));

        if (player.tickCount % 20 != 0)
            return;

        List<Monster> monsters = gatherMonsters(player, stack);

        if (monsters.isEmpty())
            return;

        for (Monster entity : monsters) {
            if (!entity.hurt(level.damageSources().generic(), 1))
                continue;

            addCharge(stack, 1);

            entity.setLastHurtByPlayer(player);
            entity.setRemainingFireTicks(10);

            spreadExperience(player, stack, 1);

            RandomSource random = level.getRandom();

            ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(new Color(200, 150 + random.nextInt(50), random.nextInt(50)), 0.4F, 20, 0.95F),
                    entity.getX(), entity.getY() + entity.getBbHeight() / 2F, entity.getZ(), 10, entity.getBbWidth() / 2F, entity.getBbHeight() / 2F, entity.getBbWidth() / 2F, 0.025F);
        }
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

        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(getTexture(stack)), stack.hasFoil());

        matrixStack.scale(0.5F, 0.5F, 0.5F);

        model.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);

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

    @EventBusSubscriber
    public static class ClientEvents {
        @SubscribeEvent
        public static void onPlayerRender(RenderPlayerEvent.Pre event) {
            AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.HOLY_LOCKET.get());

            if (stack.isEmpty() || !(stack.getItem() instanceof HolyLocketItem relic) || relic.gatherMonsters(player, stack).isEmpty())
                return;

            PoseStack poseStack = event.getPoseStack();

            float partialTicks = event.getPartialTick();

            Minecraft mc = Minecraft.getInstance();

            poseStack.pushPose();

            poseStack.translate(0D, (player.getBbHeight() / 2D) - 0.5D, 0D);
            poseStack.translate(0D, 0.5D, 0D);

            float scale = 4F + ((float) Math.sin((player.tickCount + partialTicks) * 0.05F) * 0.2F);

            poseStack.scale(scale, scale, scale);

            poseStack.mulPose(Axis.YP.rotationDegrees(-mc.getEntityRenderDispatcher().camera.getYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(mc.getEntityRenderDispatcher().camera.getXRot()));

            poseStack.translate(0D, -0.5D, 0D);

            VertexConsumer vertexConsumer = event.getMultiBufferSource().getBuffer(RenderType.entityTranslucentEmissive(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/parts/gloria.png")));

            float minX = -0.5F, maxX = 0.5F;
            float minY = 0.0F, maxY = 1.0F;
            float minU = 0.0F, maxU = 1.0F;
            float minV = 0.0F, maxV = 1.0F;

            float alpha = 0.1F + ((float) Math.sin((player.tickCount + partialTicks) * 0.15F) * 0.035F);

            PoseStack.Pose entry = poseStack.last();

            vertexConsumer.addVertex(entry.pose(), minX, minY, 0F).setColor(1F, 1F, 1F, alpha).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(LightTexture.FULL_BRIGHT, 0).setNormal(entry, 0, 0, 1);
            vertexConsumer.addVertex(entry.pose(), maxX, minY, 0F).setColor(1F, 1F, 1F, alpha).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(LightTexture.FULL_BRIGHT, 0).setNormal(entry, 0, 0, 1);
            vertexConsumer.addVertex(entry.pose(), maxX, maxY, 0F).setColor(1F, 1F, 1F, alpha).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(LightTexture.FULL_BRIGHT, 0).setNormal(entry, 0, 0, 1);
            vertexConsumer.addVertex(entry.pose(), minX, maxY, 0F).setColor(1F, 1F, 1F, alpha).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(LightTexture.FULL_BRIGHT, 0).setNormal(entry, 0, 0, 1);

            poseStack.popPose();
        }
    }

    @EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onPlayerHurt(LivingHurtEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.HOLY_LOCKET.get());

            if (stack.getItem() instanceof HolyLocketItem relic && relic.isAbilityOnCooldown(stack, "blessing")) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLivingHeal(LivingHealEvent event) {
            if (event.getEntity() instanceof Player player) {
                ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.HOLY_LOCKET.get());
                Level level = player.getCommandSenderWorld();

                if (!(stack.getItem() instanceof HolyLocketItem relic) || stack.getOrDefault(TOGGLED, true))
                    return;

                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(relic.getStatValue(stack, "belief", "radius"))).stream()
                        .filter(player::hasLineOfSight).sorted(Comparator.comparing(entity -> entity.position().distanceTo(player.position()))).limit((int) relic.getStatValue(stack, "belief", "count")).toList()) {
                    if (target.getStringUUID().equals(player.getStringUUID()))
                        continue;

                    int amount = (int) Math.max((event.getAmount() * relic.getStatValue(stack, "belief", "amount")), 1);

                    DeathEssenceEntity essence = new DeathEssenceEntity(EntityRegistry.DEATH_ESSENCE.get(), level);

                    essence.setPos(player.position().add(0, player.getBbHeight() / 2, 0));
                    essence.setDirectionChoice(MathUtils.randomFloat(player.getRandom()));
                    essence.setTarget(target);
                    essence.setDamage(amount);

                    level.addFreshEntity(essence);

                    if (!level.isClientSide())
                        ((ServerLevel) level).getChunkSource().broadcastAndSend(player, new ClientboundCustomPayloadPacket(new SyncTargetPacket(essence.getId(), target.getId())));

                    relic.spreadExperience(player, stack, amount);
                    relic.addCharge(stack, 1);
                }
            } else {
                LivingEntity entity = event.getEntity();
                Level level = entity.getCommandSenderWorld();

                for (ServerPlayer playerSearched : level.getEntitiesOfClass(ServerPlayer.class, event.getEntity().getBoundingBox().inflate(32))) {
                    ItemStack stack = EntityUtils.findEquippedCurio(playerSearched, ItemRegistry.HOLY_LOCKET.get());

                    if (!(stack.getItem() instanceof HolyLocketItem relic) || relic.getStatValue(stack, "belief", "radius") < playerSearched.position().distanceTo(event.getEntity().position())
                            || !stack.getOrDefault(TOGGLED, true))
                        continue;

                    int amount = (int) Math.max((event.getAmount() * relic.getStatValue(stack, "belief", "amount")), 0.5);

                    LifeEssenceEntity essence = new LifeEssenceEntity(EntityRegistry.LIFE_ESSENCE.get(), level);

                    essence.setPos(entity.position().add(0, entity.getBbHeight() / 2, 0));
                    essence.setDirectionChoice(MathUtils.randomFloat(playerSearched.getRandom()));
                    essence.setTarget(playerSearched);
                    essence.setHeal(amount);

                    playerSearched.level().addFreshEntity(essence);

                    if (!level.isClientSide())
                        ((ServerLevel) level).getChunkSource().broadcastAndSend(playerSearched, new ClientboundCustomPayloadPacket(new SyncTargetPacket(essence.getId(), playerSearched.getId())));

                    relic.spreadExperience(playerSearched, stack, amount);
                    relic.addCharge(stack, 1);
                }
            }
        }
    }
}