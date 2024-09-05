package com.example.covertmod.networking;

import com.example.covertmod.CovertMod;
import com.example.covertmod.networking.packets.CovertDataC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;

/**
 * This class handles the registration and sending of network messages for the mod.
 */
public class ModMessages {
    // The name of the channel used for communication
    private static final String CHANNEL_NAME = "main";
    // The protocol version for the network communication
    private static final int PROTOCOL_VERSION = 1;
    // The SimpleChannel instance used for sending and receiving messages
    private static SimpleChannel instance;
    // The packet ID counter
    private static int packetId = 0;

    /**
     * Generates a unique packet ID.
     *
     * @return the next packet ID
     */
    private static int id() {
        return packetId++;
    }

    /**
     * Registers the network channel and messages.
     */
    public static void register(){
        // Create a new SimpleChannel with the specified name and protocol version
        instance = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(CovertMod.MODID, CHANNEL_NAME))
                .networkProtocolVersion(PROTOCOL_VERSION)
                .clientAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
                .serverAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
                .simpleChannel();

        // Register the CovertDataC2SPacket message with the channel
        instance.messageBuilder(CovertDataC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CovertDataC2SPacket::new)
                .encoder(CovertDataC2SPacket::toBytes)
                .consumerMainThread(CovertDataC2SPacket::handle)
                .add();
    }

    /**
     * Sends a message to the server.
     *
     * @param <MSG> the type of the message
     * @param message the message to send
     */
    public static <MSG> void sendToServer(MSG message){
        instance.send(message, PacketDistributor.SERVER.noArg());
    }
}