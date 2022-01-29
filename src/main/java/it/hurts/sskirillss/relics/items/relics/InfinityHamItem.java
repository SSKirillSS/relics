package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
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
                        .food(new FoodProperties.Builder().build()))
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#ffe0d2", "#9c756b")
                .ability(AbilityTooltip.builder()
                        .arg(stats.feedAmount)
                        .arg(stats.maxPieces)
                        .arg(stats.rechargeTime)
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
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (group != RelicsTab.RELICS_TAB)
            return;

        ItemStack stack = new ItemStack(ItemRegistry.INFINITY_HAM.get());

        NBTUtils.setInt(stack, TAG_PIECES, stats.maxPieces);

        items.add(stack);

        super.fillItemCategory(group, items);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (DurabilityUtils.isBroken(stack) || entityIn.tickCount % 20 != 0)
            return;

        int pieces = NBTUtils.getInt(stack, TAG_PIECES, 0);

        if (pieces >= stats.maxPieces)
            return;

        int charge = NBTUtils.getInt(stack, TAG_CHARGE, 0);

        if (charge >= stats.rechargeTime) {
            NBTUtils.setInt(stack, TAG_PIECES, pieces + 1);
            NBTUtils.setInt(stack, TAG_CHARGE, 0);
        } else
            NBTUtils.setInt(stack, TAG_CHARGE, charge + 1);

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!DurabilityUtils.isBroken(stack) && NBTUtils.getInt(stack, TAG_PIECES, 0) > 0
                && player.getFoodData().needsFood())
            return super.use(world, player, hand);

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!(entity instanceof Player player))
            return stack;

        int pieces = NBTUtils.getInt(stack, TAG_PIECES, 0);

        if (pieces > 0) {
            NBTUtils.setInt(stack, TAG_PIECES, pieces - 1);

            player.getFoodData().eat(stats.feedAmount, (float) stats.saturationAmount / (float) stats.feedAmount / 2F);
        }

        return stack;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return stats.useDuration;
    }

    public static class Stats extends RelicStats {
        public int rechargeTime = 60;
        public int useDuration = 32;
        public int maxPieces = 3;
        public int feedAmount = 10;
        public int saturationAmount = 5;
    }
}