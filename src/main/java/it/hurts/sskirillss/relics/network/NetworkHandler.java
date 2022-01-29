package it.hurts.sskirillss.relics.network;

import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
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

        INSTANCE.messageBuilder(PacketMouseScroll.class, nextID())
                .encoder(PacketMouseScroll::toBytes)
                .decoder(PacketMouseScroll::new)
                .consumer(PacketMouseScroll::handle)
                .add();
        INSTANCE.messageBuilder(PacketPlayerMotion.class, nextID())
                .encoder(PacketPlayerMotion::toBytes)
                .decoder(PacketPlayerMotion::new)
                .consumer(PacketPlayerMotion::handle)
                .add();
        INSTANCE.messageBuilder(PacketRelicAbility.class, nextID())
                .encoder(PacketRelicAbility::toBytes)
                .decoder(PacketRelicAbility::new)
                .consumer(PacketRelicAbility::handle)
                .add();
        INSTANCE.messageBuilder(PacketItemActivation.class, nextID())
                .encoder(PacketItemActivation::toBytes)
                .decoder(PacketItemActivation::new)
                .consumer(PacketItemActivation::handle)
                .add();
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}