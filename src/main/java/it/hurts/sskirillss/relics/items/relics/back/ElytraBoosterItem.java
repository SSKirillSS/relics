package it.hurts.sskirillss.relics.items.relics.back;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class ElytraBoosterItem extends RelicItem<ElytraBoosterItem.Stats> implements ICurioItem {
    public static final String TAG_BREATH_AMOUNT = "breath";

    public ElytraBoosterItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#b8b8d6", "#6e6e8f")
                .ability(AbilityTooltip.builder()
                        .active(Minecraft.getInstance().options.keyShift)
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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        int breath = NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0);

        if (breath > 0)
            tooltip.add(new TranslatableComponent("tooltip.relics.elytra_booster.tooltip_1", breath));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof Player player) || DurabilityUtils.isBroken(stack))
            return;

        if (player.isFallFlying())
            accelerate(player, stack);
        else
            collectBreath(player, stack);
    }

    private void accelerate(Player player, ItemStack stack) {
        int breath = NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0);

        if (!player.isShiftKeyDown() || breath <= 0)
            return;

        Vec3 look = player.getLookAngle();
        Vec3 motion = player.getDeltaMovement();
        Level world = player.getCommandSenderWorld();
        Random random = world.getRandom();

        player.setDeltaMovement(motion.add(look.x * 0.1D + (look.x * stats.flySpeed - motion.x) * 0.5D,
                look.y * 0.1D + (look.y * stats.flySpeed - motion.y) * 0.5D,
                look.z * 0.1D + (look.z * stats.flySpeed - motion.z) * 0.5D));

        world.addParticle(ParticleTypes.DRAGON_BREATH,
                player.getX() + (MathUtils.randomFloat(random) * 0.5F),
                player.getY() + (MathUtils.randomFloat(random) * 0.5F),
                player.getZ() + (MathUtils.randomFloat(random) * 0.5F),
                0, 0, 0);

        if (player.tickCount % 10 == 0)
            NBTUtils.setInt(stack, TAG_BREATH_AMOUNT, breath - 1);
    }

    private void collectBreath(Player player, ItemStack stack) {
        Level world = player.getCommandSenderWorld();
        int breath = NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0);

        if (breath >= stats.breathCapacity)
            return;

        if (world.dimension() == Level.END && player.tickCount %
                (stats.breathRegenerationCooldown * 20) == 0)
            NBTUtils.setInt(stack, TAG_BREATH_AMOUNT, breath + 1);

        for (AreaEffectCloud cloud : world.getEntitiesOfClass(AreaEffectCloud.class,
                player.getBoundingBox().inflate(stats.breathConsumptionRadius))) {
            if (cloud.getParticle() != ParticleTypes.DRAGON_BREATH)
                continue;

            if (player.tickCount % 5 == 0)
                NBTUtils.setInt(stack, TAG_BREATH_AMOUNT, breath + 1);

            if (cloud.getRadius() <= 0)
                cloud.remove(Entity.RemovalReason.KILLED);

            cloud.setRadius(cloud.getRadius() - stats.breathConsumptionSpeed);

            Vec3 direction = player.position().add(0, 1, 0).subtract(cloud.position()).normalize();
            double distance = player.position().add(0, 1, 0).distanceTo(cloud.position());

            world.addParticle(new CircleTintData(new Color(160, 0, 255),
                            (float) (distance * 0.075F), (int) distance * 5, 0.95F, false),
                    cloud.getX(), cloud.getY(), cloud.getZ(), direction.x * 0.2F, direction.y * 0.2F, direction.z * 0.2F);
        }
    }

    public static class Stats extends RelicStats {
        public float flySpeed = 1.5F;
        public int breathConsumptionRadius = 10;
        public int breathCapacity = 1000;
        public float breathConsumptionSpeed = 0.02F;
        public int breathRegenerationCooldown = 2;
    }
}