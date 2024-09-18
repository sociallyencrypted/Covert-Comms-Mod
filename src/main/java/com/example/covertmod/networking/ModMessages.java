package com.example.covertmod.networking;

import com.example.covertmod.CovertMod;
import com.example.covertmod.networking.packets.CovertDataC2SPacket;
import com.example.covertmod.networking.packets.CovertDataS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ModMessages {
    private static String CHANNEL_NAME;
    private static int PROTOCOL_VERSION;
    private static String BINARY_PATH;
    private static String FILENAME;
    private static SimpleChannel instance;
    private static int packetId = 0;
    private static Process fifoWriterProcess;

    static {
        try (InputStream input = ModMessages.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            CHANNEL_NAME = prop.getProperty("CHANNEL_NAME");
            PROTOCOL_VERSION = Integer.parseInt(prop.getProperty("PROTOCOL_VERSION"));
            BINARY_PATH = prop.getProperty("BINARY_PATH");
            FILENAME = prop.getProperty("RECEIVING_FIFO_PATH");
        } catch (IOException ex) {
            System.err.println("Error reading config.properties file: " + ex.getMessage());
        }
    }

    private static int id() {
        return packetId++;
    }

    public static void register(){
        instance = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(CovertMod.MODID, CHANNEL_NAME))
                .networkProtocolVersion(PROTOCOL_VERSION)
                .clientAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
                .serverAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
                .simpleChannel();

        instance.messageBuilder(CovertDataC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CovertDataC2SPacket::new)
                .encoder(CovertDataC2SPacket::toBytes)
                .consumerMainThread(CovertDataC2SPacket::handle)
                .add();

        instance.messageBuilder(CovertDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CovertDataS2CPacket::new)
                .encoder(CovertDataS2CPacket::toBytes)
                .consumerMainThread(CovertDataS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message){
        instance.send(message, PacketDistributor.SERVER.noArg());
    }

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