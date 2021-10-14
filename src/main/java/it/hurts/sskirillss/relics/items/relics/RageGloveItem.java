package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.RageGloveModel;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class RageGloveItem extends RelicItem<RageGloveItem.Stats> implements ICurioItem {
    public static final String TAG_STACKS_AMOUNT = "stacks";
    public static final String TAG_UPDATE_TIME = "time";

    public static RageGloveItem INSTANCE;

    public RageGloveItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .model(new RageGloveModel())
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.NETHER)
                        .chance(0.15F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.dealtDamageMultiplier * 100) + "%")
                        .varArg("+" + (int) (config.incomingDamageMultiplier * 100) + "%")
                        .varArg(config.stackDuration)
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        int stacks = NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0);
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);

        if (isBroken(stack) || livingEntity.tickCount % 20 != 0 || stacks <= 0)
            return;

        if (time > 0)
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
        else
            NBTUtils.setInt(stack, TAG_STACKS_AMOUNT, 0);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class RageGloveEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            Entity source = event.getSource().getEntity();

            if (!(source instanceof LivingEntity))
                return;

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.RAGE_GLOVE.get(), (LivingEntity) source).ifPresent(triple -> {
                ItemStack stack = triple.getRight();

                if (isBroken(stack))
                    return;

                int stacks = NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0);

                NBTUtils.setInt(stack, TAG_STACKS_AMOUNT, ++stacks);
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, config.stackDuration);

                event.setAmount(event.getAmount() + (event.getAmount() * (stacks * config.dealtDamageMultiplier)));
            });

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.RAGE_GLOVE.get(), event.getEntityLiving()).ifPresent(triple -> {
                ItemStack stack = triple.getRight();

                if (isBroken(stack))
                    return;

                int stacks = NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0);

                if (stacks <= 0)
                    return;

                event.setAmount(event.getAmount() + (event.getAmount() * (stacks * config.incomingDamageMultiplier)));
            });
        }
    }

    public static class Stats extends RelicStats {
        public int stackDuration = 3;
        public float dealtDamageMultiplier = 0.075F;
        public float incomingDamageMultiplier = 0.025F;
    }
}