package it.hurts.sskirillss.relics.items.relics.base.handlers;

import it.hurts.sskirillss.relics.items.RelicContractItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class ContractHandler {
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        ItemEntity drop = event.getItem();
        ItemStack stack = drop.getItem();
        PlayerEntity player = event.getPlayer();
        ServerWorld world = (ServerWorld) player.getCommandSenderWorld();
        long time = NBTUtils.getLong(stack, RelicContractItem.TAG_DATE, -1);

        if (world.getGameTime() - time >= (3600 * 20) || time <= -1)
            return;

        String uuid = RelicUtils.Owner.getOwnerUUID(stack);

        if (player.isCreative() || uuid.equals("") || uuid.equals(player.getStringUUID()))
            return;

        drop.setPickUpDelay(20);

        Vector3d motion = player.position().subtract(drop.position()).normalize();

        NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x(), motion.y(), motion.z()), (ServerPlayerEntity) player);

        world.sendParticles(ParticleTypes.EXPLOSION, drop.getX(), drop.getY() + 0.5F, drop.getZ(), 1, 0, 0, 0, 0);
        world.playSound(null, drop.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.PLAYERS, 1F, 1F);

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onTooltipRender(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        long time = NBTUtils.getLong(stack, RelicContractItem.TAG_DATE, -1);

        if (event.getPlayer() == null || stack.isEmpty() || time <= -1)
            return;

        World world = event.getPlayer().getCommandSenderWorld();
        PlayerEntity owner = RelicUtils.Owner.getOwner(stack, world);
        time = (time + (3600 * 20) - world.getGameTime()) / 20;

        if (time > 0 && owner != null) {
            long hours = time / 3600;
            long minutes = (time % 3600) / 60;
            long seconds = (time % 3600) % 60;

            event.getToolTip().add(new TranslationTextComponent("tooltip.relics.contract", owner.getDisplayName(), hours, minutes, seconds));
        }
    }

    public static void handleOwner(PlayerEntity player, ItemStack stack) {
        World world = player.getCommandSenderWorld();
        PlayerEntity owner = RelicUtils.Owner.getOwner(stack, world);
        long time = NBTUtils.getLong(stack, RelicContractItem.TAG_DATE, -1);

        if (owner == null || time <= -1)
            return;

        if (time == 0)
            NBTUtils.setInt(stack, RelicContractItem.TAG_DATE, (int) world.getGameTime());
        else if (world.getGameTime() - time >= (3600 * 20)) {
            NBTUtils.setInt(stack, RelicContractItem.TAG_DATE, -1);

            RelicUtils.Owner.setOwnerUUID(stack, "");

            return;
        }

        if (!owner.getStringUUID().equals(player.getStringUUID()) && !player.isCreative() && !player.isSpectator()) {
            player.drop(stack.copy(), false, true);
            stack.shrink(1);

            player.setDeltaMovement(player.getViewVector(0F).multiply(-1F, -1F, -1F).normalize());

            world.addParticle(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 1, player.getZ(), 0, 0, 0);
            world.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.PLAYERS, 1F, 1F);
        }
    }
}