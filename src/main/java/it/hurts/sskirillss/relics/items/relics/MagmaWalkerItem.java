package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.blocks.MagmaStoneBlock;
import it.hurts.sskirillss.relics.init.BlockRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class MagmaWalkerItem extends RelicItem implements ICurioItem, IHasTooltip {
    public MagmaWalkerItem() {
        super(Rarity.RARE);
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.magma_walker.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.magma_walker.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        World world = livingEntity.getCommandSenderWorld();
        if (world.getBlockState(livingEntity.blockPosition().below()) == Fluids.LAVA.getSource().defaultFluidState().createLegacyBlock()) {
            BlockPos pos = livingEntity.blockPosition();
            world.setBlockAndUpdate(pos.below(), BlockRegistry.MAGMA_STONE_BLOCK.get().defaultBlockState());
            world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 1, 1, 1);
        }

        if (world.getBlockState(livingEntity.blockPosition().below()).getBlock() == BlockRegistry.MAGMA_STONE_BLOCK.get()
                && world.getBlockState(livingEntity.blockPosition().below()).getValue(MagmaStoneBlock.AGE) > 0) {
            world.setBlock(livingEntity.blockPosition().below(), BlockRegistry.MAGMA_STONE_BLOCK.get().defaultBlockState(), 2);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class MagmaWalkerServerEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (event.getSource() == DamageSource.HOT_FLOOR) {
                LivingEntity player = event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MAGMA_WALKER.get(), player).isPresent()) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            if (event.getSource() == DamageSource.HOT_FLOOR) {
                LivingEntity player = event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MAGMA_WALKER.get(), player).isPresent()) {
                    event.setCanceled(true);
                }
            }
        }
    }
}