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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class ChorusInhibitorItem extends Item implements ICurioItem, IHasTooltip {
    public static final String TAG_UPDATE_TIME = "time";
    public static final String TAG_WORLD = "world";
    public static final String TAG_STORED_POSITION = "position";

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
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof PlayerEntity) {
            if (livingEntity.ticksExisted % 20 == 0) {
                int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
                if (time > 0) {
                    NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
                } else {
                    BlockPos pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_STORED_POSITION, ""));
                    if (pos != null) {
                        teleportPlayer((PlayerEntity) livingEntity);
                    }
                }
            }
        }
    }

    @Override
    public void onUnequip(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
        NBTUtils.setString(stack, TAG_STORED_POSITION, "");
    }

    public static void teleportPlayer(PlayerEntity player) {
        ItemStack stack = player.inventory.getStackInSlot(EntityUtils.getSlotWithItem(player, ItemRegistry.SPATIAL_SIGN.get()));
        BlockPos pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_STORED_POSITION, ""));
        String worldString = NBTUtils.getString(stack, TAG_WORLD, "").equals("")
                ? player.getEntityWorld().getDimensionKey().getLocation().toString() : NBTUtils.getString(stack, TAG_WORLD, "");

        if (!player.getEntityWorld().isRemote() && player.getEntityWorld().getServer()
                .getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(worldString))) != null) {
            EntityUtils.teleportWithMount(player, player.getEntityWorld().getServer().getWorld(RegistryKey
                    .getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(worldString))), pos);
        }

        player.getEntityWorld().playSound(player, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        NBTUtils.setString(stack, TAG_STORED_POSITION, "");
        NBTUtils.setString(stack, TAG_WORLD, "");
        NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ChorusInhibitorEvents {
        @SubscribeEvent
        public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (event.getItem().getItem() == Items.CHORUS_FRUIT
                        && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CHORUS_INHIBITOR.get(), player).isPresent()) {
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CHORUS_INHIBITOR.get(), player).get().getRight();
                    int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
                    NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + RelicsConfig.ChorusInhibitor.TIME_PER_CHORUS.get());
                    if (time <= 0) {
                        NBTUtils.setString(stack, TAG_STORED_POSITION, NBTUtils.writePosition(player.getPosition()));
                        NBTUtils.setString(stack, TAG_WORLD, player.getEntityWorld().getDimensionKey().getLocation().toString());
                    }
                    player.getHeldItemMainhand().shrink(1);
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
}