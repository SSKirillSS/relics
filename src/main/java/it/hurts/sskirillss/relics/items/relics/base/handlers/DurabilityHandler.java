package it.hurts.sskirillss.relics.items.relics.base.handlers;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class DurabilityHandler {
    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if (!RelicsConfig.RelicsGeneral.ENABLE_RELIC_DURABILITY.get())
            return;

        LivingEntity entity = event.getEntityLiving();
        List<ItemStack> relics = new ArrayList<>();

        if (entity.getCommandSenderWorld().isClientSide())
            return;

        CuriosApi.getCuriosHelper().getEquippedCurios(entity).ifPresent(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);

                if (stack.getItem() instanceof RelicItem)
                    relics.add(stack);
            }
        });

        if (relics.isEmpty())
            return;

        relics.forEach(relic -> {
            Random random = entity.getRandom();

            if (relic.getDamageValue() - relic.getMaxDamage() < 0
                    && event.getAmount() >= 1F && random.nextFloat() <= 0.2F)
                relic.hurt(1, entity.getRandom(), entity instanceof ServerPlayerEntity
                        ? (ServerPlayerEntity) entity : null);
        });
    }
}