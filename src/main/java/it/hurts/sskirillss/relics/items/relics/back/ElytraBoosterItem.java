package it.hurts.sskirillss.relics.items.relics.back;

import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.client.renderer.items.models.ElytraBoosterModel;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(LootTables.END_CITY_TREASURE.toString())
                        .chance(0.05F)
                        .build())
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .ability(AbilityTooltip.builder()
                        .active(Minecraft.getInstance().options.keyShift)
                        .build())
                .build();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        int breath = NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0);

        if (breath > 0)
            tooltip.add(new TranslationTextComponent("tooltip.relics.elytra_booster.tooltip_1", breath));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity) || IRepairableItem.isBroken(stack))
            return;

        PlayerEntity player = (PlayerEntity) livingEntity;

        if (player.isFallFlying())
            accelerate(player, stack);
        else
            collectBreath(player, stack);
    }

    private void accelerate(PlayerEntity player, ItemStack stack) {
        int breath = NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0);

        if (!player.isShiftKeyDown() || breath <= 0)
            return;

        Vector3d look = player.getLookAngle();
        Vector3d motion = player.getDeltaMovement();
        World world = player.getCommandSenderWorld();
        Random random = world.getRandom();

        player.setDeltaMovement(motion.add(look.x * 0.1D + (look.x * config.flySpeed - motion.x) * 0.5D,
                look.y * 0.1D + (look.y * config.flySpeed - motion.y) * 0.5D,
                look.z * 0.1D + (look.z * config.flySpeed - motion.z) * 0.5D));

        world.addParticle(ParticleTypes.DRAGON_BREATH,
                player.getX() + (MathUtils.randomFloat(random) * 0.5F),
                player.getY() + (MathUtils.randomFloat(random) * 0.5F),
                player.getZ() + (MathUtils.randomFloat(random) * 0.5F),
                0, 0, 0);

        if (player.tickCount % 10 == 0)
            NBTUtils.setInt(stack, TAG_BREATH_AMOUNT, breath - 1);
    }

    private void collectBreath(PlayerEntity player, ItemStack stack) {
        World world = player.getCommandSenderWorld();
        int breath = NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0);

        if (breath >= config.breathCapacity)
            return;

        if (world.dimension() == World.END && player.tickCount %
                (config.breathRegenerationCooldown * 20) == 0)
            NBTUtils.setInt(stack, TAG_BREATH_AMOUNT, breath + 1);

        for (AreaEffectCloudEntity cloud : world.getEntitiesOfClass(AreaEffectCloudEntity.class,
                player.getBoundingBox().inflate(config.breathConsumptionRadius))) {
            if (cloud.getParticle() != ParticleTypes.DRAGON_BREATH)
                continue;

            if (player.tickCount % 5 == 0)
                NBTUtils.setInt(stack, TAG_BREATH_AMOUNT, breath + 1);

            if (cloud.getRadius() <= 0)
                cloud.remove();

            cloud.setRadius(cloud.getRadius() - config.breathConsumptionSpeed);

            Vector3d direction = player.position().add(0, 1, 0).subtract(cloud.position()).normalize();
            double distance = player.position().add(0, 1, 0).distanceTo(cloud.position());

            world.addParticle(new CircleTintData(new Color(160, 0, 255),
                            (float) (distance * 0.075F), (int) distance * 5, 0.95F, false),
                    cloud.getX(), cloud.getY(), cloud.getZ(), direction.x * 0.2F, direction.y * 0.2F, direction.z * 0.2F);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new ElytraBoosterModel();
    }

    public static class Stats extends RelicStats {
        public float flySpeed = 1.5F;
        public int breathConsumptionRadius = 10;
        public int breathCapacity = 1000;
        public float breathConsumptionSpeed = 0.02F;
        public int breathRegenerationCooldown = 2;
    }
}