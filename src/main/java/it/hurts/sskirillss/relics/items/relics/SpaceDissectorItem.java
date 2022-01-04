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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
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
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if (DurabilityUtils.isBroken(stack) || playerIn.getCooldowns().isOnCooldown(stack.getItem()))
            return ActionResult.fail(stack);

        if (!NBTUtils.getBoolean(stack, TAG_IS_THROWN, false)) {
            SpaceDissectorEntity entity = new SpaceDissectorEntity(worldIn, playerIn);

            NBTUtils.setBoolean(stack, TAG_IS_THROWN, true);
            NBTUtils.setString(stack, TAG_UUID, entity.getUUID().toString());
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);

            entity.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, 1.0F, stats.projectileSpeed, 0.0F);
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

                    playerIn.getCooldowns().addCooldown(stack.getItem(), stats.cooldownAfterTeleport * 20);

                    NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
                    dissector.remove();
                }
            } else {
                playerIn.getCooldowns().addCooldown(stack.getItem(), stats.cooldownAfterTeleport * 20);

                NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
            }
        }

        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
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