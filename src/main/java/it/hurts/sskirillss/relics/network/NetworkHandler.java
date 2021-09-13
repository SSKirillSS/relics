package it.hurts.sskirillss.relics.network;

import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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

    public static void sendToClient(Object packet, ServerPlayerEntity player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}