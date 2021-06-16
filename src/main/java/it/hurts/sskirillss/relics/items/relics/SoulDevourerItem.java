package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.List;

public class SoulDevourerItem extends RelicItem<SoulDevourerItem.Stats> implements ICurioItem {
    private static final String TAG_UPDATE_TIME = "time";
    private static final String TAG_SOUL_AMOUNT = "soul";
    private static final String TAG_EXPLOSION_READINESS = "readiness";

    public static SoulDevourerItem INSTANCE;

    public SoulDevourerItem() {
        super(Rarity.EPIC);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.soul_devourer.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.soul_devourer.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("tooltip.relics.soul_devourer.tooltip_1", NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0)));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity)) return;
        int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
        int readiness = NBTUtils.getInt(stack, TAG_EXPLOSION_READINESS, 0);
        PlayerEntity player = (PlayerEntity) livingEntity;
        if (player.tickCount % 20 == 0 && soul > 0) {
            if (time < config.soulLooseCooldown * (player.isShiftKeyDown() ? 2 : 1))
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
            else {
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, Math.round(Math.max(soul - (soul
                        * config.soulLoseMultiplierPerSoul + config.minSoulLooseAmount), 0)));
            }
        }
        if (soul < config.soulForExplosion || player.getCooldowns().isOnCooldown(stack.getItem())) return;
        if (player.isShiftKeyDown()) {
            if (readiness < config.explosionPreparation * 20) {
                NBTUtils.setInt(stack, TAG_EXPLOSION_READINESS, readiness + 1);
                float radius = (float) Math.sin(readiness * 0.1) + 1.0F + (readiness * 0.002F);
                for (int i = 0; i < 5; i++) {
                    float angle = (0.0105F * (readiness * 4 + i * 120));
                    double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle))) + player.getX();
                    double extraZ = (double) (radius * MathHelper.cos(angle)) + player.getZ();
                    player.getCommandSenderWorld().addParticle(new CircleTintData(
                                    new Color(180, 250, 255), 0.2F + (readiness * 0.004F), 40, 0.95F, false),
                            extraX, player.getY() + 0.5F, extraZ, 0F, 0F, 0F);
                }
            } else explode(player, stack, soul);
        } else if (readiness != 0) NBTUtils.setInt(stack, TAG_EXPLOSION_READINESS, 0);
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.NETHER;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    public static void explode(PlayerEntity player, ItemStack stack, int soul) {
        Stats config = INSTANCE.config;
        for (LivingEntity entity : player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(config.explosionRadius))) {
            if (entity == player) continue;
            double velocity = config.explosionKnockback;
            Vector3d motion = entity.position().add(0.0F, 1.0F, 0.0F)
                    .subtract(player.position()).normalize().multiply(velocity, velocity, velocity);
            if (entity instanceof ServerPlayerEntity) NetworkHandler.sendToClient(
                    new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayerEntity) entity);
            else entity.setDeltaMovement(motion);
            entity.hurt(DamageSource.playerAttack(player), (config.minExplosionDamage + (soul * config.explosionDamagePerSoul)));
        }
        ParticleUtils.createBall(new CircleTintData(new Color(0.3F, 0.7F, 1.0F),
                        0.5F, 50, 0.95F, false), player.position(),
                player.getCommandSenderWorld(), 4, 0.2F);
        player.getCooldowns().addCooldown(stack.getItem(), config.explosionCooldown * 20);
        NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, 0);
        NBTUtils.setInt(stack, TAG_EXPLOSION_READINESS, 0);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SoulDevourerServerEvents {
        @SubscribeEvent
        public static void onEntityDeath(LivingDeathEvent event) {
            Stats config = INSTANCE.config;
            if (!(event.getSource().getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            LivingEntity target = event.getEntityLiving();
            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SOUL_DEVOURER.get(), player).ifPresent(triple -> {
                ItemStack stack = triple.getRight();
                int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
                int capacity = config.soulCapacity;
                if (player.getCooldowns().isOnCooldown(stack.getItem()) || soul >= capacity) return;
                NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, Math.min(soul + Math.round(target.getMaxHealth()
                        * config.soulFromHealthPercentage), capacity));
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
            });
        }

        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            if (!(event.getSource().getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SOUL_DEVOURER.get(), player).ifPresent(triple -> {
                int soul = NBTUtils.getInt(triple.getRight(), TAG_SOUL_AMOUNT, 0);
                if (soul > 0) event.setAmount((float) (event.getAmount() + (soul * config.playerDamageMultiplierPerSoul)));
            });
        }
    }

    public static class Stats extends RelicStats {
        public int soulLooseCooldown = 10;
        public int minSoulLooseAmount = 5;
        public float soulLoseMultiplierPerSoul = 0.1F;
        public int soulForExplosion = 50;
        public int explosionPreparation = 12;
        public int explosionRadius = 10;
        public float explosionKnockback = 5.0F;
        public int minExplosionDamage = 2;
        public float explosionDamagePerSoul = 0.75F;
        public int explosionCooldown = 60;
        public int soulCapacity = 100;
        public float soulFromHealthPercentage = 0.25F;
        public float playerDamageMultiplierPerSoul = 0.1F;
    }
}