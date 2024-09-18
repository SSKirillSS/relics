package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.api.events.common.ContainerSlotClickEvent;
import it.hurts.sskirillss.relics.init.CreativeTabRegistry;
import it.hurts.sskirillss.relics.items.misc.CreativeContentConstructor;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

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
                                        .icon(StatIcons.SATURATION)
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("infusion")
                                .requiredLevel(5)
                                .stat(StatData.builder("duration")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(1D, 3.5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.5D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.VILLAGE)
                        .build())
                .build();
    }

    @Override
    public void gatherCreativeTabContent(CreativeContentConstructor constructor) {
        ItemStack stack = this.getDefaultInstance();

        stack.set(CHARGE, 10);

        constructor.entry(CreativeTabRegistry.RELICS_TAB.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, stack);
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

        if (pieces > 0) {
            stack.set(CHARGE, --pieces);

            int feed = (int) Math.round(getStatValue(stack, "autophagy", "feed"));

            player.getFoodData().eat(feed, feed);

            spreadRelicExperience(player, stack, Math.max(1, Math.min(20 - player.getFoodData().getFoodLevel(), feed)));

            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);

            if (!isAbilityUnlocked(stack, "infusion") || contents == null)
                return;

            int duration = (int) Math.round(getStatValue(stack, "infusion", "duration") * 20);

            contents.forEachEffect(effect -> {
                if (!effect.getEffect().value().isInstantenous()) {
                    MobEffectInstance currentEffect = player.getEffect(effect.getEffect());

                    player.addEffect(new MobEffectInstance(effect.getEffect(), currentEffect == null ? duration : currentEffect.getDuration() + duration, effect.getAmplifier()));
                }
            });

            if (pieces <= 0)
                stack.set(DataComponents.POTION_CONTENTS, null);
        } else
            player.stopUsingItem();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.get(DataComponents.POTION_CONTENTS) != null;
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

    @EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onSlotClick(ContainerSlotClickEvent event) {
            if (event.getAction() != ClickAction.PRIMARY)
                return;

            Player player = event.getEntity();

            ItemStack heldStack = event.getHeldStack();
            ItemStack slotStack = event.getSlotStack();

            if (!(heldStack.getItem() instanceof PotionItem) || !(slotStack.getItem() instanceof InfinityHamItem relic)
                    || !relic.isAbilityUnlocked(slotStack, "infusion"))
                return;

            PotionContents contents = heldStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

            List<MobEffectInstance> effects = StreamSupport.stream(contents.getAllEffects().spliterator(), false).toList();

            if (effects.isEmpty()) {
                slotStack.set(DataComponents.POTION_CONTENTS, null);
            } else {
                effects = effects.stream().filter(effect -> !effect.getEffect().value().isInstantenous()).toList();

                if (effects.isEmpty())
                    return;

                slotStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), effects));
            }

            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);

            if (player.containerMenu.getCarried().getCount() <= 1)
                player.containerMenu.setCarried(bottle);
            else {
                player.containerMenu.getCarried().shrink(1);

                EntityUtils.addItem(player, bottle);
            }

            player.playSound(SoundEvents.BOTTLE_FILL, 1F, 1F);

            event.setCanceled(true);
        }
    }
}