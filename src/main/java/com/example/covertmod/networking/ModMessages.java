package com.example.covertmod.networking;

import com.example.covertmod.CovertMod;
import com.example.covertmod.networking.packets.CovertDataC2SPacket;
import com.example.covertmod.networking.packets.CovertDataS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ModMessages {
    private static String CHANNEL_NAME;
    private static int PROTOCOL_VERSION;
    private static String BINARY_WRITE_PATH;
    private static String BINARY_READ_PATH;
    private static String RECIPIENT_HOME_PATH;
    private static String SENDER_HOME_PATH;
    private static String RECEIVING_FIFO_NAME;
    private static String SENDING_FIFO_NAME;
    private static SimpleChannel instance;
    private static int packetId = 0;
    private static Process fifoWriterProcess;
    private static Process fifoReaderProcess;

    static {
        try (InputStream input = ModMessages.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            CHANNEL_NAME = prop.getProperty("CHANNEL_NAME");
            PROTOCOL_VERSION = Integer.parseInt(prop.getProperty("PROTOCOL_VERSION"));
            BINARY_WRITE_PATH = prop.getProperty("BINARY_WRITE_PATH");
            BINARY_READ_PATH = prop.getProperty("BINARY_READ_PATH");
            RECIPIENT_HOME_PATH = prop.getProperty("RECIPIENT_HOME_PATH");
            SENDER_HOME_PATH = prop.getProperty("SENDER_HOME_PATH");
            RECEIVING_FIFO_NAME = prop.getProperty("RECEIVING_FIFO_NAME");
            SENDING_FIFO_NAME = prop.getProperty("SENDING_FIFO_NAME");
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
            fifoWriterProcess = new ProcessBuilder(BINARY_WRITE_PATH, RECIPIENT_HOME_PATH + RECEIVING_FIFO_NAME).start();
        }
        return fifoWriterProcess;
    }

    public static Process getFifoReaderProcess() throws IOException {
        if (fifoReaderProcess == null) {
            fifoReaderProcess = new ProcessBuilder(BINARY_READ_PATH, SENDER_HOME_PATH + SENDING_FIFO_NAME).start();
        }
        return fifoReaderProcess;
    }
}