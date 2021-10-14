package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.FragrantFlowerModel;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.TickRangeConverter;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class FragrantFlowerItem extends RelicItem<FragrantFlowerItem.Stats> implements ICurioItem {
    public static FragrantFlowerItem INSTANCE;

    public FragrantFlowerItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .model(new FragrantFlowerModel())
                .loot(RelicLoot.builder()
                        .table(LootTables.JUNGLE_TEMPLE.toString())
                        .chance(0.2F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg(config.luringRadius)
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArgs(config.healingMultiplier, config.luringRadius)
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) livingEntity;

        if (player.tickCount % 5 != 0)
            return;

        for (BeeEntity bee : player.getCommandSenderWorld().getEntitiesOfClass(BeeEntity.class,
                player.getBoundingBox().inflate(config.luringRadius, config.luringRadius, config.luringRadius))) {
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
            Stats config = INSTANCE.config;
            Item item = ItemRegistry.FRAGRANT_FLOWER.get();

            if (event.getSource().getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                LivingEntity target = event.getEntityLiving();

                if (!CuriosApi.getCuriosHelper().findEquippedCurio(item, player).isPresent() || target instanceof BeeEntity)
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
                    if (CuriosApi.getCuriosHelper().findEquippedCurio(item, player).isPresent())
                        event.setAmount(event.getAmount() * config.damageMultiplier);
            } else if (event.getEntityLiving() instanceof PlayerEntity && event.getSource().getEntity() instanceof LivingEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                LivingEntity source = (LivingEntity) event.getSource().getEntity();

                if (!CuriosApi.getCuriosHelper().findEquippedCurio(item, player).isPresent() || source instanceof BeeEntity)
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

            if (!(event.getEntityLiving() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getEntity();
            List<BeeEntity> bees = player.getCommandSenderWorld().getEntitiesOfClass(BeeEntity.class, player.getBoundingBox()
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