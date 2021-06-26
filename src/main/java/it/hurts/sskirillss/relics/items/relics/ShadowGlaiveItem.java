package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.entities.ShadowGlaiveEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Collections;
import java.util.List;

public class ShadowGlaiveItem extends RelicItem<ShadowGlaiveItem.Stats> {
    public static ShadowGlaiveItem INSTANCE;

    public ShadowGlaiveItem() {
        super(Rarity.EPIC);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.shadow_glaive.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.shadow_glaive.shift_2"));
        return tooltip;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.END_CITY_TREASURE);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ShadowGlaiveServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingDamageEvent event) {
            Stats config = INSTANCE.config;
            if (!(event.getSource().getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            if (player.getOffhandItem().getItem() != ItemRegistry.SHADOW_GLAIVE.get()
                    || player.getCooldowns().isOnCooldown(ItemRegistry.SHADOW_GLAIVE.get())) return;
            World world = player.getCommandSenderWorld();
            if (world.getRandom().nextFloat() > config.summonChance || event.getAmount() < config.minDamageForSummon) return;
            LivingEntity entity = event.getEntityLiving();
            if (entity == null || !entity.isAlive() || player.position().distanceTo(entity.position()) > config.maxDistanceForSummon) return;
            ShadowGlaiveEntity glaive = new ShadowGlaiveEntity(world, player);
            glaive.setDamage(event.getAmount() * config.initialDamageMultiplier);
            glaive.setOwner(player);
            glaive.setTarget(entity);
            glaive.teleportTo(player.getX(), player.getY() + player.getBbHeight() * 0.5F, player.getZ());
            player.getCooldowns().addCooldown(ItemRegistry.SHADOW_GLAIVE.get(), config.summonCooldown * 20);
            world.addFreshEntity(glaive);
        }
    }

    public static class Stats extends RelicStats {
        public float summonChance = 0.35F;
        public float minDamageForSummon = 1.0F;
        public int maxDistanceForSummon = 5;
        public float initialDamageMultiplier = 1.25F;
        public int summonCooldown = 1;
        public float bounceChanceMultiplier = 0.025F;
        public int bounceRadius = 7;
        public float projectileSpeed = 0.45F;
        public int maxBounces = 10;
        public float minDamagePerBounce = 1.0F;
        public float damageMultiplierPerBounce = 0.05F;
    }
}