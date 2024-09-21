package com.example.covertmod.utils;

import com.example.covertmod.networking.ModMessages;
import com.example.covertmod.networking.packets.CovertDataC2SPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.client.player.Input;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents a thread that reads data from a FIFO (named pipe) and sends it to the server.
 */
public class FifoReaderThread extends Thread {
    // The size of each chunk to read from the FIFO
    private static final int CHUNK_SIZE = 32;
    private static final int BYTES_PER_SECOND = 800;
    private static final int DELAY_PER_CHUNK = CHUNK_SIZE / BYTES_PER_SECOND * 1000;
    // Logger instance for logging events
    private static final Logger LOGGER = LogUtils.getLogger();
    // The FIFO reader process
    private static Process fifoReaderProcess;
    // The FIFO path
    private String fifoPath;
    // Flag to control the running state of the thread
    private volatile boolean running = true;

    /**
     * Constructs a new FifoReaderThread with the specified FIFO path.
     *
     * @param fifoPath the path to the FIFO file
     */
    public FifoReaderThread(String fifoPath) {
        this.fifoPath = fifoPath;
    }

    /**
     * The main logic of the thread. Reads data from the FIFO and sends it to the server.
     */
    @Override
    public void run() {
        try {
            fifoReaderProcess = ModMessages.getFifoReaderProcess();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Started FIFO reader C process");
        InputStream inputStream = fifoReaderProcess.getInputStream();
        // read 32 bit chunk from the fifo and send it as a packet
        while (running) {
            try {
                byte[] chunk = new byte[CHUNK_SIZE];
                int bytesRead = inputStream.read(chunk);
//                LOGGER.info("Read {} bytes from FIFO", bytesRead);
                if (bytesRead > 0) {
                    LOGGER.info("Read data from FIFO: {}", chunk);
                    CovertDataC2SPacket packet = new CovertDataC2SPacket(chunk);
                    ModMessages.sendToServer(packet);
                }
                Thread.sleep(DELAY_PER_CHUNK);
            } catch (IOException e) {
                LOGGER.error("Error reading from FIFO: {}", e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Stops the reading process by setting the running flag to false.
     */
    public void stopReading() {
        running = false;
    }
}