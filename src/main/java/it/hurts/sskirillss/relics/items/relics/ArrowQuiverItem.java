package it.hurts.sskirillss.relics.items.relics;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.renderer.ArrowQuiverModel;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootTables;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
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
                .config(Stats.class)
                .model(new ArrowQuiverModel())
                .loot(RelicLoot.builder()
                        .table(LootTables.VILLAGE_FLETCHER.toString())
                        .chance(0.25F)
                        .build())
                .loot(RelicLoot.builder()
                        .table(EntityType.SKELETON.getDefaultLootTable().toString())
                        .chance(0.05F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg(config.skippedTicks + 1)
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .active()
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        World world = livingEntity.getCommandSenderWorld();

        handleArrow(stack, world);

        handleUse(stack, livingEntity);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
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

        if ((item instanceof BowItem && !config.blacklistedItems.contains(id)) || config.whitelistedItems.contains(id))
            for (int i = 0; i < config.skippedTicks; i++)
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

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onArrowLoose(ArrowLooseEvent event) {
            Stats config = INSTANCE.config;

            if (!(event.getEntityLiving() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            Item item = ItemRegistry.ARROW_QUIVER.get();

            CuriosApi.getCuriosHelper().findEquippedCurio(item, player).ifPresent(triple -> {
                ItemStack stack = triple.getRight();
                World world = player.getCommandSenderWorld();

                if (!NBTUtils.getBoolean(stack, TAG_CHARGED, false))
                    return;

                event.setCanceled(true);

                ItemStack bow = event.getBow();
                ItemStack ammo = player.getProjectile(bow);

                if (ammo.isEmpty())
                    return;

                AbstractArrowEntity projectile = ((ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW)).createArrow(world, ammo, player);

                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) <= 0 && !player.isCreative()) {
                    projectile.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                    ammo.shrink(1);
                }

                projectile.shootFromRotation(player, player.xRot, player.yRot, 0.0F, BowItem.getPowerForTime(event.getCharge()) * 3F, 0F);
                world.addFreshEntity(projectile);

                NBTUtils.setString(stack, TAG_ARROW, projectile.getStringUUID());

                player.getCooldowns().addCooldown(item, config.activeCooldown * 20);

                world.addParticle(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 1, player.getZ(), 0, 0, 0);
                world.playSound(player, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.PLAYERS, 1F, 1F);
                world.playSound(player, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 1F, 1.75F);
            });
        }

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            Stats config = INSTANCE.config;

            if (!(event.getEntity() instanceof ProjectileEntity))
                return;

            ProjectileEntity projectile = (ProjectileEntity) event.getEntity();

            if (!(projectile.getOwner() instanceof PlayerEntity))
                return;

            PlayerEntity owner = (PlayerEntity) projectile.getOwner();

            if (owner == null)
                return;

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ARROW_QUIVER.get(), owner).ifPresent(triple -> {
                ItemStack stack = triple.getRight();
                World world = owner.getCommandSenderWorld();

                if (!event.getEntity().getUUID().toString().equals(NBTUtils.getString(stack, TAG_ARROW, "")))
                    return;

                for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(config.explosionRadius))) {
                    if (world.isClientSide())
                        return;

                    Vector3d motion = entity.position().add(0F, 2.25F, 0F).subtract(projectile.position())
                            .normalize().multiply(1.5F, 1.5F, 1.5F);

                    if (!world.isClientSide() && entity instanceof ServerPlayerEntity)
                        NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x(), motion.y(), motion.z()), (ServerPlayerEntity) entity);
                    else
                        entity.setDeltaMovement(motion);

                    if (!entity.getStringUUID().equals(owner.getStringUUID()))
                        entity.hurt(DamageSource.playerAttack(owner), (float) Math.min(config.maxExplosionDamage,
                                entity.position().distanceTo(owner.position()) * config.explosionDamageMultiplier));
                }

                NBTUtils.setString(stack, TAG_ARROW, "");
                NBTUtils.setBoolean(stack, TAG_CHARGED, false);

                world.playSound(null, projectile.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.PLAYERS, 1F, 1F);
            });
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