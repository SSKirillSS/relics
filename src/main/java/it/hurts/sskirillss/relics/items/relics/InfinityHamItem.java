package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class InfinityHamItem extends RelicItem<InfinityHamItem.Stats> implements ICurioItem {
    public static final String TAG_PIECES = "pieces";
    private static final String TAG_CHARGE = "charge";

    public InfinityHamItem() {
        super(new Item.Properties().tab(RelicsTab.RELICS_TAB).stacksTo(1).rarity(Rarity.RARE).food(new Food.Builder().build()));
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.infinity_ham.shift_1"));
        return tooltip;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn.tickCount % 20 != 0) return;
        int pieces = NBTUtils.getInt(stack, TAG_PIECES, 0);
        if (pieces >= config.maxPieces) return;
        int charge = NBTUtils.getInt(stack, TAG_CHARGE, 0);
        if (charge >= config.rechargeTime) {
            NBTUtils.setInt(stack, TAG_PIECES, pieces + 1);
            NBTUtils.setInt(stack, TAG_CHARGE, 0);
        } else NBTUtils.setInt(stack, TAG_CHARGE, charge + 1);
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (NBTUtils.getInt(stack, TAG_PIECES, 0) > 0
                && player.getFoodData().needsFood()) return super.use(world, player, hand);
        return ActionResult.fail(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity)) return stack;
        PlayerEntity player = (PlayerEntity) entity;
        int pieces = NBTUtils.getInt(stack, TAG_PIECES, 0);
        if (pieces > 0) {
            NBTUtils.setInt(stack, TAG_PIECES, pieces - 1);
            player.getFoodData().setFoodLevel(Math.min(20, player.getFoodData().getFoodLevel() + config.feedAmount));
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

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.CAVE;
    }

    @Override
    public Class<InfinityHamItem.Stats> getConfigClass() {
        return InfinityHamItem.Stats.class;
    }

    public static class Stats extends RelicStats {
        public int rechargeTime = 60;
        public int useDuration = 80;
        public int maxPieces = 3;
        public int feedAmount = 10;
    }
}