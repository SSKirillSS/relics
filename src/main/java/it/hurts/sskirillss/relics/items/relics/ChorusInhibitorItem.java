package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class ChorusInhibitorItem extends Item implements ICurioItem, IHasTooltip {
    public static final String TAG_POSITION = "pos";
    public static final String TAG_TIME = "time";
    public static final String TAG_WORLD = "world";

    public ChorusInhibitorItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public java.util.List<ITextComponent> getShiftTooltip() {
        java.util.List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.chorus_inhibitor.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.chorus_inhibitor.shift_2"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.chorus_inhibitor.shift_3"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (!NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
            Vector3d pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));
            tooltip.add(new TranslationTextComponent("tooltip.relics.chorus_inhibitor.tooltip_1", pos.getX(), pos.getY(), pos.getZ()));
            tooltip.add(new TranslationTextComponent("tooltip.relics.chorus_inhibitor.tooltip_2", NBTUtils.getInt(stack, TAG_TIME, 0)));
        }
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity) || livingEntity.getEntityWorld().isRemote() || livingEntity.ticksExisted % 20 != 0) return;
        PlayerEntity player = (PlayerEntity) livingEntity;
        int time = NBTUtils.getInt(stack, TAG_TIME, 0);
        if (time > 0) NBTUtils.setInt(stack, TAG_TIME, time - 1);
        else if (!NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
            Vector3d pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));
            if (pos == null) return;
            ServerWorld world = NBTUtils.parseWorld(player.getEntityWorld(), NBTUtils.getString(stack, TAG_WORLD, ""));
            if (world != null) EntityUtils.teleportWithMount(player, world, pos);
            player.getEntityWorld().playSound(player, pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            NBTUtils.setString(stack, TAG_POSITION, "");
            NBTUtils.setString(stack, TAG_WORLD, "");
            NBTUtils.setInt(stack, TAG_TIME, 0);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ChorusInhibitorEvents {
        @SubscribeEvent
        public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
            if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if (player.getHeldItemMainhand().getItem() == Items.CHORUS_FRUIT && !player.isSneaking()) {
                CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CHORUS_INHIBITOR.get(), player).ifPresent(triple -> {
                    ItemStack stack = triple.getRight();
                    int time = NBTUtils.getInt(stack, TAG_TIME, 0);
                    NBTUtils.setInt(stack, TAG_TIME, time + RelicsConfig.ChorusInhibitor.TIME_PER_CHORUS.get());
                    if (time <= 0) {
                        NBTUtils.setString(stack, TAG_POSITION, NBTUtils.writePosition(player.getPositionVec()));
                        NBTUtils.setString(stack, TAG_WORLD, player.getEntityWorld().getDimensionKey().getLocation().toString());
                    }
                    player.getHeldItemMainhand().shrink(1);
                    event.setCanceled(true);
                });
            }
        }

    }

    @SubscribeEvent
    public static void onPlayerTeleported(EnderTeleportEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity
                && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CHORUS_INHIBITOR.get(), event.getEntityLiving()).isPresent()) {
            event.setAttackDamage(0.0F);
        }
    }
}