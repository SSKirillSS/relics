package it.hurts.sskirillss.relics.items.relics.base.handlers;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.BrokenRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicDurability;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class DurabilityHandler {
    public static HashMap<RelicItem, RelicDurability> DURABILITY = new HashMap<>();

    public static void damageRelic(MutablePair<ItemStack, Integer> pair, LivingEntity wearer, int damage) {
        ItemStack stack = pair.getLeft();
        Random random = wearer.getRandom();

        stack.hurt(damage, random, wearer instanceof ServerPlayerEntity
                ? (ServerPlayerEntity) wearer : null);

        if (stack.getDamageValue() - stack.getMaxDamage() >= 0) {
            RelicItem<?> relic = (RelicItem<?>) stack.getItem();

            if (relic.getData().isHasScrap()) {
                for (RegistryObject<Item> object : ItemRegistry.BROKEN_RELICS.getEntries()) {
                    if (!object.isPresent())
                        continue;

                    Item item = object.get();

                    if (!(item instanceof BrokenRelicItem) || !item.getRegistryName().getPath().replace("broken_", "")
                            .equals(stack.getItem().getRegistryName().getPath()))
                        continue;

                    CuriosApi.getCuriosHelper().getEquippedCurios(wearer)
                            .ifPresent(handler -> handler.setStackInSlot(pair.getRight(), new ItemStack(item)));

                    break;
                }
            } else {
                ItemStack scrap;
                List<ItemStack> scraps = new ArrayList<>();
                int ordinal = stack.getRarity().ordinal();

                if (ordinal >= 0) {
                    scrap = new ItemStack(ItemRegistry.COMMON_SCRAP.get());
                    scrap.setCount(random.nextInt(5));

                    scraps.add(scrap);
                }

                if (ordinal >= 1) {
                    scrap = new ItemStack(ItemRegistry.UNCOMMON_SCRAP.get());
                    scrap.setCount(random.nextInt(4));

                    scraps.add(scrap);
                }

                if (ordinal >= 2) {
                    scrap = new ItemStack(ItemRegistry.RARE_SCRAP.get());
                    scrap.setCount(random.nextInt(3));

                    scraps.add(scrap);
                }

                if (ordinal >= 3) {
                    scrap = new ItemStack(ItemRegistry.EPIC_SCRAP.get());
                    scrap.setCount(random.nextInt(2));

                    scraps.add(scrap);
                }

                scraps.forEach(item -> {
                    World world = wearer.getCommandSenderWorld();

                    ItemEntity drop = new ItemEntity(world, wearer.getX(), wearer.getY(), wearer.getZ(), item);
                    drop.setNoPickUpDelay();

                    world.addFreshEntity(drop);
                });

                stack.shrink(1);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if (!RelicsConfig.RelicsGeneral.ENABLE_RELIC_DURABILITY.get())
            return;

        LivingEntity entity = event.getEntityLiving();
        List<MutablePair<ItemStack, Integer>> relics = new ArrayList<>();

        if (entity.getCommandSenderWorld().isClientSide())
            return;

        CuriosApi.getCuriosHelper().getEquippedCurios(entity).ifPresent(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);

                if (stack.getItem() instanceof RelicItem)
                    relics.add(new MutablePair<>(stack, i));
            }
        });

        if (relics.isEmpty())
            return;

        relics.forEach(relic -> {
            Random random = entity.getRandom();

            if (event.getAmount() >= 1F && random.nextFloat() <= 0.2F)
                damageRelic(relic, entity, 1);
        });
    }
}