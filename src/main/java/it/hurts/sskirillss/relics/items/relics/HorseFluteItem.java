package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicLootData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class HorseFluteItem extends RelicItem<HorseFluteItem.Stats> {
    private static final String TAG_ENTITY = "entity";
    private static final String TAG_UUID = "uuid";

    public HorseFluteItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .ability(AbilityTooltip.builder()
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg(stats.teleportDistance)
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .loot(RelicLootData.builder()
                        .table(RelicUtils.Worldgen.VILLAGE)
                        .chance(0.05F)
                        .build())
                .loot(RelicLootData.builder()
                        .table(RelicUtils.Worldgen.CAVE)
                        .chance(0.05F)
                        .build())
                .build();
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!(entity instanceof HorseEntity))
            return ActionResultType.FAIL;

        HorseEntity horse = (HorseEntity) entity;
        CompoundNBT nbt = stack.getTagElement(TAG_ENTITY);

        if (horse.getUUID().toString().equals(NBTUtils.getString(stack, TAG_UUID, ""))
                && player.isShiftKeyDown()) {
            NBTUtils.setString(stack, TAG_UUID, "");

            return ActionResultType.SUCCESS;
        }

        if (nbt != null) {
            releaseHorse(stack, player);
            catchHorse(horse, player, stack);

            return ActionResultType.SUCCESS;
        }

        catchHorse(horse, player, stack);

        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        CompoundNBT nbt = stack.getTagElement(TAG_ENTITY);

        if (player == null)
            return ActionResultType.FAIL;

        if (nbt == null) {
            catchHorse(stack, player);

            return ActionResultType.SUCCESS;
        }

        releaseHorse(stack, player);

        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundNBT nbt = stack.getTagElement(TAG_ENTITY);

        if (nbt == null) {
            catchHorse(stack, player);

            return ActionResult.success(stack);
        }

        releaseHorse(stack, player);

        return ActionResult.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        HorseEntity horse = findHorse(worldIn, stack);

        if (horse == null || entityIn.distanceTo(horse) < stats.teleportDistance)
            return;

        catchHorse(horse, entityIn, stack);

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    private HorseEntity findHorse(World world, ItemStack stack) {
        if (world.isClientSide())
            return null;

        ServerWorld serverWorld = (ServerWorld) world;
        String uuid = NBTUtils.getString(stack, TAG_UUID, "");

        if (uuid.equals(""))
            return null;

        Entity entity = serverWorld.getEntity(UUID.fromString(uuid));

        return entity instanceof HorseEntity ? (HorseEntity) entity : null;
    }

    private void catchHorse(HorseEntity horse, Entity player, ItemStack stack) {
        if (horse.isDeadOrDying() || horse.getOwnerUUID() == null
                || !horse.getOwnerUUID().equals(player.getUUID()))
            return;

        World world = horse.getCommandSenderWorld();
        CompoundNBT nbt = new CompoundNBT();
        Vector3d pos = horse.position();

        horse.saveWithoutId(nbt);

        stack.addTagElement(TAG_ENTITY, nbt);
        NBTUtils.setString(stack, TAG_UUID, "");

        horse.remove();

        if (!world.isClientSide())
            ((ServerWorld) world).sendParticles(ParticleTypes.EXPLOSION, pos.x(), pos.y(), pos.z(), 1, 0F, 0F, 0F, 0F);
        world.playSound(null, horse.blockPosition(), SoundEvents.CHICKEN_EGG, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    private void catchHorse(ItemStack stack, Entity player) {
        World world = player.getCommandSenderWorld();

        HorseEntity horse = findHorse(world, stack);

        if (horse != null)
            catchHorse(horse, player, stack);
    }

    private void releaseHorse(ItemStack stack, Entity player) {
        World world = player.getCommandSenderWorld();
        HorseEntity horse = new HorseEntity(EntityType.HORSE, world);
        Vector3d pos = player.position();
        CompoundNBT data = stack.getTagElement(TAG_ENTITY);

        if (data != null)
            horse.load(data);

        horse.setPos(pos.x(), pos.y(), pos.z());
        world.addFreshEntity(horse);
        horse.setDeltaMovement(player.getViewVector(1.0F).normalize());
        horse.fallDistance = 0F;

        stack.setTag(new CompoundNBT());
        NBTUtils.setString(stack, TAG_UUID, horse.getUUID().toString());

        if (!world.isClientSide())
            ((ServerWorld) world).sendParticles(ParticleTypes.EXPLOSION, pos.x(), pos.y(), pos.z(), 1, 0F, 0F, 0F, 0F);
        world.playSound(null, horse.blockPosition(), SoundEvents.BEEHIVE_EXIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getTagElement(TAG_ENTITY) != null;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    public static class Stats extends RelicStats {
        public int teleportDistance = 16;
    }
}