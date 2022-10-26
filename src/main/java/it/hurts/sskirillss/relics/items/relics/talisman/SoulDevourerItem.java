package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class SoulDevourerItem extends RelicItem {
    private static final String TAG_UPDATE_TIME = "time";
    private static final String TAG_SOUL_AMOUNT = "soul";

    public SoulDevourerItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

//    @Override
//    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
//        super.appendHoverText(stack, worldIn, tooltip, flagIn);
//
//        tooltip.add(new TranslatableComponent("tooltip.relics.soul_devourer.tooltip_1", NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0)));
//    }
//
//    @Override
//    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
//        if (DurabilityUtils.isBroken(stack) || !(livingEntity instanceof Player player))
//            return;
//
//        int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
//        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
//
//        if (player.tickCount % 20 == 0) {
//            if (time < stats.soulLooseCooldown)
//                NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
//            else {
//                NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
//                NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, Math.round(Math.max(soul - (soul
//                        * stats.soulLoseMultiplierPerSoul + stats.minSoulLooseAmount), 0)));
//            }
//        }
//    }
//
//    @Mod.EventBusSubscriber(modid = Reference.MODID)
//    public static class SoulDevourerServerEvents {
//        @SubscribeEvent
//        public static void onEntityDeath(LivingDeathEvent event) {
//            Stats stats = INSTANCE.stats;
//
//            if (!(event.getSource().getEntity() instanceof Player player))
//                return;
//
//            LivingEntity target = event.getEntityLiving();
//
//            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.SOUL_DEVOURER.get());
//
//            int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
//            int capacity = stats.soulCapacity;
//
//            if (stack.isEmpty() || soul >= capacity)
//                return;
//
//            NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, Math.min(soul + (int) (target.getMaxHealth()
//                    * stats.soulFromHealthMultiplier), capacity));
//            NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
//        }
//
//        @SubscribeEvent
//        public static void onEntityHurt(LivingHurtEvent event) {
//            Stats stats = INSTANCE.stats;
//
//            if (!(event.getSource().getEntity() instanceof Player player))
//                return;
//
//            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.SOUL_DEVOURER.get());
//
//            if (stack.isEmpty())
//                return;
//
//            int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
//
//            if (soul > 0)
//                event.setAmount(event.getAmount() + (event.getAmount() * (soul * stats.damageMultiplierPerSoul)));
//        }
//    }
}