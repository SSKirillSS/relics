package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.FragrantFlowerModel;
import it.hurts.sskirillss.relics.particles.spark.SparkTintData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.List;

public class FragrantFlowerItem extends RelicItem<FragrantFlowerItem.Stats> implements ICurioItem {
    public static FragrantFlowerItem INSTANCE;

    public FragrantFlowerItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(LootTables.JUNGLE_TEMPLE.toString())
                        .chance(0.2F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .arg(config.luringRadius)
                        .build())
                .shift(ShiftTooltip.builder()
                        .arg(config.healingMultiplier)
                        .arg(config.luringRadius)
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity) || IRepairableItem.isBroken(stack))
            return;

        PlayerEntity player = (PlayerEntity) livingEntity;
        World world = player.getCommandSenderWorld();

        if (player.tickCount % 5 != 0)
            return;

        List<BeeEntity> bees = world.getEntitiesOfClass(BeeEntity.class,
                player.getBoundingBox().inflate(config.luringRadius, config.luringRadius, config.luringRadius));

        if (!bees.isEmpty() && world.isClientSide())
            CuriosApi.getCuriosHelper().getCuriosHandler(player).map(curios -> curios.getStacksHandler(identifier).map(handler -> {
                if (!handler.getStacks().getStackInSlot(index).isEmpty() && handler.getRenders().get(index))
                    world.addParticle(new SparkTintData(new Color(255, 255 - random.nextInt(50), 0), 0.2F, 30),
                            player.position().x(), player.getEyeY() + 0.25F, player.position().z(),
                            MathUtils.randomFloat(random) * 0.01F, 0, MathUtils.randomFloat(random) * 0.01F);

                return true;
            }).orElse(false));

        for (BeeEntity bee : bees) {
            if (bee.getPersistentAngerTarget() == null || !bee.getPersistentAngerTarget().equals(player.getUUID()))
                continue;

            bee.setLastHurtByMob(null);
            bee.setPersistentAngerTarget(null);
            bee.setTarget(null);
            bee.setRemainingPersistentAngerTime(0);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new FragrantFlowerModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class FragrantFlowerServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingHurtEvent event) {
            Stats config = INSTANCE.config;

            if (event.getSource().getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                LivingEntity target = event.getEntityLiving();

                if (target instanceof BeeEntity)
                    return;

                if (EntityUtils.findEquippedCurio(player, ItemRegistry.FRAGRANT_FLOWER.get()).isEmpty())
                    return;

                for (BeeEntity bee : player.getCommandSenderWorld().getEntitiesOfClass(BeeEntity.class, player.getBoundingBox()
                        .inflate(config.luringRadius, config.luringRadius, config.luringRadius))) {
                    bee.setLastHurtByMob(target);
                    bee.setPersistentAngerTarget(target.getUUID());
                    bee.setTarget(target);
                    bee.setRemainingPersistentAngerTime(TickRangeConverter.rangeOfSeconds(20, 39)
                            .randomValue(bee.getCommandSenderWorld().getRandom()));
                }
            } else if (event.getSource().getEntity() instanceof BeeEntity) {
                BeeEntity bee = (BeeEntity) event.getSource().getEntity();

                for (PlayerEntity player : bee.getCommandSenderWorld().getEntitiesOfClass(PlayerEntity.class, bee.getBoundingBox()
                        .inflate(config.luringRadius, config.luringRadius, config.luringRadius)))
                    if (!EntityUtils.findEquippedCurio(player, ItemRegistry.FRAGRANT_FLOWER.get()).isEmpty())
                        event.setAmount(event.getAmount() * config.damageMultiplier);
            } else if (event.getEntityLiving() instanceof PlayerEntity && event.getSource().getEntity() instanceof LivingEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                LivingEntity source = (LivingEntity) event.getSource().getEntity();

                if (source instanceof BeeEntity)
                    return;

                if (EntityUtils.findEquippedCurio(player, ItemRegistry.FRAGRANT_FLOWER.get()).isEmpty())
                    return;

                for (BeeEntity bee : player.getCommandSenderWorld().getEntitiesOfClass(BeeEntity.class, player.getBoundingBox()
                        .inflate(config.luringRadius, config.luringRadius, config.luringRadius))) {
                    if (bee.getPersistentAngerTarget() != null)
                        continue;

                    bee.setLastHurtByMob(source);
                    bee.setPersistentAngerTarget(source.getUUID());
                    bee.setTarget(source);
                    bee.setRemainingPersistentAngerTime(TickRangeConverter.rangeOfSeconds(20, 39)
                            .randomValue(bee.getCommandSenderWorld().getRandom()));
                }
            }
        }

        @SubscribeEvent
        public static void onEntityHeal(LivingHealEvent event) {
            Stats config = INSTANCE.config;
            LivingEntity entity = event.getEntityLiving();

            if (EntityUtils.findEquippedCurio(entity, ItemRegistry.FRAGRANT_FLOWER.get()).isEmpty())
                return;

            List<BeeEntity> bees = entity.getCommandSenderWorld().getEntitiesOfClass(BeeEntity.class, entity.getBoundingBox()
                    .inflate(config.luringRadius, config.luringRadius, config.luringRadius));

            if (bees.isEmpty())
                return;

            event.setAmount(event.getAmount() + (event.getAmount() * (bees.size() * config.healingMultiplier)));
        }
    }

    public static class Stats extends RelicStats {
        public int luringRadius = 16;
        public float damageMultiplier = 3.0F;
        public float healingMultiplier = 0.5F;
    }
}