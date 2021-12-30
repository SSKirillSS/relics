package it.hurts.sskirillss.relics.items.relics.necklace;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.client.renderer.items.models.ReflectionNecklaceModel;
import it.hurts.sskirillss.relics.client.renderer.items.models.ReflectionNecklaceShieldModel;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicLootData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ReflectionNecklaceItem extends RelicItem<ReflectionNecklaceItem.Stats> {
    public static final String TAG_CHARGE_AMOUNT = "charges";
    public static final String TAG_UPDATE_TIME = "time";

    public static ReflectionNecklaceItem INSTANCE;

    public ReflectionNecklaceItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#004463", "#01144b")
                .ability(AbilityTooltip.builder()
                        .arg(stats.maxCharges)
                        .arg((int) (stats.reflectedDamageMultiplier * 100) + "%")
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .loot(RelicLootData.builder()
                        .table(RelicUtils.Worldgen.NETHER)
                        .chance(0.01F)
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (DurabilityUtils.isBroken(stack) || livingEntity.tickCount % 20 != 0)
            return;

        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
        int charges = NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0);

        if (charges >= stats.maxCharges)
            return;

        if (time < Math.max(stats.timePerCharge, charges * stats.timePerCharge))
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
        else {
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
            NBTUtils.setInt(stack, TAG_CHARGE_AMOUNT, charges + 1);

            livingEntity.getCommandSenderWorld().playSound(null, livingEntity.blockPosition(),
                    SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 0.5F, 0.75F);
        }
    }

    private final ResourceLocation SHIELD_TEXTURE = new ResourceLocation(Reference.MODID, "textures/items/models/reflection_necklace_shield.png");

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        super.render(identifier, index, matrixStack, renderTypeBuffer, light, livingEntity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, stack);

        int charges = NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0);
        ReflectionNecklaceShieldModel shieldModel = new ReflectionNecklaceShieldModel();

        if (charges > 0)
            for (int i = 0; i < charges; i++) {
                matrixStack.pushPose();

                matrixStack.scale(0.5F, 0.5F, 0.5F);
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees((MathHelper.cos(livingEntity.tickCount / 10.0F) / 7.0F) * (180F / (float) Math.PI)));
                matrixStack.mulPose(Vector3f.YP.rotationDegrees((livingEntity.tickCount / 10.0F) * (180F / (float) Math.PI) + (i * (360F / charges))));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees((MathHelper.sin(livingEntity.tickCount / 10.0F) / 7.0F) * (180F / (float) Math.PI)));
                matrixStack.translate(0.0F, 1.5F, 1F + charges * 0.3F);
                shieldModel.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityTranslucent(SHIELD_TEXTURE)),
                        light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                matrixStack.popPose();
            }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new ReflectionNecklaceModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ReflectionNecklaceServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getEntityLiving() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.REFLECTION_NECKLACE.get());

            if (stack.isEmpty())
                return;

            int charges = NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0);

            if (charges <= 0 || !(event.getSource().getEntity() instanceof LivingEntity))
                return;

            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();

            if (attacker == null || attacker == player)
                return;

            if (player.position().distanceTo(attacker.position()) < 10) {
                Vector3d motion = attacker.position().subtract(player.position()).normalize().multiply(2F, 1.5F, 2F);

                if (attacker instanceof PlayerEntity)
                    NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayerEntity) attacker);
                else
                    attacker.setDeltaMovement(motion);

                player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WITHER_BREAK_BLOCK, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }

            NBTUtils.setInt(stack, TAG_CHARGE_AMOUNT, charges - 1);

            attacker.hurt(DamageSource.playerAttack(player), event.getAmount() * stats.reflectedDamageMultiplier);

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            if (!(event.getRayTraceResult() instanceof EntityRayTraceResult))
                return;

            Entity undefinedProjectile = event.getEntity();
            Entity target = ((EntityRayTraceResult) event.getRayTraceResult()).getEntity();

            if (!(target instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) target;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.REFLECTION_NECKLACE.get());

            if (stack.isEmpty() || NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0) <= 0)
                return;

            undefinedProjectile.setDeltaMovement(undefinedProjectile.getDeltaMovement().reverse());

            if (undefinedProjectile instanceof DamagingProjectileEntity) {
                DamagingProjectileEntity projectile = (DamagingProjectileEntity) undefinedProjectile;

                projectile.setOwner(player);

                projectile.xPower *= -1;
                projectile.yPower *= -1;
                projectile.zPower *= -1;
            }

            event.setCanceled(true);

            undefinedProjectile.hurtMarked = true;

            NBTUtils.setInt(stack, TAG_CHARGE_AMOUNT, NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0) - 1);

            player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WITHER_BREAK_BLOCK, SoundCategory.PLAYERS, 0.5F, 1.0F);
        }
    }

    public static class Stats extends RelicStats {
        public int maxCharges = 5;
        public int timePerCharge = 60;
        public float reflectedDamageMultiplier = 0.5F;
    }
}