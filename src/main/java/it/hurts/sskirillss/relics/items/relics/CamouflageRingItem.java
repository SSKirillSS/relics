package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Optional;

public class CamouflageRingItem extends RelicItem<CamouflageRingItem.Stats> implements ICurioItem {
    public static final String TAG_TIME = "time";

    public static CamouflageRingItem INSTANCE;

    public CamouflageRingItem() {
        super(Rarity.UNCOMMON);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.camouflage_ring.shift_1"));
        return tooltip;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity.tickCount % 20 != 0 || !(livingEntity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) livingEntity;
        int time = NBTUtils.getInt(stack, TAG_TIME, 0);
        if (canHide(player)) {
            player.addEffect(new EffectInstance(Effects.INVISIBILITY, 20, 0, false, false));
            NBTUtils.setInt(stack, TAG_TIME, time + 1);
        } else {
            if (time <= 0) return;
            if (!player.getCooldowns().isOnCooldown(stack.getItem()))
                player.getCooldowns().addCooldown(stack.getItem(), config.invisibilityTime * 20);
            NBTUtils.setInt(stack, TAG_TIME, time - 1);
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.CAVE;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    private static boolean canHide(LivingEntity entity) {
        Stats config = INSTANCE.config;
        if (!(entity instanceof PlayerEntity)) return false;
        PlayerEntity player = (PlayerEntity) entity;
        Optional<ImmutableTriple<String, Integer, ItemStack>> optional = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CAMOUFLAGE_RING.get(), player);
        if (optional.isPresent()) {
            ItemStack stack = optional.get().getRight();
            return player.getCommandSenderWorld().getBlockState(player.blockPosition()).getBlock() instanceof DoublePlantBlock && player.isCrouching()
                    && NBTUtils.getInt(stack, TAG_TIME, 0) < config.invisibilityTime && !player.getCooldowns().isOnCooldown(stack.getItem());
        }
        return false;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class CamouflageRingServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            if (!(event.getSource().getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            if (canHide(player)) event.setAmount(event.getAmount() * config.damageMultiplier);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class CamouflageRingClientEvents {
        @SubscribeEvent
        public static void onEntityRender(RenderPlayerEvent.Pre event) {
            PlayerEntity player = event.getPlayer();
            if (canHide(player)) event.setCanceled(true);
        }
    }

    public static class Stats extends RelicStats {
        public int invisibilityTime = 60;
        public float damageMultiplier = 2.0F;
    }
}