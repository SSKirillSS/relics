package it.hurts.sskirillss.relics.items.relics.necklace;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.entities.StalactiteEntity;
import it.hurts.sskirillss.relics.indev.*;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Random;

public class ReflectionNecklaceItem extends RelicItem {
    public static final String TAG_CHARGE = "charge";
    public static final String TAG_TIME = "time";

    public ReflectionNecklaceItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .build());
    }

    @Override
    public RelicDataNew getNewData() {
        return RelicDataNew.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("explode", RelicAbilityEntry.builder()
                                .stat("capacity", RelicAbilityStat.builder()
                                        .initialValue(20D, 60D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 20D)
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
        if (DurabilityUtils.isBroken(stack) || !(slotContext.entity() instanceof Player player)
                || player.tickCount % 20 != 0)
            return;

        int time = NBTUtils.getInt(stack, TAG_TIME, 0);
        double charge = NBTUtils.getDouble(stack, TAG_CHARGE, 0);

        if (time > 0 && charge < getAbilityValue(stack, "explode", "capacity")) {
            --time;

            NBTUtils.setInt(stack, TAG_TIME, time);
        } else if (charge > 0) {
            Level level = player.getLevel();
            Random random = player.getRandom();

            float size = (float) (Math.log(charge) * 0.6F);
            float speed = (float) (0.35F + (charge * 0.001F));

            for (float i = -size; i <= size; i += 1) {
                for (float j = -size; j <= size; j += 1) {
                    for (float k = -size; k <= size; k += 1) {
                        double d3 = (double) j + (random.nextDouble() - random.nextDouble());
                        double d4 = (double) i + (random.nextDouble() - random.nextDouble());
                        double d5 = (double) k + (random.nextDouble() - random.nextDouble());

                        double d6 = (double) Mth.sqrt((float) (d3 * d3 + d4 * d4 + d5 * d5)) / speed + random.nextGaussian();

                        Vec3 motion = new Vec3(d3 / d6, d4 / d6, d5 / d6);

                        float mul = player.getBbHeight() / 1.5F;

                        Vec3 pos = player.position().add(0, mul, 0).add(motion.normalize().multiply(mul, mul, mul));

                        if (level.getBlockState(new BlockPos(pos)).getMaterial().blocksMotion())
                            continue;

                        StalactiteEntity stalactite = new StalactiteEntity(level, (float) charge);

                        stalactite.setOwner(player);
                        stalactite.setPos(pos);
                        stalactite.setDeltaMovement(motion);

                        level.addFreshEntity(stalactite);
                    }
                }
            }

            NBTUtils.setDouble(stack, TAG_CHARGE, 0);
            NBTUtils.setInt(stack, TAG_TIME, 0);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ReflectionNecklaceServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            if (!(event.getEntityLiving() instanceof Player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.REFLECTION_NECKLACE.get());

            if (stack.isEmpty())
                return;

            double charge = NBTUtils.getDouble(stack, TAG_CHARGE, 0);

            if (charge < getAbilityValue(stack, "explode", "capacity")) {
                NBTUtils.setDouble(stack, TAG_CHARGE, charge + (event.getAmount()));

                NBTUtils.setInt(stack, TAG_TIME, 5);
            }
        }
    }
}