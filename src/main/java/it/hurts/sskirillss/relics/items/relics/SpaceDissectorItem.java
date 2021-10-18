package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.entities.SpaceDissectorEntity;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class SpaceDissectorItem extends RelicItem<SpaceDissectorItem.Stats> {
    public static final String TAG_IS_THROWN = "thrown";
    public static final String TAG_UUID = "uuid";
    public static final String TAG_UPDATE_TIME = "time";

    public static SpaceDissectorItem INSTANCE;

    public SpaceDissectorItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(LootTables.END_CITY_TREASURE.toString())
                        .chance(0.1F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .arg(config.maxBounces)
                        .arg(config.baseDamage)
                        .arg((int) (config.damageMultiplierPerBounce * 100 - 100) + "%")
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .shift(ShiftTooltip.builder()
                        .arg(Minecraft.getInstance().options.keyShift.getKey().getDisplayName().getString())
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .build();
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if (isBroken(stack) || playerIn.getCooldowns().isOnCooldown(stack.getItem()))
            return ActionResult.fail(stack);

        if (!NBTUtils.getBoolean(stack, TAG_IS_THROWN, false)) {
            SpaceDissectorEntity entity = new SpaceDissectorEntity(worldIn, playerIn);

            NBTUtils.setBoolean(stack, TAG_IS_THROWN, true);
            NBTUtils.setString(stack, TAG_UUID, entity.getUUID().toString());
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);

            entity.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, 1.0F, config.projectileSpeed, 0.0F);
            entity.stack = playerIn.getItemInHand(handIn);
            entity.setOwner(playerIn);

            worldIn.addFreshEntity(entity);

            worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundRegistry.THROW,
                    SoundCategory.MASTER, 0.5F, 0.75F + (random.nextFloat() * 0.5F));
        } else {
            String string = NBTUtils.getString(stack, TAG_UUID, "");

            if (string.equals(""))
                return ActionResult.fail(stack);

            UUID uuid = UUID.fromString(string);

            if (!(worldIn instanceof ServerWorld))
                return ActionResult.fail(stack);

            Entity entity = ((ServerWorld) worldIn).getEntity(uuid);

            if (entity instanceof SpaceDissectorEntity && entity.isAlive()) {
                SpaceDissectorEntity dissector = (SpaceDissectorEntity) entity;

                if (!playerIn.isShiftKeyDown()) {
                    if (!dissector.getEntityData().get(SpaceDissectorEntity.IS_RETURNING))
                        dissector.getEntityData().set(SpaceDissectorEntity.IS_RETURNING, true);
                } else {
                    playerIn.teleportTo(dissector.getX(), dissector.getY(), dissector.getZ());
                    worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(),
                            SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    playerIn.getCooldowns().addCooldown(stack.getItem(), config.cooldownAfterTeleport * 20);

                    NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
                    dissector.remove();
                }
            } else {
                playerIn.getCooldowns().addCooldown(stack.getItem(), config.cooldownAfterTeleport * 20);

                NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
            }
        }

        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (isBroken(stack) || !NBTUtils.getBoolean(stack, TAG_IS_THROWN, false) || entityIn.tickCount % 20 != 0)
            return;

        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);

        NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);

        if (time > config.maxThrownTime)
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