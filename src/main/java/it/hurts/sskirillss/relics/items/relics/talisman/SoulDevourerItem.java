package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

public class SoulDevourerItem extends RelicItem<SoulDevourerItem.Stats> {
    private static final String TAG_UPDATE_TIME = "time";
    private static final String TAG_SOUL_AMOUNT = "soul";

    public static SoulDevourerItem INSTANCE;

    public SoulDevourerItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#8261be", "#433994")
                .ability(AbilityTooltip.builder()
                        .arg((int) (stats.soulFromHealthMultiplier * 100) + "%")
                        .arg(stats.damageMultiplierPerSoul + "%")
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        tooltip.add(new TranslatableComponent("tooltip.relics.soul_devourer.tooltip_1", NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0)));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (DurabilityUtils.isBroken(stack) || !(livingEntity instanceof Player player))
            return;

        int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);

        if (player.tickCount % 20 == 0) {
            if (time < stats.soulLooseCooldown)
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
            else {
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, Math.round(Math.max(soul - (soul
                        * stats.soulLoseMultiplierPerSoul + stats.minSoulLooseAmount), 0)));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SoulDevourerServerEvents {
        @SubscribeEvent
        public static void onEntityDeath(LivingDeathEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getSource().getEntity() instanceof Player player))
                return;

            LivingEntity target = event.getEntityLiving();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.SOUL_DEVOURER.get());

            int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
            int capacity = stats.soulCapacity;

            if (stack.isEmpty() || soul >= capacity)
                return;

            NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, Math.min(soul + (int) (target.getMaxHealth()
                    * stats.soulFromHealthMultiplier), capacity));
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
        }

        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getSource().getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.SOUL_DEVOURER.get());

            if (stack.isEmpty())
                return;

            int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);

            if (soul > 0)
                event.setAmount(event.getAmount() + (event.getAmount() * (soul * stats.damageMultiplierPerSoul)));
        }
    }

    public static class Stats extends RelicStats {
        public int soulLooseCooldown = 10;
        public int minSoulLooseAmount = 5;
        public float soulLoseMultiplierPerSoul = 0.1F;
        public int soulCapacity = 100;
        public float soulFromHealthMultiplier = 0.25F;
        public float damageMultiplierPerSoul = 0.1F;
    }
}