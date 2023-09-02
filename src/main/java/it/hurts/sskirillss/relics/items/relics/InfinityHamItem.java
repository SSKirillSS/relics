package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.api.events.common.ContainerSlotClickEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
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

import java.util.List;

public class InfinityHamItem extends RelicItem {
    public static final String TAG_PIECES = "pieces";
    private static final String TAG_CHARGE = "charge";
    private static final String TAG_POTION = "potion";

    public InfinityHamItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("autophagy", RelicAbilityEntry.builder()
                                .stat("feed", RelicAbilityStat.builder()
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability("infusion", RelicAbilityEntry.builder()
                                .requiredLevel(5)
                                .stat("duration", RelicAbilityStat.builder()
                                        .initialValue(1D, 3.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.5D)
                                        .formatValue(value -> MathUtils.round(value, 1))
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
    public List<ItemStack> processCreativeTab() {
        ItemStack stack = this.getDefaultInstance();

        NBTUtils.setInt(stack, TAG_PIECES, 10);

        return Lists.newArrayList(stack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn.tickCount % 20 != 0 || !(entityIn instanceof Player player)
                || player.isUsingItem())
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

        if (NBTUtils.getInt(stack, TAG_PIECES, 0) > 0
                && (player.getFoodData().needsFood() || player.isCreative()))
            player.startUsingItem(hand);

        return super.use(world, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (!(entity instanceof Player player))
            return;

        if (!player.getFoodData().needsFood() && !player.isCreative()) {
            player.stopUsingItem();

            return;
        }

        if (player.tickCount % 10 != 0)
            return;

        int pieces = NBTUtils.getInt(stack, TAG_PIECES, 0);
        CompoundTag nbt = stack.getOrCreateTag();

        if (pieces > 0) {
            NBTUtils.setInt(stack, TAG_PIECES, --pieces);

            int feed = (int) Math.round(AbilityUtils.getAbilityValue(stack, "autophagy", "feed"));

            player.getFoodData().eat(feed, feed);

            LevelingUtils.addExperience(player, stack, Math.max(1, Math.min(20 - player.getFoodData().getFoodLevel(), feed)));

            if (!AbilityUtils.canUseAbility(stack, "infusion") || !nbt.contains(TAG_POTION, 9))
                return;

            int duration = (int) Math.round(AbilityUtils.getAbilityValue(stack, "infusion", "duration") * 20);

            ListTag list = nbt.getList(TAG_POTION, 10);

            for (int i = 0; i < list.size(); ++i) {
                MobEffectInstance effect = MobEffectInstance.load(list.getCompound(i));

                if (effect == null || effect.getEffect().isInstantenous())
                    continue;

                MobEffectInstance currentEffect = player.getEffect(effect.getEffect());

                player.addEffect(new MobEffectInstance(effect.getEffect(), currentEffect == null ? duration : currentEffect.getDuration() + duration, effect.getAmplifier()));
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

            Player player = event.getEntity();

            ItemStack heldStack = event.getHeldStack();
            ItemStack slotStack = event.getSlotStack();

            if (!(heldStack.getItem() instanceof PotionItem) || !(slotStack.getItem() instanceof InfinityHamItem)
                    || !AbilityUtils.canUseAbility(slotStack, "infusion"))
                return;

            CompoundTag tag = slotStack.getOrCreateTag();
            ListTag list = tag.getList(TAG_POTION, 9);

            List<MobEffectInstance> effects = PotionUtils.getMobEffects(heldStack);

            if (effects.isEmpty()) {
                NBTUtils.clearTag(slotStack, TAG_POTION);
            } else {
                effects = effects.stream().filter(effect -> effect != null && !effect.getEffect().isInstantenous()).toList();

                if (effects.isEmpty())
                    return;

                for (MobEffectInstance effect : effects)
                    list.add(effect.save(new CompoundTag()));

                tag.put(TAG_POTION, list);
            }

            player.containerMenu.setCarried(new ItemStack(Items.GLASS_BOTTLE));
            player.playSound(SoundEvents.BOTTLE_FILL, 1F, 1F);

            event.setCanceled(true);
        }
    }
}