package com.example.covertmod.networking.packets;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.example.covertmod.networking.ModMessages;

public class CovertDataS2CPacket {
    // The data to be transmitted
    private final String covertData;
    // Logger instance for logging events
    private static final Logger LOGGER = LogUtils.getLogger();
    // Filename for the receiving FIFO
    private static final String FILENAME = "/Users/sociallyencrypted/try2.txt";
    private BufferedWriter writer;

    /**
     * Constructs a new CovertDataS2CPacket with the specified covert data.
     *
     * @param covertData the data to be transmitted
     */
    public CovertDataS2CPacket(String covertData) {
        this.covertData = covertData;
    }

    /**
     * Constructs a new CovertDataS2CPacket by reading data from the provided buffer.
     *
     * @param buf the buffer containing the packet data
     */
    public CovertDataS2CPacket(FriendlyByteBuf buf) {
        this.covertData = buf.readUtf();
    }

    /**
     * Writes the packet data to the provided buffer.
     *
     * @param buf the buffer to write the packet data to
     */
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.covertData);
    }

    /**
     * Handles the packet when it is received on the client.
     *
     * @param context the context of the custom payload event
     */
    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            LOGGER.info("Received covert data of length {}", covertData.length());
            LOGGER.info("Data: {}", covertData);
            writeFileData(covertData);
        });
        context.setPacketHandled(true);
    }

    /**
     * Writes the provided data to a file on the server.
     *
     * @param data the data to write to the file
     */
    private void writeFileData(String data) {
        try {
            Process process = ModMessages.getFifoWriterProcess();
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("Error writing data to FIFO: {}", e.getMessage());
        }
    }
}
