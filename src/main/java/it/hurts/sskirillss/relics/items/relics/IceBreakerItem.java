package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class IceBreakerItem extends RelicItem implements ICurioItem, IHasTooltip {
    private static final AttributeModifier ICE_BREAKER_SPEED_BOOST = new AttributeModifier(UUID.fromString("90af8e8a-93aa-4b0f-8ddc-8986dd2a8461"),
            Reference.MODID + ":" + "ice_breaker_movement_speed", RelicsConfig.IceBreaker.MOVEMENT_SPEED_MULTIPLIER.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final AttributeModifier ICE_BREAKER_KNOCKBACK_RESISTANCE = new AttributeModifier(UUID.fromString("70c6b1a0-e025-44bf-8dcd-c165c59b7eb4"),
            Reference.MODID + ":" + "ice_breaker_knockback resistance", RelicsConfig.IceBreaker.ADDITIONAL_KNOCKBACK_RESISTANCE.get(), AttributeModifier.Operation.ADDITION);

    public IceBreakerItem() {
        super(Rarity.RARE);
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.ice_breaker.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.ice_breaker.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            Vector3d motion = player.getDeltaMovement();
            ModifiableAttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            ModifiableAttributeInstance knockbackResistance = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (!movementSpeed.hasModifier(ICE_BREAKER_SPEED_BOOST)) movementSpeed.addTransientModifier(ICE_BREAKER_SPEED_BOOST);
            if (!knockbackResistance.hasModifier(ICE_BREAKER_KNOCKBACK_RESISTANCE)) knockbackResistance.addTransientModifier(ICE_BREAKER_KNOCKBACK_RESISTANCE);
            if (player.fallDistance >= RelicsConfig.IceBreaker.MIN_FALL_DISTANCE.get() && player.isShiftKeyDown())
                player.setDeltaMovement(motion.x(), motion.y() * RelicsConfig.IceBreaker.FALL_MOTION_MULTIPLIER.get(), motion.z());
            if (player.horizontalCollision && player.isShiftKeyDown()) {
                player.setDeltaMovement(0, -RelicsConfig.IceBreaker.WALL_SLIPPING_SPEED.get(), 0);
                player.fallDistance = 0;
                player.getCommandSenderWorld().addParticle(ParticleTypes.CRIT, player.getX(), player.getY() - 0.15D, player.getZ(), 0, 0, 0);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED);
        ModifiableAttributeInstance knockbackResistance = slotContext.getWearer().getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (movementSpeed.hasModifier(ICE_BREAKER_SPEED_BOOST)) movementSpeed.removeModifier(ICE_BREAKER_SPEED_BOOST);
        if (knockbackResistance.hasModifier(ICE_BREAKER_KNOCKBACK_RESISTANCE)) knockbackResistance.removeModifier(ICE_BREAKER_KNOCKBACK_RESISTANCE);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class IceBreakerServerEvents {
        @SubscribeEvent
        public static void onEntityFall(LivingFallEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ICE_BREAKER.get(), player).isPresent()
                        && !player.getCooldowns().isOnCooldown(ItemRegistry.ICE_BREAKER.get())) {
                    if (event.getDistance() >= RelicsConfig.IceBreaker.MIN_FALL_DISTANCE.get() && player.isShiftKeyDown()) {
                        player.getCommandSenderWorld().playSound(null, player.blockPosition(), SoundEvents.WITHER_BREAK_BLOCK,
                                SoundCategory.PLAYERS, 0.75F, 1.0F);
                        player.getCooldowns().addCooldown(ItemRegistry.ICE_BREAKER.get(), Math.round(event.getDistance()
                                * RelicsConfig.IceBreaker.STOMP_COOLDOWN_MULTIPLIER.get().floatValue() * 20));
                        for (LivingEntity entity : player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class,
                                player.getBoundingBox().inflate(event.getDistance() * RelicsConfig.IceBreaker.STOMP_RADIUS_MULTIPLIER.get()))) {
                            if (entity != player) {
                                entity.hurt(DamageSource.playerAttack(player), Math.min(RelicsConfig.IceBreaker.MAX_DEALT_DAMAGE.get().floatValue(),
                                        event.getDistance() * RelicsConfig.IceBreaker.DEALT_DAMAGE_MULTIPLIER.get().floatValue()));
                                entity.setDeltaMovement(entity.position().subtract(player.position()).add(0, 1.005F, 0).multiply(
                                        RelicsConfig.IceBreaker.STOMP_MOTION_MULTIPLIER.get(),
                                        RelicsConfig.IceBreaker.STOMP_MOTION_MULTIPLIER.get(),
                                        RelicsConfig.IceBreaker.STOMP_MOTION_MULTIPLIER.get()));
                            }
                        }
                        if (player.getCommandSenderWorld().getBlockState(player.blockPosition().below()).is(BlockTags.ICE))
                            player.getCommandSenderWorld().destroyBlock(player.blockPosition().below(), false, player);
                        event.setDamageMultiplier(RelicsConfig.IceBreaker.INCOMING_FALL_DAMAGE_MULTIPLIER.get().floatValue());
                    }
                }
            }
        }
    }
}