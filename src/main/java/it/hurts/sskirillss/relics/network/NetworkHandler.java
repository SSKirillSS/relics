package it.hurts.sskirillss.relics.network;

import it.hurts.sskirillss.relics.network.packets.PacketItemActivation;
import it.hurts.sskirillss.relics.network.packets.PacketPlayerMotion;
import it.hurts.sskirillss.relics.network.packets.PacketSyncEntityEffects;
import it.hurts.sskirillss.relics.network.packets.abilities.SpellCastPacket;
import it.hurts.sskirillss.relics.network.packets.capability.CapabilitySyncPacket;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketExperienceExchange;
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

    public static void register() {
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
        INSTANCE.messageBuilder(CapabilitySyncPacket.class, nextID())
                .encoder(CapabilitySyncPacket::toBytes)
                .decoder(CapabilitySyncPacket::new)
                .consumer(CapabilitySyncPacket::handle)
                .add();
        INSTANCE.messageBuilder(SpellCastPacket.class, nextID())
                .encoder(SpellCastPacket::toBytes)
                .decoder(SpellCastPacket::new)
                .consumer(SpellCastPacket::handle)
                .add();
        INSTANCE.messageBuilder(PacketExperienceExchange.class, nextID())
                .encoder(PacketExperienceExchange::toBytes)
                .decoder(PacketExperienceExchange::new)
                .consumer(PacketExperienceExchange::handle)
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