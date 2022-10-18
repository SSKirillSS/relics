package it.hurts.sskirillss.relics.items.relics.back;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

public class MidnightRobeItem extends RelicItem {
    private static final String TAG_TARGET = "target";

    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("vanish", RelicAbilityEntry.builder()
                                .requiredPoints(2)
                                .stat("light", RelicAbilityStat.builder()
                                        .initialValue(1D, 2D)
                                        .upgradeModifier("add", 1D)
                                        .formatValue(value -> String.valueOf((int) MathUtils.round(value, 0)))
                                        .build())
                                .stat("speed", RelicAbilityStat.builder()
                                        .initialValue(0.1D, 0.2D)
                                        .upgradeModifier("add", 0.1D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .ability("backstab", RelicAbilityEntry.builder()
                                .stat("damage", RelicAbilityStat.builder()
                                        .initialValue(1.5D, 2D)
                                        .upgradeModifier("add", 0.1D)
                                        .formatValue(value -> String.valueOf((int) (100 * MathUtils.round(value - 1, 1))))
                                        .build())
                                .stat("distance", RelicAbilityStat.builder()
                                        .initialValue(15D, 20D)
                                        .upgradeModifier("add", -1D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#00071f", "#001974")
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity entity, ItemStack stack) {
        Level level = entity.getCommandSenderWorld();

        if (DurabilityUtils.isBroken(stack) || level.isClientSide())
            return;

        ServerLevel serverLevel = (ServerLevel) level;
        LivingEntity target = getTarget(serverLevel, stack);

        if (target != null) {
            double radius = getAbilityValue(stack, "backstab", "distance");
            double step = 0.25D;
            int offset = 16;

            double len = (float) (2 * Math.PI * radius);
            int num = (int) (len / step);

            for (int i = 0; i < num; i++) {
                double angle = Math.toRadians(((360F / num) * i) + (360F * ((((len / step) - num) / num) / len)));

                double extraX = (radius * Math.sin(angle)) + target.getX();
                double extraZ = (radius * Math.cos(angle)) + target.getZ();
                double extraY = target.getY() + target.getBbHeight() * 0.5F;

                boolean foundPos = false;

                int tries;

                for (tries = 0; tries < offset * 2; tries++) {
                    Vec3 vec = new Vec3(extraX, extraY, extraZ);
                    BlockPos pos = new BlockPos(vec);

                    BlockState state = serverLevel.getBlockState(pos);
                    VoxelShape shape = state.getCollisionShape(serverLevel, pos);

                    if (shape.isEmpty()) {
                        if (!foundPos) {
                            extraY -= 1;

                            continue;
                        }
                    } else
                        foundPos = true;

                    if (shape.isEmpty())
                        break;

                    AABB aabb = shape.bounds();

                    if (!aabb.move(pos).contains(vec)) {
                        if (aabb.maxY >= 1F) {
                            extraY += 1;

                            continue;
                        }

                        break;
                    }

                    extraY += step;
                }

                if (tries < offset * 2)
                    serverLevel.sendParticles(new CircleTintData(new Color(50 + serverLevel.getRandom().nextInt(50), 0, 255), 0.2F, 3, 0.75F, true),
                            extraX, extraY + 0.1F, extraZ, 1, 0.05, 0.05, 0.05, 0.025);
            }
        }

        if (!canHide(entity)) {
            EntityUtils.removeAttribute(entity, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL);

            if (target != null && (target.isDeadOrDying() || target.position().distanceTo(entity.position()) >= getAbilityValue(stack, "backstab", "distance")))
                NBTUtils.clearTag(stack, TAG_TARGET);
        } else {
            entity.addEffect(new MobEffectInstance(EffectRegistry.VANISHING.get(), 5, 0, false, false));

            EntityUtils.applyAttribute(entity, stack, Attributes.MOVEMENT_SPEED, (float) getAbilityValue(stack, "vanishing", "speed"), AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }

    @Nullable
    private static LivingEntity getTarget(Level level, ItemStack stack) {
        if (level.isClientSide())
            return null;

        ServerLevel serverLevel = (ServerLevel) level;

        String string = NBTUtils.getString(stack, TAG_TARGET, "");

        if (string.isEmpty() || !(serverLevel.getEntity(UUID.fromString(string)) instanceof LivingEntity target))
            return null;

        return target;
    }

    private static boolean canHide(LivingEntity entity) {
        ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.MIDNIGHT_ROBE.get());
        Level world = entity.getCommandSenderWorld();
        BlockPos position = entity.blockPosition().above();

        double light = getAbilityValue(stack, "vanish", "light");

        return !stack.isEmpty() && !DurabilityUtils.isBroken(stack) && NBTUtils.getString(stack, TAG_TARGET, "").isEmpty()
                && world.getBrightness(LightLayer.BLOCK, position) + world.getBrightness(LightLayer.SKY, position) / 2D <= (world.isNight() ? light * 1.5D : light);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttribute(slotContext.entity(), stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Mod.EventBusSubscriber
    public static class ServerEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity target = event.getEntityLiving();
            Level level = target.getCommandSenderWorld();

            if (!(event.getSource().getEntity() instanceof Player player)
                    || level.isClientSide())
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.MIDNIGHT_ROBE.get());

            if (stack.isEmpty() || !canHide(player))
                return;

            event.setAmount((float) (event.getAmount() * getAbilityValue(stack, "backstab", "damage")));

            addExperience(stack, 1);

            NBTUtils.setString(stack, TAG_TARGET, event.getEntityLiving().getStringUUID());
        }
    }
}