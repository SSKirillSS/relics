package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicLootData;
import it.hurts.sskirillss.relics.entities.ShadowGlaiveEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.*;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.SlotContext;

public class ShadowGlaiveItem extends RelicItem<ShadowGlaiveItem.Stats> {
    public static final String TAG_CHARGES = "charges";
    private static final String TAG_TIME = "time";

    public static ShadowGlaiveItem INSTANCE;

    public ShadowGlaiveItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .ability(AbilityTooltip.builder()
                        .arg(stats.maxBounces)
                        .arg(stats.damage)
                        .arg(stats.chargeRegenerationTime)
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .loot(RelicLootData.builder()
                        .table(LootTables.END_CITY_TREASURE.toString())
                        .chance(0.1F)
                        .build())
                .build();
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (group != RelicsTab.RELICS_TAB)
            return;

        ItemStack stack = new ItemStack(ItemRegistry.SHADOW_GLAIVE.get());

        NBTUtils.setInt(stack, TAG_CHARGES, stats.maxCharges);

        items.add(stack);

        super.fillItemCategory(group, items);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        int charges = NBTUtils.getInt(stack, TAG_CHARGES, 0);

        if (DurabilityUtils.isBroken(stack) || entityIn.tickCount % 20 != 0 || charges >= stats.maxCharges)
            return;

        int time = NBTUtils.getInt(stack, TAG_TIME, 0);

        if (time >= stats.chargeRegenerationTime) {
            NBTUtils.setInt(stack, TAG_CHARGES, charges + 1);
            NBTUtils.setInt(stack, TAG_TIME, 0);
        } else
            NBTUtils.setInt(stack, TAG_TIME, time + 1);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        int charges = NBTUtils.getInt(stack, TAG_CHARGES, 0);

        if (DurabilityUtils.isBroken(stack) || charges <= 0 || playerIn.getCooldowns().isOnCooldown(stack.getItem()))
            return ActionResult.fail(stack);

        ShadowGlaiveEntity glaive = new ShadowGlaiveEntity(worldIn, playerIn);

        glaive.setOwner(playerIn);
        glaive.teleportTo(playerIn.getX(), playerIn.getY() + playerIn.getBbHeight() * 0.5F, playerIn.getZ());
        glaive.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, stats.projectileSpeed, 1, 0.0F);

        worldIn.addFreshEntity(glaive);

        EntityRayTraceResult result = EntityUtils.rayTraceEntity(playerIn, EntityPredicates.NO_CREATIVE_OR_SPECTATOR, 20);

        if (result != null) {
            Entity target = result.getEntity();

            if (target instanceof LivingEntity)
                glaive.setTarget((LivingEntity) target);
        }

        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundRegistry.THROW,
                SoundCategory.MASTER, 0.5F, 0.75F + (random.nextFloat() * 0.5F));

        NBTUtils.setInt(stack, TAG_CHARGES, charges - 1);
        playerIn.getCooldowns().addCooldown(stack.getItem(), stats.throwCooldown);

        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    public static class Stats extends RelicStats {
        public int maxCharges = 8;
        public int chargeRegenerationTime = 10;
        public int damage = 5;
        public int throwCooldown = 5;
        public float bounceChanceMultiplier = 0.015F;
        public int bounceRadius = 7;
        public float projectileSpeed = 0.75F;
        public int maxBounces = 10;
    }
}