package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.ScarabTalismanModel;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ScarabTalismanItem extends RelicItem<ScarabTalismanItem.Stats> implements ICurioItem {
    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "scarab_talisman_movement_speed", UUID.fromString("09bc5b60-3277-45ee-8bf0-aae7acba4385"));

    public static ScarabTalismanItem INSTANCE;

    public ScarabTalismanItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .hasAbility()
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.DESERT)
                        .chance(0.1F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .arg("+" + (int) (config.speedModifier * 100 - 100) + "%")
                        .build())
                .shift(ShiftTooltip.builder()
                        .arg("+" + (int) (config.digModifier * 100 - 100) + "%")
                        .build())
                .shift(ShiftTooltip.builder()
                        .build())
                .shift(ShiftTooltip.builder()
                        .active()
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        World world = livingEntity.getCommandSenderWorld();

        if (isBroken(stack))
            return;

        if (config.allowedBiomes.stream().map(Biome.Category::byName).collect(Collectors.toList())
                .contains(world.getBiome(livingEntity.blockPosition()).getBiomeCategory()))
            EntityUtils.applyAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(),
                    SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
        else
            EntityUtils.removeAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(),
                    config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED);

        EntityUtils.removeAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(),
                config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public void castAbility(PlayerEntity player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(stack.getItem()))
            return;

        World world = player.getCommandSenderWorld();
        BlockPos position = player.blockPosition();
        Vector3d vec = player.position();

        BlockPos target = position;
        Vector3d ground = vec;
        boolean canTeleport = false;


        if (isEmptySpot(world, position) && isEmptySpot(world, position.above())) {
            vec = vec.add(0F, -2F, 0F);
            target = target.below(2);

            if (!isEmptySpot(world, target) && !isEmptySpot(world, target.above()))
                canTeleport = true;
        } else if (!isEmptySpot(world, position) && !isEmptySpot(world, position.above())) {
            vec = vec.add(0F, 2F, 0F);
            ground = ground.add(0F, 2F, 0F);
            target = target.above(2);

            if (isEmptySpot(world, target) && isEmptySpot(world, target.above()))
                canTeleport = true;
        }

        if (canTeleport) {
            player.getCooldowns().addCooldown(stack.getItem(), config.burrowCooldown * 20);

            player.teleportTo(vec.x(), vec.y(), vec.z());

            for (int i = 0; i < 100; i++)
                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, world.getBlockState(position.below())),
                        ground.x() + MathUtils.randomFloat(random) * 0.5F, ground.y() + 0.2F,
                        ground.z() + MathUtils.randomFloat(random) * 0.5F, 0, random.nextFloat(), 0);
            world.playSound(null, player.blockPosition(), SoundEvents.BASALT_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    private boolean isEmptySpot(World world, BlockPos position) {
        return !world.getBlockState(position).getMaterial().blocksMotion();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new ScarabTalismanModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ScarabTalismanServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            LivingEntity entity = event.getEntityLiving();

            if (EntityUtils.findEquippedCurio(entity, ItemRegistry.SCARAB_TALISMAN.get()).isEmpty()
                    || event.getSource() != DamageSource.IN_WALL)
                return;

            entity.addEffect(new EffectInstance(Effects.INVISIBILITY, 20, 0, false, false));

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onEntityAttack(LivingAttackEvent event) {
            LivingEntity entity = event.getEntityLiving();

            if (EntityUtils.findEquippedCurio(entity, ItemRegistry.SCARAB_TALISMAN.get()).isEmpty()
                    || event.getSource() != DamageSource.IN_WALL)
                return;

            entity.addEffect(new EffectInstance(Effects.INVISIBILITY, 20, 0, false, false));

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onBlockBreakCalculate(PlayerEvent.BreakSpeed event) {
            Stats config = INSTANCE.config;

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SCARAB_TALISMAN.get(), event.getEntityLiving()).ifPresent(triple -> {
                if (isBroken(triple.getRight()))
                    return;

                event.setNewSpeed(event.getNewSpeed() * config.digModifier);
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