package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class CamouflageRingItem extends RelicItem<CamouflageRingItem.Stats> implements ICurioItem, IHasTooltip {
    public static final String TAG_TIME = "time";
    public static final String TAG_IS_ACTIVE = "active";

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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        int time = NBTUtils.getInt(stack, TAG_TIME, 0);
        if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)) {
            if (time > 0 && livingEntity.isShiftKeyDown()
                    && livingEntity.getCommandSenderWorld().getBlockState(livingEntity.blockPosition()).getBlock() instanceof DoublePlantBlock) {
                if (livingEntity.tickCount % 20 == 0) NBTUtils.setInt(stack, TAG_TIME, time - 1);
                livingEntity.setInvisible(true);
            } else {
                NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, false);
                livingEntity.setInvisible(false);
            }
        } else {
            if (livingEntity.tickCount % 20 == 0 && time < config.invisibilityTime) {
                NBTUtils.setInt(stack, TAG_TIME, time + 1);
            }
            if (livingEntity.isShiftKeyDown() && time > 0
                    && livingEntity.getCommandSenderWorld().getBlockState(livingEntity.blockPosition()).getBlock() instanceof DoublePlantBlock) {
                NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, true);
            }
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

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class CamouflageRingServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            if (event.getSource().getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CAMOUFLAGE_RING.get(), player).isPresent()) {
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CAMOUFLAGE_RING.get(), player).get().getRight();
                    if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)) {
                        event.setAmount(event.getAmount() * config.damageMultiplier);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            if (event.getState().getBlock() instanceof DoublePlantBlock
                    && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CAMOUFLAGE_RING.get(), event.getPlayer()).isPresent()
                    && NBTUtils.getBoolean(CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CAMOUFLAGE_RING.get(), event.getPlayer()).get().getRight(), TAG_IS_ACTIVE, false)) {
                event.setCanceled(true);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class CamouflageRingClientEvents {
        @SubscribeEvent
        public static void onEntityRender(RenderPlayerEvent.Pre event) {
            PlayerEntity player = event.getPlayer();
            if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CAMOUFLAGE_RING.get(), player).isPresent()
                    && NBTUtils.getBoolean(CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CAMOUFLAGE_RING.get(), player).get().getRight(), TAG_IS_ACTIVE, false))
                event.setCanceled(true);
        }
    }

    public static class Stats extends RelicStats {
        public int invisibilityTime = 60;
        public float damageMultiplier = 2.0F;
    }
}