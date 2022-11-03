package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
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
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.PacketItemActivation;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SpatialSignItem extends RelicItem {
    public static final String TAG_POSITION = "pos";
    public static final String TAG_STAGE = "stage";
    public static final String TAG_TIME = "time";
    public static final String TAG_WORLD = "world";

    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("seal", RelicAbilityEntry.builder()
                                .stat("speed", RelicAbilityStat.builder()
                                        .initialValue(1D, 1.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat("duration", RelicAbilityStat.builder()
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.5D)
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
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if (DurabilityUtils.isBroken(stack))
            return InteractionResultHolder.fail(stack);

        if (NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
            NBTUtils.setString(stack, TAG_POSITION, NBTUtils.writePosition(playerIn.position()));
            NBTUtils.setString(stack, TAG_WORLD, playerIn.getCommandSenderWorld().dimension().location().toString());
            NBTUtils.setInt(stack, TAG_TIME, (int) Math.round(getAbilityValue(stack, "seal", "duration")));

            worldIn.playSound(playerIn, playerIn.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else if (playerIn.isShiftKeyDown()) {
            String string = NBTUtils.getString(stack, TAG_POSITION, "");
            List<String> positions = Arrays.asList(string.split("\\|"));

            if (positions.size() <= 1) {
                NBTUtils.clearTag(stack, TAG_TIME);
                NBTUtils.clearTag(stack, TAG_STAGE);
                NBTUtils.clearTag(stack, TAG_POSITION);
                NBTUtils.clearTag(stack, TAG_WORLD);
            } else {
                NBTUtils.setString(stack, TAG_POSITION, string + "|" + NBTUtils.writePosition(playerIn.position()));
                NBTUtils.setInt(stack, TAG_STAGE, positions.size() - 2);
                NBTUtils.setInt(stack, TAG_TIME, 0);
            }
        }

        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (DurabilityUtils.isBroken(stack) || !(entityIn instanceof Player player))
            return;

        if (NBTUtils.getInt(stack, TAG_STAGE, -1) > -1)
            player.noPhysics = true;

        if (worldIn.isClientSide())
            return;

        Random random = worldIn.getRandom();

        int time = NBTUtils.getInt(stack, TAG_TIME, 0);
        int stage = NBTUtils.getInt(stack, TAG_STAGE, -1);

        String string = NBTUtils.getString(stack, TAG_POSITION, "");
        List<String> positions = Arrays.asList(string.split("\\|"));

        double speed = getAbilityValue(stack, "seal", "speed");

        if (time == 0 && stage != -1) {
            Vec3 pos = NBTUtils.parsePosition(positions.get(stage));

            if (!worldIn.isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) worldIn;

                serverLevel.sendParticles(new CircleTintData(getParticleColor(random, (stage + 1) % 3), 0.1F, 30, 0.9F, true),
                        player.getX(), player.getY(), player.getZ(), 50, 0.25F, 0.25F, 0.25F, 0.05F);

                for (int i = positions.size() - 1; i > stage; i--) {
                    Vec3 currentVec = NBTUtils.parsePosition(positions.get(i));
                    Vec3 nextVec = i > stage + 1 ? NBTUtils.parsePosition(positions.get(i - 1)) : player.position().add(0, -1, 0);

                    int distance = (int) Math.round(currentVec.distanceTo(nextVec));

                    Vec3 finalVec = currentVec.add(nextVec.subtract(currentVec).normalize().multiply(distance, distance, distance));

                    distance = (int) Math.round(currentVec.distanceTo(finalVec)) * 10;

                    for (int j = 0; j < distance; j++) {
                        float x = (float) (((finalVec.x - currentVec.x) * j / distance) + currentVec.x);
                        float y = (float) (((finalVec.y - currentVec.y) * j / distance) + currentVec.y) + player.getBbHeight() / 2F;
                        float z = (float) (((finalVec.z - currentVec.z) * j / distance) + currentVec.z);

                        serverLevel.sendParticles(new CircleTintData(getParticleColor(random, i % 3), 0.1F, 2, 0.9F, true),
                                x, y, z, 1, 0.1F, 0.1F, 0.1F, 0.1F);
                    }
                }
            }

            if (pos != null) {
                if (!worldIn.dimension().location().toString().equals(NBTUtils.getString(stack, TAG_WORLD, ""))
                        || pos.distanceTo(player.position()) > speed * 3 + 100) {
                    stack.setDamageValue(getMaxDamage(stack));

                    worldIn.playSound(null, player.blockPosition(), SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.MASTER, 1F, 1F);

                    if (!worldIn.isClientSide())
                        NetworkHandler.sendToClient(new PacketItemActivation(stack), (ServerPlayer) player);

                    NBTUtils.clearTag(stack, TAG_TIME);
                    NBTUtils.clearTag(stack, TAG_POSITION);
                    NBTUtils.clearTag(stack, TAG_WORLD);

                    return;
                }

                ((ServerPlayer) player).connection.send(new ClientboundSetEntityMotionPacket(player.getId(), pos.add(0, 1, 0).subtract(player.position()).normalize().scale(speed)));

                if (player.tickCount % 20 == 0)
                    addExperience(player, stack, 1);

                player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING.get(), 20, 0, false, false));

                player.fallDistance = 0;
                player.clearFire();

                if (player.position().distanceTo(pos) <= speed * 2) {
                    NBTUtils.setInt(stack, TAG_STAGE, --stage);

                    if (stage < 0) {
                        NBTUtils.clearTag(stack, TAG_TIME);
                        NBTUtils.clearTag(stack, TAG_STAGE);
                        NBTUtils.clearTag(stack, TAG_POSITION);
                        NBTUtils.clearTag(stack, TAG_WORLD);

                        if (!worldIn.isClientSide()) {
                            ServerLevel serverLevel = (ServerLevel) worldIn;

                            for (int i = positions.size() - 1; i > stage; i--) {
                                Vec3 currentVec = NBTUtils.parsePosition(positions.get(i));
                                Vec3 nextVec = i > stage + 1 ? NBTUtils.parsePosition(positions.get(i - 1)) : player.position().add(0, -1, 0);

                                int distance = (int) Math.round(currentVec.distanceTo(nextVec));

                                Vec3 finalVec = currentVec.add(nextVec.subtract(currentVec).normalize().multiply(distance, distance, distance));

                                distance = (int) Math.round(currentVec.distanceTo(finalVec)) * 10;

                                for (int j = 0; j < distance; j++) {
                                    float x = (float) (((finalVec.x - currentVec.x) * j / distance) + currentVec.x);
                                    float y = (float) (((finalVec.y - currentVec.y) * j / distance) + currentVec.y) + player.getBbHeight() / 2F;
                                    float z = (float) (((finalVec.z - currentVec.z) * j / distance) + currentVec.z);

                                    serverLevel.sendParticles(new CircleTintData(getParticleColor(random, i % 3), 0.2F, 30, 0.94F, true),
                                            x, y, z, 2, 0.2F, 0.2F, 0.2F, 0.05F);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (time > 0) {
            if (player.tickCount % 20 == 0) {
                NBTUtils.setInt(stack, TAG_TIME, --time);

                if (time <= 10 && time > 0)
                    worldIn.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK,
                            SoundSource.MASTER, 0.75F, 0.75F + time * 0.125F);
            }

            if (player.tickCount % 5 == 0) {
                if (!positions.isEmpty()) {
                    if (!worldIn.dimension().location().toString().equals(NBTUtils.getString(stack, TAG_WORLD, ""))
                            || NBTUtils.parsePosition(positions.get(positions.size() - 1)).distanceTo(player.position()) > speed * 3 + 100) {
                        stack.setDamageValue(getMaxDamage(stack));

                        worldIn.playSound(null, player.blockPosition(), SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.MASTER, 1F, 1F);

                        if (!worldIn.isClientSide())
                            NetworkHandler.sendToClient(new PacketItemActivation(stack), (ServerPlayer) player);

                        NBTUtils.clearTag(stack, TAG_TIME);
                        NBTUtils.clearTag(stack, TAG_WORLD);
                        NBTUtils.clearTag(stack, TAG_STAGE);
                        NBTUtils.clearTag(stack, TAG_POSITION);

                        return;
                    }

                    Vec3 pos = NBTUtils.parsePosition(positions.get(positions.size() - 1));

                    if (pos != null && player.position().distanceTo(pos) > speed * 3)
                        NBTUtils.setString(stack, TAG_POSITION, string + "|" + NBTUtils.writePosition(player.position()));

                    if (time == 0)
                        NBTUtils.setInt(stack, TAG_STAGE, positions.size() - 1);
                }
            }
        }

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    private Color getParticleColor(Random random, int type) {
        return Lists.newArrayList(
                new Color(random.nextInt(50), 255, random.nextInt(50)),
                new Color(random.nextInt(50), random.nextInt(50), 255),
                new Color(255, 50 + random.nextInt(50), random.nextInt(50))
        ).get(type);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return NBTUtils.getInt(stack, TAG_TIME, 0) > 0;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Mod.EventBusSubscriber
    public static class ServerEvents {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity entity = event.getEntityLiving();

            if (!(entity instanceof Player player))
                return;

            for (int slot : EntityUtils.getSlotsWithItem(player, ItemRegistry.SPATIAL_SIGN.get())
                    .stream()
                    .filter(slot -> slot != -1)
                    .toList()) {
                ItemStack stack = player.getInventory().getItem(slot);

                if (stack.getItem() instanceof SpatialSignItem
                        && NBTUtils.getInt(stack, TAG_STAGE, -1) > -1
                        && !NBTUtils.getString(stack, TAG_POSITION, "").isEmpty()) {
                    event.setCanceled(true);

                    return;
                }
            }
        }
    }
}