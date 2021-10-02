package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketItemActivation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

public class SpatialSignItem extends RelicItem<SpatialSignItem.Stats> {
    public static final String TAG_POSITION = "pos";
    public static final String TAG_TIME = "time";
    public static final String TAG_WORLD = "world";

    public static SpatialSignItem INSTANCE;

    public SpatialSignItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.CAVE)
                        .chance(0.15F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg(config.timeBeforeActivation)
                        .varArg(Minecraft.getInstance().options.keyShift.getKey().getDisplayName().getString())
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .build())
                .build();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (!NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
            Vector3d pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));

            tooltip.add(new TranslationTextComponent("tooltip.relics.spatial_sign.tooltip_1", pos.x(), pos.y(), pos.z()));
            tooltip.add(new TranslationTextComponent("tooltip.relics.spatial_sign.tooltip_2", NBTUtils.getInt(stack, TAG_TIME, 0)));
        }
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if (NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
            NBTUtils.setString(stack, TAG_POSITION, NBTUtils.writePosition(playerIn.position()));
            NBTUtils.setString(stack, TAG_WORLD, playerIn.getCommandSenderWorld().dimension().location().toString());
            NBTUtils.setInt(stack, TAG_TIME, config.timeBeforeActivation);

            worldIn.playSound(playerIn, playerIn.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
        } else if (playerIn.isShiftKeyDown())
            teleportPlayer(playerIn, stack);

        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isClientSide() || !(entityIn instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) entityIn;

        int time = NBTUtils.getInt(stack, TAG_TIME, -1);

        if (player.tickCount % 20 == 0 && time >= 0) {
            if (time > 0)
                NBTUtils.setInt(stack, TAG_TIME, time - 1);
            else
                teleportPlayer(player, stack);
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return NBTUtils.getInt(stack, TAG_TIME, 0) > 0;
    }

    public static void teleportPlayer(PlayerEntity player, ItemStack stack) {
        Stats config = INSTANCE.config;

        if (player.getCommandSenderWorld().isClientSide())
            return;

        Vector3d pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));
        ServerWorld world = NBTUtils.parseWorld(player.getCommandSenderWorld(), NBTUtils.getString(stack, TAG_WORLD, ""));
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        if (pos == null || world == null)
            return;

        serverPlayer.teleportTo(world, pos.x(), pos.y(), pos.z(), player.yRot, player.xRot);
        world.playSound(null, player.blockPosition(), SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        NetworkHandler.sendToClient(new PacketItemActivation(stack), serverPlayer);

        if (!player.abilities.instabuild)
            player.getCooldowns().addCooldown(stack.getItem(),
                    (config.timeBeforeActivation - NBTUtils.getInt(stack, TAG_TIME, 0)) * 20);

        NBTUtils.setString(stack, TAG_WORLD, "");
        NBTUtils.setString(stack, TAG_POSITION, "");
        NBTUtils.setInt(stack, TAG_TIME, -1);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SpatialSignServerEvents {
        @SubscribeEvent
        public static void onEntityDeath(LivingDeathEvent event) {
            if (!(event.getEntity() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getEntity();

            if (EntityUtils.getSlotWithItem(player, ItemRegistry.SPATIAL_SIGN.get()) == -1)
                return;

            ItemStack stack = player.inventory.getItem(EntityUtils.getSlotWithItem(player, ItemRegistry.SPATIAL_SIGN.get()));

            if (!NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
                teleportPlayer(player, stack);
                player.setHealth(1.0F);

                stack.shrink(1);

                event.setCanceled(true);
            }
        }
    }

    public static class Stats extends RelicStats {
        public int timeBeforeActivation = 30;
    }
}