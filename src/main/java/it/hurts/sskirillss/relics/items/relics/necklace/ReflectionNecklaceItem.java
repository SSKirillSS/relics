package it.hurts.sskirillss.relics.items.relics.necklace;

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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
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
                    SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 0.5F, 0.75F);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ReflectionNecklaceServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getEntityLiving() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.REFLECTION_NECKLACE.get());

            if (stack.isEmpty())
                return;

            int charges = NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0);

            if (charges <= 0 || !(event.getSource().getEntity() instanceof LivingEntity attacker))
                return;

            if (attacker == player)
                return;

            if (player.position().distanceTo(attacker.position()) < 10) {
                Vec3 motion = attacker.position().subtract(player.position()).normalize().multiply(2F, 1.5F, 2F);

                if (attacker instanceof Player)
                    NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayer) attacker);
                else
                    attacker.setDeltaMovement(motion);

                player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 0.5F, 1.0F);
            }

            NBTUtils.setInt(stack, TAG_CHARGE_AMOUNT, charges - 1);

            attacker.hurt(DamageSource.playerAttack(player), event.getAmount() * stats.reflectedDamageMultiplier);

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            if (!(event.getRayTraceResult() instanceof EntityHitResult))
                return;

            Entity undefinedProjectile = event.getEntity();
            Entity target = ((EntityHitResult) event.getRayTraceResult()).getEntity();

            if (!(target instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.REFLECTION_NECKLACE.get());

            if (stack.isEmpty() || NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0) <= 0)
                return;

            undefinedProjectile.setDeltaMovement(undefinedProjectile.getDeltaMovement().reverse());

            if (undefinedProjectile instanceof AbstractHurtingProjectile projectile) {
                projectile.setOwner(player);

                projectile.xPower *= -1;
                projectile.yPower *= -1;
                projectile.zPower *= -1;
            }

            event.setCanceled(true);

            undefinedProjectile.hurtMarked = true;

            NBTUtils.setInt(stack, TAG_CHARGE_AMOUNT, NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0) - 1);

            player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }

    public static class Stats extends RelicStats {
        public int maxCharges = 5;
        public int timePerCharge = 60;
        public float reflectedDamageMultiplier = 0.5F;
    }
}