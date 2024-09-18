package com.example.covertmod.networking;

import com.example.covertmod.CovertMod;
import com.example.covertmod.networking.packets.CovertDataC2SPacket;
import com.example.covertmod.networking.packets.CovertDataS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;

import java.io.IOException;

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
    private static Process fifoWriterProcess;
    private static final String BINARY_PATH = "/Users/sociallyencrypted/Downloads/forge-1.21.1-52.0.9-mdk/build/bin/write_to_fifo";
    private static final String FILENAME = "/Users/sociallyencrypted/try2.txt";

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

        // Register the CovertDataS2CPacket message with the channel
        instance.messageBuilder(CovertDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CovertDataS2CPacket::new)
                .encoder(CovertDataS2CPacket::toBytes)
                .consumerMainThread(CovertDataS2CPacket::handle)
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

    /**
     * Sends a message to all players.
     *
     * @param <MSG> the type of the message
     * @param message the message to send
     */
    public static <MSG> void sendToPlayers(MSG message) {
        instance.send(message, PacketDistributor.ALL.noArg());
    }

    public static Process getFifoWriterProcess() throws IOException {
        if (fifoWriterProcess == null) {
            fifoWriterProcess = new ProcessBuilder(BINARY_PATH, FILENAME).start();
        }
        return fifoWriterProcess;
    }
}