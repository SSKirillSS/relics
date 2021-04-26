package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagicMirrorItem extends Item implements IHasTooltip {
    public MagicMirrorItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public java.util.List<ITextComponent> getShiftTooltip() {
        java.util.List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.magic_mirror.shift_1"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.getCooldownTracker().hasCooldown(ItemRegistry.MAGIC_MIRROR.get())
                || worldIn.isRemote()) return ActionResult.resultFail(stack);
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerIn;
        BlockPos pos = serverPlayer.func_241140_K_();
        ServerWorld world = worldIn.getDimensionKey() == serverPlayer.func_241141_L_()
                ? (ServerWorld) worldIn : serverPlayer.getServer().getWorld(serverPlayer.func_241141_L_());
        if (pos != null && world != null) {
            EntityUtils.teleportWithMount(playerIn, world, new Vector3d(pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F));
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (!playerIn.abilities.isCreativeMode) playerIn.getCooldownTracker().setCooldown(ItemRegistry.MAGIC_MIRROR.get(),
                    RelicsConfig.MagicMirror.USAGE_COOLDOWN.get() * 20);
        } else playerIn.sendStatusMessage(new TranslationTextComponent("tooltip.relics.magic_mirror.invalid_location"), true);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}