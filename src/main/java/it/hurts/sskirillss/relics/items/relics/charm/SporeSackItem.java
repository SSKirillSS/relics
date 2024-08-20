package it.hurts.sskirillss.relics.items.relics.charm;

import it.hurts.sskirillss.relics.entities.SporeEntity;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Random;

import static it.hurts.sskirillss.relics.init.DataComponentRegistry.CHARGE;

public class SporeSackItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("spore")
                                .maxLevel(10)
                                .stat(StatData.builder("size")
                                        .icon(StatIcons.SIZE)
                                        .initialValue(0.1D, 0.5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(StatData.builder("damage")
                                        .icon(StatIcons.DEALT_DAMAGE)
                                        .initialValue(0.25D, 0.5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(StatData.builder("cooldown")
                                        .icon(StatIcons.COOLDOWN)
                                        .initialValue(15D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("duration")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(2D, 4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("buffer")
                                .requiredLevel(5)
                                .maxLevel(10)
                                .stat(StatData.builder("capacity")
                                        .icon(StatIcons.CAPACITY)
                                        .initialValue(2D, 5D)
                                        .upgradeModifier(UpgradeOperation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .stat(StatData.builder("chance")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(0.025D, 0.075D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, 0.1)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("multiplying")
                                .requiredLevel(10)
                                .maxLevel(10)
                                .stat(StatData.builder("chance")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(0.05D, 0.15D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, 0.128)
                                        .formatValue(value -> (int) Math.round(MathUtils.round(value, 3) * 100))
                                        .build())
                                .stat(StatData.builder("size")
                                        .icon(StatIcons.SIZE)
                                        .initialValue(0.05D, 0.1D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, 0.1775)
                                        .formatValue(value -> (int) Math.round(MathUtils.round(value, 3) * 100))
                                        .build())
                                .stat(StatData.builder("amount")
                                        // TODO: icon
                                        .initialValue(0.05D, 0.15D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.4)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("explosion")
                                .requiredLevel(15)
                                .maxLevel(10)
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .castPredicate("spore", (player, stack) -> stack.getOrDefault(CHARGE, 0) > 0)
                                        .build())
                                .stat(StatData.builder("size")
                                        .icon(StatIcons.SIZE)
                                        .initialValue(0.05D, 0.25D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 20, 100))
                .style(StyleData.builder()
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.JUNGLE)
                        .build())
                .build();
    }

    public int getMaxSpores(ItemStack stack) {
        return (int) Math.round(canUseAbility(stack, "buffer") ? getStatValue(stack, "buffer", "capacity") : 1);
    }

    public int getSpores(ItemStack stack) {
        return stack.getOrDefault(CHARGE, 0);
    }

    public void setSpores(ItemStack stack, int amount) {
        stack.set(CHARGE, Mth.clamp(amount, 0, getMaxSpores(stack)));
    }

    public void addSpores(ItemStack stack, int amount) {
        if (canUseAbility(stack, "buffer") && amount < 0
                && new Random().nextFloat() <= getStatValue(stack, "buffer", "chance"))
            return;

        setSpores(stack, getSpores(stack) + amount);
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        Level level = player.getCommandSenderWorld();
        RandomSource random = level.getRandom();

        if (ability.equals("explosion")) {
            if (getSpores(stack) > 0) {
                ParticleUtils.createBall(ParticleTypes.SPIT, player.position().add(0, player.getBbHeight() / 2F, 0), level, 2, 0.5F);
                level.playSound(null, player.blockPosition(), SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.MASTER, 1F, 1F);

                while (getSpores(stack) > 0) {
                    float mul = player.getBbHeight() / 1.5F;
                    float speed = 0.25F + random.nextFloat() * 0.2F;
                    Vec3 motion = new Vec3(MathUtils.randomFloat(random) * speed, speed, MathUtils.randomFloat(random) * speed);

                    SporeEntity spore = new SporeEntity(EntityRegistry.SPORE.get(), level);

                    spore.setOwner(player);
                    spore.setStack(stack);
                    spore.setDeltaMovement(motion);
                    spore.setPos(player.position().add(0, mul, 0).add(motion.normalize().scale(mul)));
                    spore.setSize((float) Math.min(player.getMaxHealth(), 0.1F + (player.getMaxHealth() - player.getHealth()) * getStatValue(stack, "explosion", "size")));

                    level.addFreshEntity(spore);

                    addSpores(stack, -1);
                }
            }
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.level().isClientSide() || getSpores(stack) >= getMaxSpores(stack)
                || player.tickCount % Math.round(getStatValue(stack, "spore", "cooldown") * 20) != 0)
            return;

        addSpores(stack, 1);
    }

    @EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onLivingHurt(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.SPORE_SACK.get());

            if (!(stack.getItem() instanceof SporeSackItem relic) || relic.getSpores(stack) < 1)
                return;

            Level level = player.level();

            if (level.isClientSide())
                return;

            RandomSource random = player.getRandom();

            float mul = player.getBbHeight() / 1.5F;
            float speed = 0.25F + random.nextFloat() * 0.2F;
            Vec3 motion = new Vec3(MathUtils.randomFloat(random) * speed, speed, MathUtils.randomFloat(random) * speed);

            SporeEntity spore = new SporeEntity(EntityRegistry.SPORE.get(), level);

            spore.setOwner(player);
            spore.setStack(stack.copy());
            spore.setDeltaMovement(motion);
            spore.setPos(player.position().add(0, mul, 0).add(motion.normalize().scale(mul)));
            spore.setSize((float) Math.min(player.getMaxHealth(), 0.1F + event.getAmount() * relic.getStatValue(stack, "spore", "size")));

            level.addFreshEntity(spore);

            relic.addSpores(stack, -1);

            relic.spreadRelicExperience(player, stack, 1);
        }
    }
}