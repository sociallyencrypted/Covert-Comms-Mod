package com.example.covertmod.networking.packets;

import com.example.covertmod.networking.ModMessages;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.slf4j.Logger;

/**
 * This class represents a packet that contains covert data sent from the client to the server.
 */
public class CovertDataC2SPacket {
    // The data read from the file
    private final byte[] fileData;
    // Logger instance for logging events
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Constructs a new CovertDataC2SPacket with the specified file data.
     *
     * @param fileData the data read from the file
     */
    public CovertDataC2SPacket(byte[] fileData) {
        this.fileData = fileData;
    }

    /**
     * Constructs a new CovertDataC2SPacket by reading data from the provided buffer.
     *
     * @param buf the buffer containing the packet data
     */
    public CovertDataC2SPacket(FriendlyByteBuf buf) {
        this.fileData = buf.readByteArray();
    }

    /**
     * Writes the packet data to the provided buffer.
     *
     * @param buf the buffer to write the packet data to
     */
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeByteArray(this.fileData);
    }

    /**
     * Handles the packet when it is received on the server.
     *
     * @param context the context of the custom payload event
     */
    public void handle(CustomPayloadEvent.Context context) {
        LOGGER.info("Data read from fifo: {}", this.fileData);
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                // Send a system message to the player with the number of bytes read
                player.sendSystemMessage(Component.translatable("Bytes read: " + this.fileData.length));
                // Craft a CovertDataS2CPacket and send it to all clients
                CovertDataS2CPacket packet = new CovertDataS2CPacket(this.fileData);
                ModMessages.sendToPlayers(packet);
            }
        });
    }
}