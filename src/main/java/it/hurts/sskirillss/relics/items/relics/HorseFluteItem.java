package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
                .borders("#eed551", "#dcbe1d")
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
                .build();
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!(entity instanceof Horse horse))
            return InteractionResult.FAIL;

        CompoundTag nbt = stack.getTagElement(TAG_ENTITY);

        if (nbt != null) {
            releaseHorse(stack, player);
            catchHorse(horse, player, stack);

            return InteractionResult.SUCCESS;
        }

        catchHorse(horse, player, stack);

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        CompoundTag nbt = stack.getTagElement(TAG_ENTITY);

        if (player == null)
            return InteractionResult.FAIL;

        if (nbt == null) {
            if (player.isShiftKeyDown())
                NBTUtils.setString(stack, TAG_UUID, "");
            else
                catchHorse(stack, player);

            return InteractionResult.SUCCESS;
        }

        releaseHorse(stack, player);

        if (player.isShiftKeyDown())
            NBTUtils.setString(stack, TAG_UUID, "");

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag nbt = stack.getTagElement(TAG_ENTITY);

        if (nbt == null) {
            if (player.isShiftKeyDown())
                NBTUtils.setString(stack, TAG_UUID, "");
            else
                catchHorse(stack, player);

            return InteractionResultHolder.success(stack);
        }

        releaseHorse(stack, player);

        if (player.isShiftKeyDown())
            NBTUtils.setString(stack, TAG_UUID, "");

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        Horse horse = findHorse(worldIn, stack);

        if (horse == null || entityIn.distanceTo(horse) < stats.teleportDistance)
            return;

        catchHorse(horse, entityIn, stack);

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    private Horse findHorse(Level world, ItemStack stack) {
        if (world.isClientSide())
            return null;

        ServerLevel serverLevel = (ServerLevel) world;
        String uuid = NBTUtils.getString(stack, TAG_UUID, "");

        if (uuid.equals(""))
            return null;

        Entity entity = serverLevel.getEntity(UUID.fromString(uuid));

        return entity instanceof Horse ? (Horse) entity : null;
    }

    private void catchHorse(Horse horse, Entity player, ItemStack stack) {
        if (horse.isDeadOrDying() || horse.getOwnerUUID() == null
                || !horse.getOwnerUUID().equals(player.getUUID()))
            return;

        Level world = horse.getCommandSenderWorld();
        CompoundTag nbt = new CompoundTag();
        Vec3 pos = horse.position();

        horse.saveWithoutId(nbt);

        stack.addTagElement(TAG_ENTITY, nbt);
        NBTUtils.setString(stack, TAG_UUID, "");

        horse.remove(Entity.RemovalReason.KILLED);

        if (!world.isClientSide())
            ((ServerLevel) world).sendParticles(ParticleTypes.EXPLOSION, pos.x(), pos.y(), pos.z(), 1, 0F, 0F, 0F, 0F);
        world.playSound(null, horse.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private void catchHorse(ItemStack stack, Entity player) {
        Level world = player.getCommandSenderWorld();

        Horse horse = findHorse(world, stack);

        if (horse != null)
            catchHorse(horse, player, stack);
    }

    private void releaseHorse(ItemStack stack, Entity player) {
        Level world = player.getCommandSenderWorld();
        Horse horse = new Horse(EntityType.HORSE, world);
        Vec3 pos = player.position();
        CompoundTag data = stack.getTagElement(TAG_ENTITY);

        if (data != null)
            horse.load(data);

        horse.setPos(pos.x(), pos.y(), pos.z());
        world.addFreshEntity(horse);
        horse.setDeltaMovement(player.getViewVector(1.0F).normalize());
        horse.fallDistance = 0F;

        stack.setTag(new CompoundTag());
        NBTUtils.setString(stack, TAG_UUID, horse.getUUID().toString());

        if (!world.isClientSide())
            ((ServerLevel) world).sendParticles(ParticleTypes.EXPLOSION, pos.x(), pos.y(), pos.z(), 1, 0F, 0F, 0F, 0F);
        world.playSound(null, horse.blockPosition(), SoundEvents.BEEHIVE_EXIT, SoundSource.PLAYERS, 1.0F, 1.0F);
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