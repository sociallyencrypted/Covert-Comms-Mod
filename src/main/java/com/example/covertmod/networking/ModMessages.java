package com.example.covertmod.networking;

import com.example.covertmod.CovertMod;
import com.example.covertmod.networking.packet.CovertDataC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;

public class ModMessages {
    private static final String CHANNEL_NAME = "main";
    private static final int PROTOCOL_VERSION = 1;
    private static SimpleChannel instance;
    private static int packetId = 0;

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
    }

    public static <MSG> void sendToServer(MSG message){
        instance.send(message, PacketDistributor.SERVER.noArg());
    }
}