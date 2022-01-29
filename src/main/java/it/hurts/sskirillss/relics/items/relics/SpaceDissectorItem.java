package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.entities.SpaceDissectorEntity;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Random;
import java.util.UUID;

public class SpaceDissectorItem extends RelicItem<SpaceDissectorItem.Stats> {
    public static final String TAG_IS_THROWN = "thrown";
    public static final String TAG_UUID = "uuid";
    public static final String TAG_UPDATE_TIME = "time";

    public static SpaceDissectorItem INSTANCE;

    public SpaceDissectorItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#e3dbd7", "#8a7d78")
                .ability(AbilityTooltip.builder()
                        .arg(stats.maxBounces)
                        .arg(stats.baseDamage)
                        .arg((int) (stats.damageMultiplierPerBounce * 100 - 100) + "%")
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg(Minecraft.getInstance().options.keyShift.getKey().getDisplayName().getString())
                        .active(Minecraft.getInstance().options.keyUse)
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
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        Random random = playerIn.getRandom();

        if (DurabilityUtils.isBroken(stack) || playerIn.getCooldowns().isOnCooldown(stack.getItem()))
            return InteractionResultHolder.fail(stack);

        if (!NBTUtils.getBoolean(stack, TAG_IS_THROWN, false)) {
            SpaceDissectorEntity entity = new SpaceDissectorEntity(worldIn, playerIn);

            NBTUtils.setBoolean(stack, TAG_IS_THROWN, true);
            NBTUtils.setString(stack, TAG_UUID, entity.getUUID().toString());
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);

            entity.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 1.0F, stats.projectileSpeed, 0.0F);
            entity.stack = playerIn.getItemInHand(handIn);
            entity.setOwner(playerIn);

            worldIn.addFreshEntity(entity);

            worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundRegistry.THROW,
                    SoundSource.MASTER, 0.5F, 0.75F + (random.nextFloat() * 0.5F));
        } else {
            String string = NBTUtils.getString(stack, TAG_UUID, "");

            if (string.equals(""))
                return InteractionResultHolder.fail(stack);

            UUID uuid = UUID.fromString(string);

            if (!(worldIn instanceof ServerLevel))
                return InteractionResultHolder.fail(stack);

            Entity entity = ((ServerLevel) worldIn).getEntity(uuid);

            if (entity instanceof SpaceDissectorEntity && entity.isAlive()) {
                SpaceDissectorEntity dissector = (SpaceDissectorEntity) entity;

                if (!playerIn.isShiftKeyDown()) {
                    if (!dissector.getEntityData().get(SpaceDissectorEntity.IS_RETURNING))
                        dissector.getEntityData().set(SpaceDissectorEntity.IS_RETURNING, true);
                } else {
                    playerIn.teleportTo(dissector.getX(), dissector.getY(), dissector.getZ());
                    worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(),
                            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                    playerIn.getCooldowns().addCooldown(stack.getItem(), stats.cooldownAfterTeleport * 20);

                    NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
                    dissector.remove(Entity.RemovalReason.KILLED);
                }
            } else {
                playerIn.getCooldowns().addCooldown(stack.getItem(), stats.cooldownAfterTeleport * 20);

                NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
            }
        }

        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (DurabilityUtils.isBroken(stack) || !NBTUtils.getBoolean(stack, TAG_IS_THROWN, false) || entityIn.tickCount % 20 != 0)
            return;

        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);

        NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);

        if (time > stats.maxThrownTime)
            NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    public static class Stats extends RelicStats {
        public float projectileSpeed = 1.0F;
        public int maxThrownTime = 60;
        public int timeBeforeReturn = 10;
        public int cooldownAfterTeleport = 10;
        public int maxBounces = 10;
        public int additionalTimeAfterBounce = 4;
        public int baseDamage = 8;
        public float damageMultiplierPerBounce = 1.5F;
    }
}