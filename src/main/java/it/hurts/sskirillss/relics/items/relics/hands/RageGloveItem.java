package it.hurts.sskirillss.relics.items.relics.hands;

import it.hurts.sskirillss.relics.client.renderer.items.models.RageGloveModel;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

public class RageGloveItem extends RelicItem<RageGloveItem.Stats> {
    public static final String TAG_STACKS_AMOUNT = "stacks";
    public static final String TAG_UPDATE_TIME = "time";

    public static RageGloveItem INSTANCE;

    public RageGloveItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#ffa90c", "#ff0606")
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.dealtDamageMultiplier * 100) + "%")
                        .arg("+" + (int) (stats.incomingDamageMultiplier * 100) + "%")
                        .arg(stats.stackDuration)
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        int stacks = NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0);
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);

        if (DurabilityUtils.isBroken(stack) || livingEntity.tickCount % 20 != 0 || stacks <= 0)
            return;

        if (time > 0)
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
        else
            NBTUtils.setInt(stack, TAG_STACKS_AMOUNT, 0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new RageGloveModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class RageGloveEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;
            Entity source = event.getSource().getEntity();

            if (!(source instanceof LivingEntity))
                return;

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.RAGE_GLOVE.get(), (LivingEntity) source).ifPresent(triple -> {
                ItemStack stack = triple.getRight();

                if (DurabilityUtils.isBroken(stack))
                    return;

                int stacks = NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0);

                NBTUtils.setInt(stack, TAG_STACKS_AMOUNT, ++stacks);
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, stats.stackDuration);

                event.setAmount(event.getAmount() + (event.getAmount() * (stacks * stats.dealtDamageMultiplier)));
            });

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.RAGE_GLOVE.get(), event.getEntityLiving()).ifPresent(triple -> {
                ItemStack stack = triple.getRight();

                if (DurabilityUtils.isBroken(stack))
                    return;

                int stacks = NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0);

                if (stacks <= 0)
                    return;

                event.setAmount(event.getAmount() + (event.getAmount() * (stacks * stats.incomingDamageMultiplier)));
            });
        }
    }

    public static class Stats extends RelicStats {
        public int stackDuration = 3;
        public float dealtDamageMultiplier = 0.075F;
        public float incomingDamageMultiplier = 0.025F;
    }
}