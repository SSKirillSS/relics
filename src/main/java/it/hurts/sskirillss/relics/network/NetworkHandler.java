package it.hurts.sskirillss.relics.network;

import it.hurts.sskirillss.relics.network.packets.*;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketRelicTweak;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static SimpleChannel INSTANCE;
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Reference.MODID, "network"),
                () -> "1.0",
                s -> true,
                s -> true);

        INSTANCE.messageBuilder(PacketPlayerMotion.class, nextID())
                .encoder(PacketPlayerMotion::toBytes)
                .decoder(PacketPlayerMotion::new)
                .consumer(PacketPlayerMotion::handle)
                .add();
        INSTANCE.messageBuilder(PacketItemActivation.class, nextID())
                .encoder(PacketItemActivation::toBytes)
                .decoder(PacketItemActivation::new)
                .consumer(PacketItemActivation::handle)
                .add();
        INSTANCE.messageBuilder(PacketRelicTweak.class, nextID())
                .encoder(PacketRelicTweak::toBytes)
                .decoder(PacketRelicTweak::new)
                .consumer(PacketRelicTweak::handle)
                .add();
        INSTANCE.messageBuilder(PacketSyncEntityEffects.class, nextID())
                .encoder(PacketSyncEntityEffects::toBytes)
                .decoder(PacketSyncEntityEffects::new)
                .consumer(PacketSyncEntityEffects::handle)
                .add();
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

    public static void sendToClients(PacketDistributor.PacketTarget target, Object packet) {
        INSTANCE.send(target, packet);
    }
}