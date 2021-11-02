package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class SoulDevourerItem extends RelicItem<SoulDevourerItem.Stats> implements ICurioItem {
    private static final String TAG_UPDATE_TIME = "time";
    private static final String TAG_SOUL_AMOUNT = "soul";

    public static SoulDevourerItem INSTANCE;

    public SoulDevourerItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(LootTables.END_CITY_TREASURE.toString())
                        .chance(0.1F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .ability(AbilityTooltip.builder()
                        .arg((int) (config.soulFromHealthMultiplier * 100) + "%")
                        .arg(config.damageMultiplierPerSoul + "%")
                        .build())
                .build();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        tooltip.add(new TranslationTextComponent("tooltip.relics.soul_devourer.tooltip_1", NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0)));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (IRepairableItem.isBroken(stack) || !(livingEntity instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) livingEntity;
        int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);

        if (player.tickCount % 20 == 0) {
            if (time < config.soulLooseCooldown)
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
            else {
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, Math.round(Math.max(soul - (soul
                        * config.soulLoseMultiplierPerSoul + config.minSoulLooseAmount), 0)));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SoulDevourerServerEvents {
        @SubscribeEvent
        public static void onEntityDeath(LivingDeathEvent event) {
            Stats config = INSTANCE.config;

            if (!(event.getSource().getEntity() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            LivingEntity target = event.getEntityLiving();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.SOUL_DEVOURER.get());

            int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
            int capacity = config.soulCapacity;

            if (stack.isEmpty() || soul >= capacity)
                return;

            NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, Math.min(soul + (int) (target.getMaxHealth()
                    * config.soulFromHealthMultiplier), capacity));
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
        }

        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;

            if (!(event.getSource().getEntity() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.SOUL_DEVOURER.get());

            if (stack.isEmpty())
                return;

            int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);

            if (soul > 0)
                event.setAmount(event.getAmount() + (event.getAmount() * (soul * config.damageMultiplierPerSoul)));
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