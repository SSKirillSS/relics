package it.hurts.sskirillss.relics.items.relics.ring;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class DelayRingItem extends RelicItem {
    public static final String TAG_UPDATE_TIME = "time";
    public static final String TAG_STORED_AMOUNT = "amount";
    public static final String TAG_KILLER_UUID = "killer";

    public DelayRingItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

//    @Override
//    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
//        super.appendHoverText(stack, worldIn, tooltip, flagIn);
//
//        if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0) > 0) {
//            tooltip.add(new TranslatableComponent("tooltip.relics.delay_ring.tooltip_1", NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0)));
//            tooltip.add(new TranslatableComponent("tooltip.relics.delay_ring.tooltip_2", NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)));
//        }
//    }
//
//    @Override
//    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
//        if (!(livingEntity instanceof Player player) || DurabilityUtils.isBroken(stack))
//            return;
//
//        Level world = player.getCommandSenderWorld();
//        int points = NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0);
//        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, -1);
//
//        if (player.tickCount % 4 == 0 && time > 0)
//            world.addParticle(points > 0 ? ParticleTypes.HEART : ParticleTypes.ANGRY_VILLAGER,
//                    player.getX() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
//                    player.getEyeY() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
//                    player.getZ() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
//                    0, -0.25F, 0);
//
//        if (world.isClientSide()
//                || player.getCooldowns().isOnCooldown(stack.getItem()))
//            return;
//
//        if (time > 0) {
//            if (player.tickCount % 20 == 0) {
//                NBTUtils.setInt(stack, TAG_UPDATE_TIME, --time);
//
//                world.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK,
//                        SoundSource.MASTER, 0.75F, 1.0F + time * 0.1F);
//            }
//        } else if (time == 0)
//            delay(player, stack);
//    }
//
//    private void delay(LivingEntity entity, ItemStack stack) {
//        if (!(entity instanceof Player player) || DurabilityUtils.isBroken(stack))
//            return;
//
//        NBTUtils.setInt(stack, TAG_UPDATE_TIME, -1);
//
//        Level world = player.getCommandSenderWorld();
//        int points = NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0);
//
//        world.playSound(null, player.blockPosition(), SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.MASTER, 1.0F, 1.0F);
//        player.getCooldowns().addCooldown(stack.getItem(), stats.useCooldown * 20);
//
//        if (points > 0)
//            player.heal(points);
//        else {
//            String uuidString = NBTUtils.getString(stack, TAG_KILLER_UUID, "");
//            DamageSource source = DamageSource.GENERIC;
//
//            if (!uuidString.equals("")) {
//                Player killer = world.getPlayerByUUID(UUID.fromString(uuidString));
//
//                if (killer != null)
//                    source = DamageSource.playerAttack(killer);
//            }
//
//            player.hurt(source, Integer.MAX_VALUE);
//        }
//
//        NBTUtils.setInt(stack, TAG_STORED_AMOUNT, 0);
//        NBTUtils.setString(stack, TAG_KILLER_UUID, "");
//    }
//
//    @Override
//    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
//        if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, -1) > -1
//                && newStack.getItem() != stack.getItem())
//            delay(slotContext.getWearer(), stack);
//    }
//
//    @Mod.EventBusSubscriber(modid = Reference.MODID)
//    public static class DelayRingEvents {
//        @SubscribeEvent(priority = EventPriority.HIGHEST)
//        public static void onEntityDeath(LivingDeathEvent event) {
//            if (!(event.getEntityLiving() instanceof Player player))
//                return;
//
//            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.DELAY_RING.get());
//
//            if (stack.isEmpty())
//                return;
//
//            Entity source = event.getSource().getEntity();
//
//            if (source instanceof Player)
//                NBTUtils.setString(stack, TAG_KILLER_UUID, source.getUUID().toString());
//
//            NBTUtils.setInt(stack, TAG_UPDATE_TIME, stats.delayDuration);
//            NBTUtils.setInt(stack, TAG_STORED_AMOUNT, 0);
//
//            player.setHealth(1.0F);
//
//            event.setCanceled(true);
//        }
//
//        @SubscribeEvent
//        public static void onEntityHurt(LivingHurtEvent event) {
//            Stats stats = INSTANCE.stats;
//
//            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.DELAY_RING.get());
//
//            if (stack.isEmpty() || NBTUtils.getInt(stack, TAG_UPDATE_TIME, -1) < 0)
//                return;
//
//            NBTUtils.setInt(stack, TAG_STORED_AMOUNT, NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)
//                    - Math.round(event.getAmount() * stats.damageMultiplier));
//
//            event.setCanceled(true);
//        }
//
//        @SubscribeEvent
//        public static void onEntityHeal(LivingHealEvent event) {
//            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.DELAY_RING.get());
//
//            if (stack.isEmpty() || NBTUtils.getInt(stack, TAG_UPDATE_TIME, -1) < 0)
//                return;
//
//            NBTUtils.setInt(stack, TAG_STORED_AMOUNT, NBTUtils.getInt(stack, TAG_STORED_AMOUNT, 0)
//                    + Math.round(event.getAmount() * stats.healMultiplier));
//
//            event.setCanceled(true);
//        }
//    }
}