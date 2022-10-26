package it.hurts.sskirillss.relics.items.relics.back;

import it.hurts.sskirillss.relics.client.renderer.items.models.ArrowQuiverModel;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArrowQuiverItem extends RelicItem<ArrowQuiverItem.Stats> implements ICurioItem {
    public static ArrowQuiverItem INSTANCE;

    private static final String TAG_CHARGED = "charged";
    private static final String TAG_ARROW = "arrow";

    public ArrowQuiverItem() {
        super(RelicData.builder()
                .rarity(Rarity.UNCOMMON)
                .hasAbility()
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#c87625", "#ab661b")
                .ability(AbilityTooltip.builder()
                        .arg(stats.skippedTicks + 1)
                        .build())
                .ability(AbilityTooltip.builder()
                        .active()
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (DurabilityUtils.isBroken(stack))
            return;

        handleArrow(stack, livingEntity.getCommandSenderWorld());
        handleUse(stack, livingEntity);
    }

    private void handleArrow(ItemStack stack, World world) {
        if (world.isClientSide())
            return;

        String UUIDString = NBTUtils.getString(stack, TAG_ARROW, "");
        ServerWorld serverWorld = (ServerWorld) world;

        if (UUIDString.equals(""))
            return;

        Entity arrow = serverWorld.getEntity(UUID.fromString(UUIDString));

        if (arrow == null || !arrow.isAlive()) {
            NBTUtils.setString(stack, TAG_ARROW, "");
            NBTUtils.setBoolean(stack, TAG_CHARGED, false);

            return;
        }

        serverWorld.sendParticles(ParticleTypes.CLOUD, arrow.getX(), arrow.getY(), arrow.getZ(), 1, 0, 0, 0, 0);
    }

    private void handleUse(ItemStack stack, LivingEntity entity) {
        if (!entity.isUsingItem() || NBTUtils.getBoolean(stack, TAG_CHARGED, false))
            return;

        Item item = entity.getMainHandItem().getItem();
        String id = item.getRegistryName().toString();

        if ((item instanceof BowItem && !stats.blacklistedItems.contains(id)) || stats.whitelistedItems.contains(id))
            for (int i = 0; i < stats.skippedTicks; i++)
                entity.updatingUsingItem();
    }

    @Override
    public void castAbility(PlayerEntity player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(stack.getItem()))
            return;

        player.getCommandSenderWorld().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundCategory.PLAYERS, 1.0F, 1.0F);

        NBTUtils.setBoolean(stack, TAG_CHARGED, !NBTUtils.getBoolean(stack, TAG_CHARGED, false));
        NBTUtils.setString(stack, TAG_ARROW, "");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new ArrowQuiverModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onArrowLoose(ArrowLooseEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getEntityLiving() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.ARROW_QUIVER.get());

            if (stack.isEmpty())
                return;

            World world = player.getCommandSenderWorld();

            if (!NBTUtils.getBoolean(stack, TAG_CHARGED, false))
                return;

            event.setCanceled(true);

            ItemStack bow = event.getBow();
            ItemStack ammo = player.getProjectile(bow);

            if (ammo.isEmpty())
                return;

            AbstractArrowEntity projectile = ((ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW)).createArrow(world, ammo, player);

            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) <= 0 && !player.isCreative())
                ammo.shrink(1);
            else
                projectile.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;

            projectile.shootFromRotation(player, player.xRot, player.yRot, 0.0F, BowItem.getPowerForTime(event.getCharge()) * 3F, 0F);
            world.addFreshEntity(projectile);

            NBTUtils.setString(stack, TAG_ARROW, projectile.getStringUUID());

            player.getCooldowns().addCooldown(stack.getItem(), stats.activeCooldown * 20);

            world.addParticle(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 1, player.getZ(), 0, 0, 0);
            world.playSound(player, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.PLAYERS, 1F, 1F);
            world.playSound(player, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 1F, 1.75F);
        }

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getEntity() instanceof ProjectileEntity))
                return;

            ProjectileEntity projectile = (ProjectileEntity) event.getEntity();

            if (!(projectile.getOwner() instanceof PlayerEntity))
                return;

            PlayerEntity owner = (PlayerEntity) projectile.getOwner();

            if (owner == null)
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(owner, ItemRegistry.ARROW_QUIVER.get());

            if (stack.isEmpty())
                return;

            World world = owner.getCommandSenderWorld();

            if (DurabilityUtils.isBroken(stack) || !event.getEntity().getUUID().toString().equals(NBTUtils.getString(stack, TAG_ARROW, "")))
                return;

            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(stats.explosionRadius))) {
                if (world.isClientSide())
                    return;

                Vector3d motion = entity.position().add(0F, 2.25F, 0F).subtract(projectile.position())
                        .normalize().multiply(1.5F, 1.5F, 1.5F);

                if (!world.isClientSide() && entity instanceof ServerPlayerEntity)
                    NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x(), motion.y(), motion.z()), (ServerPlayerEntity) entity);
                else
                    entity.setDeltaMovement(motion);

                if (!entity.getStringUUID().equals(owner.getStringUUID()))
                    entity.hurt(DamageSource.playerAttack(owner), (float) Math.min(stats.maxExplosionDamage,
                            entity.position().distanceTo(owner.position()) * stats.explosionDamageMultiplier));
            }

            NBTUtils.setString(stack, TAG_ARROW, "");
            NBTUtils.setBoolean(stack, TAG_CHARGED, false);

            world.playSound(null, projectile.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.PLAYERS, 1F, 1F);
        }
    }

    public static class Stats extends RelicStats {
        public int skippedTicks = 1;
        public int activeCooldown = 10;
        public int explosionRadius = 5;
        public float explosionDamageMultiplier = 1.5F;
        public int maxExplosionDamage = 75;
        public List<String> whitelistedItems = new ArrayList<>();
        public List<String> blacklistedItems = new ArrayList<>();
    }
}