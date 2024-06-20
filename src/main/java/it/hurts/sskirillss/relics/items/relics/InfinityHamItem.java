package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.misc.Backgrounds;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

import static it.hurts.sskirillss.relics.init.DataComponentRegistry.CHARGE;
import static it.hurts.sskirillss.relics.init.DataComponentRegistry.TIME;

public class InfinityHamItem extends RelicItem {
    private static final String TAG_POTION = "potion";

    public InfinityHamItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("autophagy")
                                .stat(StatData.builder("feed")
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("infusion")
                                .requiredLevel(5)
                                .stat(StatData.builder("duration")
                                        .initialValue(1D, 3.5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.5D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .style(StyleData.builder()
                        .background(Backgrounds.PLAINS)
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.VILLAGE)
                        .build())
                .build();
    }

    @Override
    public List<ItemStack> processCreativeTab() {
        ItemStack stack = this.getDefaultInstance();

        stack.set(CHARGE, 10);

        return Lists.newArrayList(stack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn.tickCount % 20 != 0 || !(entityIn instanceof Player player)
                || player.isUsingItem())
            return;

        int pieces = stack.getOrDefault(CHARGE, 0);

        if (pieces >= 10)
            return;

        int charge = stack.getOrDefault(TIME, 0);

        if (charge >= 10) {
            stack.set(CHARGE, ++pieces);
            stack.set(TIME, 0);
        } else
            stack.set(TIME, ++charge);

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getOrDefault(CHARGE, 0) > 0
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

        int pieces = stack.getOrDefault(CHARGE, 0);
        CompoundTag nbt = NBTUtils.getOrCreateTag(stack);

        if (pieces > 0) {
            stack.set(CHARGE, --pieces);

            int feed = (int) Math.round(getStatValue(stack, "autophagy", "feed"));

            player.getFoodData().eat(feed, feed);

            spreadExperience(player, stack, Math.max(1, Math.min(20 - player.getFoodData().getFoodLevel(), feed)));

            if (!canUseAbility(stack, "infusion") || !nbt.contains(TAG_POTION, 9))
                return;

            int duration = (int) Math.round(getStatValue(stack, "infusion", "duration") * 20);

            ListTag list = nbt.getList(TAG_POTION, 10);

            for (int i = 0; i < list.size(); ++i) {
                MobEffectInstance effect = MobEffectInstance.load(list.getCompound(i));

                if (effect == null || effect.getEffect().value().isInstantenous())
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
        return NBTUtils.getOrCreateTag(stack).contains(TAG_POTION);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, LivingEntity entity) {
        return 50;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

//    @EventBusSubscriber
//    public static class Events {
//        @SubscribeEvent
//        public static void onSlotClick(ContainerSlotClickEvent event) {
//            if (event.getAction() != ClickAction.PRIMARY)
//                return;
//
//            Player player = event.getEntity();
//
//            ItemStack heldStack = event.getHeldStack();
//            ItemStack slotStack = event.getSlotStack();
//
//            if (!(heldStack.getItem() instanceof PotionItem) || !(slotStack.getItem() instanceof InfinityHamItem relic)
//                    || !relic.canUseAbility(slotStack, "infusion"))
//                return;
//
//            CompoundTag tag = NBTUtils.getOrCreateTag(slotStack);
//            ListTag list = tag.getList(TAG_POTION, 9);
//
//            List<MobEffectInstance> effects = PotionUtils.getMobEffects(heldStack);
//
//            if (effects.isEmpty()) {
//                NBTUtils.clearTag(slotStack, TAG_POTION);
//            } else {
//                effects = effects.stream().filter(effect -> effect != null && !effect.getEffect().isInstantenous()).toList();
//
//                if (effects.isEmpty())
//                    return;
//
//                for (MobEffectInstance effect : effects)
//                    list.add(effect.save(new CompoundTag()));
//
//                tag.put(TAG_POTION, list);
//            }
//
//            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
//
//            if (player.containerMenu.getCarried().getCount() <= 1)
//                player.containerMenu.setCarried(bottle);
//            else {
//                player.containerMenu.getCarried().shrink(1);
//
//                EntityUtils.addItem(player, bottle);
//            }
//
//            player.playSound(SoundEvents.BOTTLE_FILL, 1F, 1F);
//
//            event.setCanceled(true);
//        }
//    }
}