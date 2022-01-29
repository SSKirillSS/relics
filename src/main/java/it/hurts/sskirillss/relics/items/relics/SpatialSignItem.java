package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicDurabilityData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketItemActivation;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#dc41ff", "#832698")
                .ability(AbilityTooltip.builder()
                        .arg(stats.timeBeforeActivation)
                        .arg(stats.experiencePerSecond)
                        .arg(Minecraft.getInstance().options.keyShift.getKey().getDisplayName().getString())
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .ability(AbilityTooltip.builder()
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .durability(new RelicDurabilityData(1))
                .build();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (!NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
            Vec3 pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));

            tooltip.add(new TranslatableComponent("tooltip.relics.spatial_sign.tooltip_1", pos.x(), pos.y(), pos.z()));
            tooltip.add(new TranslatableComponent("tooltip.relics.spatial_sign.tooltip_2", NBTUtils.getInt(stack, TAG_TIME, 0)));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if (DurabilityUtils.isBroken(stack))
            return InteractionResultHolder.fail(stack);

        if (NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
            if (playerIn.totalExperience > 0) {
                NBTUtils.setString(stack, TAG_POSITION, NBTUtils.writePosition(playerIn.position()));
                NBTUtils.setString(stack, TAG_WORLD, playerIn.getCommandSenderWorld().dimension().location().toString());
                NBTUtils.setInt(stack, TAG_TIME, stats.timeBeforeActivation);

                worldIn.playSound(playerIn, playerIn.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        } else if (playerIn.isShiftKeyDown())
            teleportPlayer(playerIn, stack);

        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isClientSide() || DurabilityUtils.isBroken(stack) || !(entityIn instanceof Player))
            return;

        Player player = (Player) entityIn;

        int time = NBTUtils.getInt(stack, TAG_TIME, -1);

        if (player.tickCount % 20 == 0 && time >= 0) {
            if (time > 0) {
                NBTUtils.setInt(stack, TAG_TIME, time - 1);

                if (player.totalExperience > 0)
                    player.giveExperiencePoints(-stats.experiencePerSecond);
                else
                    teleportPlayer(player, stack);
            } else
                teleportPlayer(player, stack);
        }

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return NBTUtils.getInt(stack, TAG_TIME, 0) > 0;
    }

    public static void teleportPlayer(Player player, ItemStack stack) {
        Stats stats = INSTANCE.stats;

        if (player.getCommandSenderWorld().isClientSide())
            return;

        Vec3 pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));
        ServerLevel world = NBTUtils.parseLevel(player.getCommandSenderWorld(), NBTUtils.getString(stack, TAG_WORLD, ""));
        ServerPlayer serverPlayer = (ServerPlayer) player;

        if (pos == null || world == null)
            return;

        serverPlayer.teleportTo(world, pos.x(), pos.y(), pos.z(), player.getYRot(), player.getXRot());
        world.playSound(null, player.blockPosition(), SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.PLAYERS, 1.0F, 1.0F);
        NetworkHandler.sendToClient(new PacketItemActivation(stack), serverPlayer);

        if (!player.getAbilities().instabuild)
            player.getCooldowns().addCooldown(stack.getItem(),
                    (stats.timeBeforeActivation - NBTUtils.getInt(stack, TAG_TIME, 0)) * 20);

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
            if (!(event.getEntity() instanceof Player player))
                return;

            if (EntityUtils.getSlotWithItem(player, ItemRegistry.SPATIAL_SIGN.get()) == -1)
                return;

            ItemStack stack = player.getInventory().getItem(EntityUtils.getSlotWithItem(player, ItemRegistry.SPATIAL_SIGN.get()));

            if (!DurabilityUtils.isBroken(stack) && !NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
                teleportPlayer(player, stack);
                player.setHealth(1.0F);

                stack.setDamageValue(stack.getMaxDamage());

                event.setCanceled(true);
            }
        }
    }

    public static class Stats extends RelicStats {
        public int timeBeforeActivation = 30;
        public int experiencePerSecond = 1;
    }
}