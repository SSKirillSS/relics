package it.hurts.sskirillss.relics.items.relics.base.handlers;

import it.hurts.sskirillss.relics.items.RelicContractItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.PacketPlayerMotion;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class ContractHandler {
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        ItemEntity drop = event.getItem();
        ItemStack stack = drop.getItem();
        Player player = event.getPlayer();
        ServerLevel world = (ServerLevel) player.getCommandSenderWorld();
        long time = NBTUtils.getLong(stack, RelicContractItem.TAG_DATE, -1);

        if (world.getGameTime() - time >= (3600 * 20) || time <= -1)
            return;

        String uuid = RelicUtils.Owner.getOwnerUUID(stack);

        if (player.isCreative() || uuid.equals("") || uuid.equals(player.getStringUUID()))
            return;

        drop.setPickUpDelay(20);

        Vec3 motion = player.position().subtract(drop.position()).normalize();

        NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x(), motion.y(), motion.z()), (ServerPlayer) player);

        world.sendParticles(ParticleTypes.EXPLOSION, drop.getX(), drop.getY() + 0.5F, drop.getZ(), 1, 0, 0, 0, 0);
        world.playSound(null, drop.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1F, 1F);

        event.setCanceled(true);
    }

    public static void handleOwner(Player player, ItemStack stack) {
        Level world = player.getCommandSenderWorld();
        Player owner = RelicUtils.Owner.getOwner(stack, world);
        long time = NBTUtils.getLong(stack, RelicContractItem.TAG_DATE, -1);

        if (owner == null || time <= -1)
            return;

        if (time == 0)
            NBTUtils.setLong(stack, RelicContractItem.TAG_DATE, world.getGameTime());
        else if (world.getGameTime() - time >= (3600 * 20)) {
            NBTUtils.setLong(stack, RelicContractItem.TAG_DATE, -1);

            RelicUtils.Owner.setOwnerUUID(stack, "");

            return;
        }

        if (!owner.getStringUUID().equals(player.getStringUUID()) && !player.isCreative() && !player.isSpectator()) {
            player.drop(stack.copy(), false, true);
            stack.shrink(1);

            player.setDeltaMovement(player.getViewVector(0F).multiply(-1F, -1F, -1F).normalize());

            world.addParticle(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 1, player.getZ(), 0, 0, 0);
            world.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1F, 1F);
        }
    }
}