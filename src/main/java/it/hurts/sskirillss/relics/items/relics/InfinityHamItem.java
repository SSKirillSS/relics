package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.api.events.ContainerSlotClickEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.indev.*;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

public class InfinityHamItem extends RelicItem {
    public static final String TAG_PIECES = "pieces";
    private static final String TAG_CHARGE = "charge";
    private static final String TAG_POTION = "potion";

    private static final String TAG_USES = "uses";

    public InfinityHamItem() {
        super(RelicData.builder()
                .properties(new Item.Properties()
                        .tab(RelicsTab.RELICS_TAB)
                        .stacksTo(1)
                        .rarity(Rarity.RARE))
                .build());
    }

    @Override
    public RelicDataNew getNewData() {
        return RelicDataNew.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("autophagy", RelicAbilityEntry.builder()
                                .stat("feed", RelicAbilityStat.builder()
                                        .initialValue(1D, 2D)
                                        .upgradeModifier("add", 0.5D)
                                        .build())
                                .build())
                        .ability("infusion", RelicAbilityEntry.builder()
                                .requiredLevel(5)
                                .stat("duration", RelicAbilityStat.builder()
                                        .initialValue(1, 5)
                                        .upgradeModifier("add", 3)
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#ffe0d2", "#9c756b")
                        .build())
                .build();
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
        if (group != RelicsTab.RELICS_TAB)
            return;

        ItemStack stack = new ItemStack(ItemRegistry.INFINITY_HAM.get());

        NBTUtils.setInt(stack, TAG_PIECES, 10);

        items.add(stack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn.tickCount % 20 != 0 || !(entityIn instanceof Player player)
                || player.isUsingItem() || DurabilityUtils.isBroken(stack))
            return;

        int pieces = NBTUtils.getInt(stack, TAG_PIECES, 0);

        if (pieces >= 10)
            return;

        int charge = NBTUtils.getInt(stack, TAG_CHARGE, 0);

        if (charge >= 10) {
            NBTUtils.setInt(stack, TAG_PIECES, pieces + 1);
            NBTUtils.setInt(stack, TAG_CHARGE, 0);
        } else
            NBTUtils.setInt(stack, TAG_CHARGE, charge + 1);

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!DurabilityUtils.isBroken(stack) && NBTUtils.getInt(stack, TAG_PIECES, 0) > 0
                && player.getFoodData().needsFood())
            player.startUsingItem(hand);

        return super.use(world, player, hand);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
        if (!(entity instanceof Player player))
            return;

        if (!player.getFoodData().needsFood()) {
            player.stopUsingItem();

            return;
        }

        if (player.tickCount % 10 != 0)
            return;

        int pieces = NBTUtils.getInt(stack, TAG_PIECES, 0);
        CompoundTag nbt = stack.getOrCreateTag();

        if (pieces > 0) {
            NBTUtils.setInt(stack, TAG_PIECES, --pieces);

            int feed = (int) Math.round(getAbilityValue(stack, "autophagy", "feed"));

            player.getFoodData().eat(feed, feed);

            int uses = NBTUtils.getInt(stack, TAG_USES, 0);

            if (uses < 5)
                NBTUtils.setInt(stack, TAG_USES, ++uses);
            else {
                NBTUtils.setInt(stack, TAG_USES, 0);

                addExperience(stack, 1);
            }

            if (!canUseAbility(stack, "infusion") || !nbt.contains(TAG_POTION, 9))
                return;

            ListTag list = nbt.getList(TAG_POTION, 10);

            for (int i = 0; i < list.size(); ++i) {
                MobEffectInstance effect = MobEffectInstance.load(list.getCompound(i));

                if (effect == null)
                    continue;

                MobEffectInstance currentEffect = player.getEffect(effect.getEffect());

                player.addEffect(new MobEffectInstance(effect.getEffect(), currentEffect == null ? 100 : currentEffect.getDuration() + 100, effect.getAmplifier()));
            }

            if (pieces <= 0 && nbt.contains(TAG_POTION))
                nbt.remove(TAG_POTION);
        } else
            player.stopUsingItem();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrCreateTag().contains(TAG_POTION);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 50;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.EAT;
    }

    @Mod.EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onSlotClick(ContainerSlotClickEvent event) {
            if (event.getAction() != ClickAction.PRIMARY)
                return;

            Player player = event.getPlayer();

            ItemStack heldStack = event.getHeldStack();
            ItemStack slotStack = event.getSlotStack();

            if (!(heldStack.getItem() instanceof PotionItem) || !(slotStack.getItem() instanceof InfinityHamItem)
                    || slotStack.getOrCreateTag().contains(InfinityHamItem.TAG_POTION) || !RelicItem.canUseAbility(slotStack, "infusion"))
                return;

            CompoundTag tag = slotStack.getOrCreateTag();
            ListTag list = tag.getList(InfinityHamItem.TAG_POTION, 9);

            for (MobEffectInstance mobeffectinstance : PotionUtils.getMobEffects(heldStack))
                list.add(mobeffectinstance.save(new CompoundTag()));

            tag.put(InfinityHamItem.TAG_POTION, list);

            player.containerMenu.setCarried(new ItemStack(Items.GLASS_BOTTLE));
            player.level.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.MASTER, 1F, 1F);

            event.setCanceled(true);
        }
    }
}