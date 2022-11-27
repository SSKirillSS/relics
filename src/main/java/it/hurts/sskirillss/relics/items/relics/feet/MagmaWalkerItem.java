package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.api.events.common.FluidCollisionEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

public class MagmaWalkerItem extends RelicItem {
    public static final String TAG_HEAT = "heat";

    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("pace", RelicAbilityEntry.builder()
                                .stat("heat", RelicAbilityStat.builder()
                                        .initialValue(20D, 50D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 0)))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 200))
                .styleData(RelicStyleData.builder()
                        .borders("#dc41ff", "#832698")
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        int heat = NBTUtils.getInt(stack, TAG_HEAT, 0);

        if (!(slotContext.getWearer() instanceof Player player) || player.tickCount % 20 != 0)
            return;

        if (heat > 0) {
            if (heat > getAbilityValue(stack, "pace", "heat"))
                player.hurt(DamageSource.HOT_FLOOR, (float) (1F + ((heat - getAbilityValue(stack, "pace", "heat")) / 10F)));

            Level level = player.level;

            if (!level.getFluidState(player.blockPosition().below()).is(FluidTags.LAVA)
                    && !level.getFluidState(player.blockPosition()).is(FluidTags.LAVA))
                NBTUtils.setInt(stack, TAG_HEAT, --heat);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntity(), ItemRegistry.MAGMA_WALKER.get());

            if (!stack.isEmpty() && event.getSource() == DamageSource.HOT_FLOOR
                    && NBTUtils.getInt(stack, TAG_HEAT, 0) <= getAbilityValue(stack, "pace", "heat")) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onFluidCollide(FluidCollisionEvent event) {
            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntity(), ItemRegistry.MAGMA_WALKER.get());

            if (!(event.getEntity() instanceof Player player) || stack.isEmpty()
                    || !event.getFluid().is(FluidTags.LAVA) || player.isShiftKeyDown())
                return;

            if (player.tickCount % 20 == 0) {
                int heat = NBTUtils.getInt(stack, TAG_HEAT, 0);

                NBTUtils.setInt(stack, TAG_HEAT, ++heat);

                if (heat % 5 == 0)
                    addExperience(player, stack, 1);
            }

            System.out.println(123);

            event.setCanceled(true);
        }
    }
}