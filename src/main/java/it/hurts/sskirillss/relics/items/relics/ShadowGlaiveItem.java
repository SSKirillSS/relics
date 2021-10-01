package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.entities.ShadowGlaiveEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.SlotContext;

public class ShadowGlaiveItem extends RelicItem<ShadowGlaiveItem.Stats> {
    public static final String TAG_CHARGES = "charges";
    private static final String TAG_TIME = "time";

    public static ShadowGlaiveItem INSTANCE;

    public ShadowGlaiveItem() {
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
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg(config.maxBounces)
                        .varArg(config.damage)
                        .varArg(config.chargeRegenerationTime)
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .build();
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        ItemStack stack = new ItemStack(ItemRegistry.SHADOW_GLAIVE.get());

        NBTUtils.setInt(stack, TAG_CHARGES, config.maxCharges);

        items.add(stack);

        super.fillItemCategory(group, items);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        int charges = NBTUtils.getInt(stack, TAG_CHARGES, 0);

        if (entityIn.tickCount % 20 != 0 || charges >= config.maxCharges)
            return;

        int time = NBTUtils.getInt(stack, TAG_TIME, 0);

        if (time >= config.chargeRegenerationTime) {
            NBTUtils.setInt(stack, TAG_CHARGES, charges + 1);
            NBTUtils.setInt(stack, TAG_TIME, 0);
        } else
            NBTUtils.setInt(stack, TAG_TIME, time + 1);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        int charges = NBTUtils.getInt(stack, TAG_CHARGES, 0);

        if (charges <= 0 || playerIn.getCooldowns().isOnCooldown(stack.getItem()))
            return ActionResult.fail(stack);

        ShadowGlaiveEntity glaive = new ShadowGlaiveEntity(worldIn, playerIn);

        glaive.setOwner(playerIn);
        glaive.teleportTo(playerIn.getX(), playerIn.getY() + playerIn.getBbHeight() * 0.5F, playerIn.getZ());
        glaive.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, config.projectileSpeed, 1, 0.0F);

        worldIn.addFreshEntity(glaive);

        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundRegistry.THROW,
                SoundCategory.MASTER, 0.5F, 0.75F + (random.nextFloat() * 0.5F));

        NBTUtils.setInt(stack, TAG_CHARGES, charges - 1);
        playerIn.getCooldowns().addCooldown(stack.getItem(), config.throwCooldown);

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
        public int throwCooldown = 10;
        public float bounceChanceMultiplier = 0.015F;
        public int bounceRadius = 7;
        public float projectileSpeed = 0.45F;
        public int maxBounces = 10;
    }
}