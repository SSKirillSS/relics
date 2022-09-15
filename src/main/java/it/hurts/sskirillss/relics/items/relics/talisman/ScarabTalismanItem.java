package it.hurts.sskirillss.relics.items.relics.talisman;

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
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class ScarabTalismanItem extends RelicItem<ScarabTalismanItem.Stats> {
    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "scarab_talisman_movement_speed", UUID.fromString("09bc5b60-3277-45ee-8bf0-aae7acba4385"));

    public static ScarabTalismanItem INSTANCE;

    public ScarabTalismanItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .hasAbility()
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#fcc100", "#fca900")
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.speedModifier * 100 - 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.digModifier * 100 - 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
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
        AttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        Level world = livingEntity.getCommandSenderWorld();

        if (DurabilityUtils.isBroken(stack))
            return;

        if (stats.allowedBiomes.stream().map(Biome.BiomeCategory::byName).collect(Collectors.toList())
                .contains(world.getBiome(livingEntity.blockPosition())))
            EntityUtils.applyAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(),
                    SPEED_INFO.getLeft(), stats.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
        else
            EntityUtils.removeAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(),
                    stats.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        AttributeInstance movementSpeed = slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED);

        EntityUtils.removeAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(),
                stats.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public void castAbility(Player player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(stack.getItem()))
            return;

        Level world = player.getCommandSenderWorld();
        Random random = world.getRandom();
        BlockPos position = player.blockPosition();
        Vec3 vec = player.position();

        BlockPos target = position;
        Vec3 ground = vec;
        boolean canTeleport = false;


        if (isEmptySpot(world, position) && isEmptySpot(world, position.above())) {
            vec = vec.add(0F, -2F, 0F);
            target = target.below(3);

            if (!isEmptySpot(world, target) && !isEmptySpot(world, target.above()) && !isEmptySpot(world, target.above()))
                canTeleport = true;
        } else if (!isEmptySpot(world, position) && !isEmptySpot(world, position.above())) {
            vec = vec.add(0F, 2F, 0F);
            ground = ground.add(0F, 2F, 0F);
            target = target.above(2);

            if (isEmptySpot(world, target) && isEmptySpot(world, target.above()))
                canTeleport = true;
        }

        if (canTeleport) {
            player.getCooldowns().addCooldown(stack.getItem(), stats.burrowCooldown * 20);

            player.teleportTo(vec.x(), vec.y(), vec.z());

            for (int i = 0; i < 100; i++)
                world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, world.getBlockState(position.below())),
                        ground.x() + MathUtils.randomFloat(random) * 0.5F, ground.y() + 0.2F,
                        ground.z() + MathUtils.randomFloat(random) * 0.5F, 0, random.nextFloat(), 0);
            world.playSound(null, player.blockPosition(), SoundEvents.BASALT_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    private boolean isEmptySpot(Level world, BlockPos position) {
        return !world.getBlockState(position).getMaterial().blocksMotion();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ScarabTalismanServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            LivingEntity entity = event.getEntityLiving();

            if (EntityUtils.findEquippedCurio(entity, ItemRegistry.SCARAB_TALISMAN.get()).isEmpty()
                    || event.getSource() != DamageSource.IN_WALL)
                return;

            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, false, false));

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onEntityAttack(LivingAttackEvent event) {
            LivingEntity entity = event.getEntityLiving();

            if (EntityUtils.findEquippedCurio(entity, ItemRegistry.SCARAB_TALISMAN.get()).isEmpty()
                    || event.getSource() != DamageSource.IN_WALL)
                return;

            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 30, 0, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30, 0, false, false));

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onBlockBreakCalculate(PlayerEvent.BreakSpeed event) {
            Stats stats = INSTANCE.stats;

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SCARAB_TALISMAN.get(), event.getEntityLiving()).ifPresent(triple -> {
                if (DurabilityUtils.isBroken(triple.getRight()))
                    return;

                event.setNewSpeed(event.getNewSpeed() * stats.digModifier);
            });
        }
    }

    public static class Stats extends RelicStats {
        public int burrowCooldown = 3;
        public float speedModifier = 1.15F;
        public float digModifier = 1.1F;
        public List<String> allowedBiomes = Arrays.asList("mesa", "desert");
    }
}