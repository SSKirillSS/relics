package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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

public class IceBreakerItem extends Item implements ICurioItem, IHasTooltip {
    private static final AttributeModifier ICE_BREAKER_SPEED_BOOST = new AttributeModifier(UUID.fromString("90af8e8a-93aa-4b0f-8ddc-8986dd2a8461"),
            Reference.MODID + ":" + "ice_breaker_movement_speed", RelicsConfig.IceBreaker.MOVEMENT_SPEED_MULTIPLIER.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);

    public IceBreakerItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.ice_breaker.shift_1"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            Vector3d motion = player.getMotion();
            ModifiableAttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (!movementSpeed.hasModifier(ICE_BREAKER_SPEED_BOOST)) movementSpeed.applyNonPersistentModifier(ICE_BREAKER_SPEED_BOOST);
            if (player.fallDistance >= RelicsConfig.IceBreaker.MIN_FALL_DISTANCE.get() && player.isSneaking())
                player.setMotion(motion.getX(), motion.getY() * RelicsConfig.IceBreaker.FALL_MOTION_MULTIPLIER.get(), motion.getZ());
            if (player.collidedHorizontally && player.isSneaking()) {
                player.setMotion(0, -RelicsConfig.IceBreaker.WALL_SLIPPING_SPEED.get(), 0);
                player.fallDistance = 0;
                player.getEntityWorld().addParticle(ParticleTypes.CRIT, player.getPosX(), player.getPosY() - 0.15D, player.getPosZ(), 0, 0, 0);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed.hasModifier(ICE_BREAKER_SPEED_BOOST)) {
            movementSpeed.removeModifier(ICE_BREAKER_SPEED_BOOST);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class IceBreakerServerEvents {
        @SubscribeEvent
        public static void onEntityFall(LivingFallEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ICE_BREAKER.get(), player).isPresent()
                        && !player.getCooldownTracker().hasCooldown(ItemRegistry.ICE_BREAKER.get())) {
                    if (event.getDistance() >= RelicsConfig.IceBreaker.MIN_FALL_DISTANCE.get() && player.isSneaking()) {
                        player.getEntityWorld().playSound(null, player.getPosition(), SoundEvents.ENTITY_WITHER_BREAK_BLOCK,
                                SoundCategory.PLAYERS, 0.75F, 1.0F);
                        player.getCooldownTracker().setCooldown(ItemRegistry.ICE_BREAKER.get(), Math.round(event.getDistance()
                                * RelicsConfig.IceBreaker.STOMP_COOLDOWN_MULTIPLIER.get().floatValue() * 20));
                        for (LivingEntity entity : player.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class,
                                player.getBoundingBox().grow(event.getDistance() * RelicsConfig.IceBreaker.STOMP_RADIUS_MULTIPLIER.get()))) {
                            if (entity != player) {
                                entity.attackEntityFrom(DamageSource.causePlayerDamage(player), Math.min(RelicsConfig.IceBreaker.MAX_DEALT_DAMAGE.get().floatValue(),
                                        event.getDistance() * RelicsConfig.IceBreaker.DEALT_DAMAGE_MULTIPLIER.get().floatValue()));
                                entity.setMotion(entity.getPositionVec().subtract(player.getPositionVec()).add(0, 1.005F, 0).mul(
                                        RelicsConfig.IceBreaker.STOMP_MOTION_MULTIPLIER.get(),
                                        RelicsConfig.IceBreaker.STOMP_MOTION_MULTIPLIER.get(),
                                        RelicsConfig.IceBreaker.STOMP_MOTION_MULTIPLIER.get()));
                            }
                        }
                        if (player.getEntityWorld().getBlockState(player.getPosition().down()).isIn(BlockTags.ICE))
                            player.getEntityWorld().destroyBlock(player.getPosition().down(), false, player);
                        event.setDamageMultiplier(RelicsConfig.IceBreaker.INCOMING_FALL_DAMAGE_MULTIPLIER.get().floatValue());
                    }
                }
            }
        }
    }
}