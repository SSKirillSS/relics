package it.hurts.sskirillss.relics.items.relics.back;

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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

    private void handleArrow(ItemStack stack, Level world) {
        if (world.isClientSide())
            return;

        String UUIDString = NBTUtils.getString(stack, TAG_ARROW, "");
        ServerLevel serverLevel = (ServerLevel) world;

        if (UUIDString.equals(""))
            return;

        Entity arrow = serverLevel.getEntity(UUID.fromString(UUIDString));

        if (arrow == null || !arrow.isAlive()) {
            NBTUtils.setString(stack, TAG_ARROW, "");
            NBTUtils.setBoolean(stack, TAG_CHARGED, false);

            return;
        }

        serverLevel.sendParticles(ParticleTypes.CLOUD, arrow.getX(), arrow.getY(), arrow.getZ(), 1, 0, 0, 0, 0);
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
    public void castAbility(Player player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(stack.getItem()))
            return;

        player.getCommandSenderWorld().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.PLAYERS, 1.0F, 1.0F);

        NBTUtils.setBoolean(stack, TAG_CHARGED, !NBTUtils.getBoolean(stack, TAG_CHARGED, false));
        NBTUtils.setString(stack, TAG_ARROW, "");
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onArrowLoose(ArrowLooseEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getEntityLiving() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.ARROW_QUIVER.get());

            if (stack.isEmpty())
                return;

            Level world = player.getCommandSenderWorld();

            if (!NBTUtils.getBoolean(stack, TAG_CHARGED, false))
                return;

            event.setCanceled(true);

            ItemStack bow = event.getBow();
            ItemStack ammo = player.getProjectile(bow);

            if (ammo.isEmpty())
                return;

            AbstractArrow projectile = ((ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW)).createArrow(world, ammo, player);

            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) <= 0 && !player.isCreative()) {
                projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                ammo.shrink(1);
            }

            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, BowItem.getPowerForTime(event.getCharge()) * 3F, 0F);
            world.addFreshEntity(projectile);

            NBTUtils.setString(stack, TAG_ARROW, projectile.getStringUUID());

            player.getCooldowns().addCooldown(stack.getItem(), stats.activeCooldown * 20);

            world.addParticle(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 1, player.getZ(), 0, 0, 0);
            world.playSound(player, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1F, 1F);
            world.playSound(player, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1F, 1.75F);
        }

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getEntity() instanceof Projectile projectile)
                    || !(projectile.getOwner() instanceof Player owner))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(owner, ItemRegistry.ARROW_QUIVER.get());

            if (stack.isEmpty())
                return;

            Level world = owner.getCommandSenderWorld();

            if (DurabilityUtils.isBroken(stack) || !event.getEntity().getUUID().toString().equals(NBTUtils.getString(stack, TAG_ARROW, "")))
                return;

            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(stats.explosionRadius))) {
                if (world.isClientSide())
                    return;

                Vec3 motion = entity.position().add(0F, 2.25F, 0F).subtract(projectile.position())
                        .normalize().multiply(1.5F, 1.5F, 1.5F);

                if (!world.isClientSide() && entity instanceof ServerPlayer)
                    NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x(), motion.y(), motion.z()), (ServerPlayer) entity);
                else
                    entity.setDeltaMovement(motion);

                if (!entity.getStringUUID().equals(owner.getStringUUID()))
                    entity.hurt(DamageSource.playerAttack(owner), (float) Math.min(stats.maxExplosionDamage,
                            entity.position().distanceTo(owner.position()) * stats.explosionDamageMultiplier));
            }

            NBTUtils.setString(stack, TAG_ARROW, "");
            NBTUtils.setBoolean(stack, TAG_CHARGED, false);

            world.playSound(null, projectile.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1F, 1F);
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