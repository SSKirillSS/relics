package it.hurts.sskirillss.relics.items.relics.base.handlers;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

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
            int durability = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, relic) + 1;

            if (entity.getRandom().nextFloat() <= 0.75F / durability
                    && event.getAmount() >= 1F)
                relic.setDamageValue(Math.min(relic.getMaxDamage(), relic.getDamageValue() + 1));
        });
    }
}