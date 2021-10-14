package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class InfinityHamItem extends RelicItem<InfinityHamItem.Stats> implements ICurioItem {
    public static final String TAG_PIECES = "pieces";
    private static final String TAG_CHARGE = "charge";

    public InfinityHamItem() {
        super(RelicData.builder()
                .properties(new Item.Properties()
                        .tab(RelicsTab.RELICS_TAB)
                        .stacksTo(1)
                        .rarity(Rarity.RARE)
                        .food(new Food.Builder().build()))
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.CAVE)
                        .chance(0.1F)
                        .build())
                .build());
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArgs(config.feedAmount, config.maxPieces, config.rechargeTime)
                        .build())
                .build();
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (group != RelicsTab.RELICS_TAB)
            return;

        ItemStack stack = new ItemStack(ItemRegistry.INFINITY_HAM.get());

        NBTUtils.setInt(stack, TAG_PIECES, config.maxPieces);

        items.add(stack);

        super.fillItemCategory(group, items);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (isBroken(stack) || entityIn.tickCount % 20 != 0)
            return;

        int pieces = NBTUtils.getInt(stack, TAG_PIECES, 0);

        if (pieces >= config.maxPieces)
            return;

        int charge = NBTUtils.getInt(stack, TAG_CHARGE, 0);

        if (charge >= config.rechargeTime) {
            NBTUtils.setInt(stack, TAG_PIECES, pieces + 1);
            NBTUtils.setInt(stack, TAG_CHARGE, 0);
        } else
            NBTUtils.setInt(stack, TAG_CHARGE, charge + 1);

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!isBroken(stack) && NBTUtils.getInt(stack, TAG_PIECES, 0) > 0
                && player.getFoodData().needsFood())
            return super.use(world, player, hand);

        return ActionResult.pass(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity))
            return stack;

        PlayerEntity player = (PlayerEntity) entity;
        int pieces = NBTUtils.getInt(stack, TAG_PIECES, 0);

        if (pieces > 0) {
            NBTUtils.setInt(stack, TAG_PIECES, pieces - 1);

            player.getFoodData().eat(config.feedAmount, (float) config.saturationAmount / (float) config.feedAmount / 2F);
        }

        return stack;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return config.useDuration;
    }

    public static class Stats extends RelicStats {
        public int rechargeTime = 60;
        public int useDuration = 32;
        public int maxPieces = 3;
        public int feedAmount = 10;
        public int saturationAmount = 5;
    }
}