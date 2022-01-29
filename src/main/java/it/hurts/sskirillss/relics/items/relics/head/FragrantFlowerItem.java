package it.hurts.sskirillss.relics.items.relics.head;

import it.hurts.sskirillss.relics.client.particles.spark.SparkTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class FragrantFlowerItem extends RelicItem<FragrantFlowerItem.Stats> {
    public static FragrantFlowerItem INSTANCE;

    public FragrantFlowerItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#f95aa7", "#910d61")
                .ability(AbilityTooltip.builder()
                        .arg(stats.luringRadius)
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg(stats.healingMultiplier)
                        .arg(stats.luringRadius)
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
        if (!(livingEntity instanceof Player player) || DurabilityUtils.isBroken(stack))
            return;

        Level world = player.getCommandSenderWorld();
        Random random = world.getRandom();

        if (player.tickCount % 5 != 0)
            return;

        List<Bee> bees = world.getEntitiesOfClass(Bee.class,
                player.getBoundingBox().inflate(stats.luringRadius, stats.luringRadius, stats.luringRadius));

        if (!bees.isEmpty() && world.isClientSide())
            CuriosApi.getCuriosHelper().getCuriosHandler(player).map(curios -> curios.getStacksHandler(identifier).map(handler -> {
                if (!handler.getStacks().getStackInSlot(index).isEmpty() && handler.getRenders().get(index))
                    world.addParticle(new SparkTintData(new Color(255, 255 - random.nextInt(50), 0), 0.2F, 30),
                            player.position().x(), player.getEyeY() + 0.25F, player.position().z(),
                            MathUtils.randomFloat(random) * 0.01F, 0, MathUtils.randomFloat(random) * 0.01F);

                return true;
            }).orElse(false));

        for (Bee bee : bees) {
            if (bee.getPersistentAngerTarget() == null || !bee.getPersistentAngerTarget().equals(player.getUUID()))
                continue;

            bee.setLastHurtByMob(null);
            bee.setPersistentAngerTarget(null);
            bee.setTarget(null);
            bee.setRemainingPersistentAngerTime(0);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class FragrantFlowerServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;

            if (event.getSource().getEntity() instanceof Player player) {
                LivingEntity target = event.getEntityLiving();

                if (target != null)
                    return;

                if (EntityUtils.findEquippedCurio(player, ItemRegistry.FRAGRANT_FLOWER.get()).isEmpty())
                    return;

                for (Bee bee : player.getCommandSenderWorld().getEntitiesOfClass(Bee.class, player.getBoundingBox()
                        .inflate(stats.luringRadius, stats.luringRadius, stats.luringRadius))) {
                    bee.setLastHurtByMob(target);
                    bee.setPersistentAngerTarget(target.getUUID());
                    bee.setTarget(target);
                    bee.setRemainingPersistentAngerTime(TimeUtil.rangeOfSeconds(20, 39)
                            .sample(bee.getCommandSenderWorld().getRandom()));
                }
            } else if (event.getSource().getEntity() instanceof Bee) {
                Bee bee = (Bee) event.getSource().getEntity();

                for (Player player : bee.getCommandSenderWorld().getEntitiesOfClass(Player.class, bee.getBoundingBox()
                        .inflate(stats.luringRadius, stats.luringRadius, stats.luringRadius)))
                    if (!EntityUtils.findEquippedCurio(player, ItemRegistry.FRAGRANT_FLOWER.get()).isEmpty())
                        event.setAmount(event.getAmount() * stats.damageMultiplier);
            } else if (event.getEntityLiving() instanceof Player && event.getSource().getEntity() instanceof LivingEntity) {
                Player player = (Player) event.getEntityLiving();
                LivingEntity source = (LivingEntity) event.getSource().getEntity();

                if (source instanceof Bee)
                    return;

                if (EntityUtils.findEquippedCurio(player, ItemRegistry.FRAGRANT_FLOWER.get()).isEmpty())
                    return;

                for (Bee bee : player.getCommandSenderWorld().getEntitiesOfClass(Bee.class, player.getBoundingBox()
                        .inflate(stats.luringRadius, stats.luringRadius, stats.luringRadius))) {
                    if (bee.getPersistentAngerTarget() != null)
                        continue;

                    bee.setLastHurtByMob(source);
                    bee.setPersistentAngerTarget(source.getUUID());
                    bee.setTarget(source);
                    bee.setRemainingPersistentAngerTime(TimeUtil.rangeOfSeconds(20, 39)
                            .sample(bee.getCommandSenderWorld().getRandom()));
                }
            }
        }

        @SubscribeEvent
        public static void onEntityHeal(LivingHealEvent event) {
            Stats stats = INSTANCE.stats;
            LivingEntity entity = event.getEntityLiving();

            if (EntityUtils.findEquippedCurio(entity, ItemRegistry.FRAGRANT_FLOWER.get()).isEmpty())
                return;

            List<Bee> bees = entity.getCommandSenderWorld().getEntitiesOfClass(Bee.class, entity.getBoundingBox()
                    .inflate(stats.luringRadius, stats.luringRadius, stats.luringRadius));

            if (bees.isEmpty())
                return;

            event.setAmount(event.getAmount() + (event.getAmount() * (bees.size() * stats.healingMultiplier)));
        }
    }

    public static class Stats extends RelicStats {
        public int luringRadius = 16;
        public float damageMultiplier = 3.0F;
        public float healingMultiplier = 0.5F;
    }
}