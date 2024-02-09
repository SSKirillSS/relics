package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.api.events.common.LivingSlippingEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.entities.ShockwaveEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateInfo;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

public class IceBreakerItem extends RelicItem {
    public static final String TAG_FALLING_POINT = "point";

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("sustainability", RelicAbilityEntry.builder()
                                .stat("modifier", RelicAbilityStat.builder()
                                        .initialValue(0.75, 0.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, -0.05D)
                                        .formatValue(value -> (int) (MathUtils.round(1 - value, 1) * 100))
                                        .build())
                                .build())
                        .ability("impact", RelicAbilityEntry.builder()
                                .maxLevel(10)
                                .active(AbilityCastType.INSTANTANEOUS, AbilityCastPredicate.builder()
                                        .predicate("falling", data -> {
                                                    Player player = data.getPlayer();

                                                    return PredicateInfo.builder()
                                                            .condition(!(player.isOnGround() || player.isSpectator()))
                                                            .build();
                                                }
                                        )
                                )
                                .stat("size", RelicAbilityStat.builder()
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.3D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat("damage", RelicAbilityStat.builder()
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 200))
                .styleData(RelicStyleData.builder()
                        .borders("#dc41ff", "#832698")
                        .build())
                .build();
    }

    @Override
    public RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(Attributes.KNOCKBACK_RESISTANCE, (float) getAbilityValue(stack, "sustainability", "modifier")))
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, AbilityCastType type, AbilityCastStage stage) {
        if (ability.equals("impact")) {
            NBTUtils.setString(stack, TAG_FALLING_POINT, NBTUtils.writePosition(player.position()));
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        Vec3 position = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_FALLING_POINT, ""));

        if (position == null)
            return;

        if (!player.isOnGround()) {
            Vec3 motion = player.getDeltaMovement();

            if (player.isOnGround() || player.isSpectator()) {
                NBTUtils.clearTag(stack, TAG_FALLING_POINT);

                return;
            }

            player.stopFallFlying();
            player.getAbilities().flying = false;

            player.fallDistance = 0F;

            player.setDeltaMovement(motion.x(), Math.min(-0.01F, motion.y() * 1.075F), motion.z());
        } else {
            Level level = player.getCommandSenderWorld();

            double distance = (position.y() + Math.abs(level.getMinBuildHeight())) - (player.getY() + Math.abs(level.getMinBuildHeight()));

            NBTUtils.clearTag(stack, TAG_FALLING_POINT);

            if (distance <= 0)
                return;

            addExperience(player, stack, (int) Math.min(10, Math.round(distance / 3F)));

            ShockwaveEntity shockwave = new ShockwaveEntity(level,
                    (int) Math.round(Math.min(getAbilityValue(stack, "impact", "size"), distance * 0.25D)),
                    (float) getAbilityValue(stack, "impact", "damage"));

            BlockPos pos = player.getOnPos();

            shockwave.setOwner(player);
            shockwave.setPos(pos.getX(), pos.getY(), pos.getZ());

            level.addFreshEntity(shockwave);

            level.playSound(null, player.blockPosition(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.MASTER, 0.75F, 1.0F);
            level.addParticle(ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (stack.getItem() == newStack.getItem())
            return;

        NBTUtils.clearTag(stack, TAG_FALLING_POINT);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onLivingSlipping(LivingSlippingEvent event) {
            if (event.getFriction() <= 0.6F || !(event.getEntity() instanceof Player player)
                    || player.isInWater() || player.isInLava())
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.ICE_BREAKER.get());

            if (stack.isEmpty() || DurabilityUtils.isBroken(stack))
                return;

            event.setFriction(0.6F);
        }
    }
}