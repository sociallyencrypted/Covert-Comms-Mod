package com.example.covertmod.utils;

import com.example.covertmod.networking.ModMessages;
import com.example.covertmod.networking.packets.CovertDataC2SPacket;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * Thread to read data from FIFO (named pipe) using cat command and send it to the server.
 */
public class FifoReaderThread extends Thread {
    // The size of each chunk to read from the FIFO
    private static final int CHUNK_SIZE = 32;
    // The number of bytes per second to read from the FIFO
    private static final int BYTES_PER_SECOND = 800;
    // The delay between reading each chunk from the FIFO
    private static final int DELAY_PER_CHUNK = CHUNK_SIZE / BYTES_PER_SECOND * 1000;
    // Logger instance for logging events
    private static final Logger LOGGER = LogUtils.getLogger();
    // Flag to control the running state of the thread
    private volatile boolean running = true;

    /**
     * Constructs a new FifoReaderThread with the specified FIFO path.
     */
    public FifoReaderThread() {
    }

    /**
     * The main logic of the thread. Reads data from the FIFO and sends it to the server.
     */
    @Override
    public void run() {
        // The FIFO reader process
        Process fifoReaderProcess;
        try {
            fifoReaderProcess = ModMessages.getFifoReaderProcess();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Started FIFO reader C process");
        InputStream inputStream = fifoReaderProcess.getInputStream();
        // Read data from the FIFO in chunks
        while (running) {
            try {
                byte[] chunk = new byte[CHUNK_SIZE];
                int bytesRead = inputStream.read(chunk);
                if (bytesRead < chunk.length) {
                    byte[] trimmedChunk = new byte[bytesRead];
                    System.arraycopy(chunk, 0, trimmedChunk, 0, bytesRead);
                    chunk = trimmedChunk;
                }
                LOGGER.info("Read {} bytes from FIFO", bytesRead);
                if (bytesRead > 0) {
                    LOGGER.info("Read data from FIFO: {}", chunk);
                    // Send the chunk to the server using a CovertDataC2SPacket
                    CovertDataC2SPacket packet = new CovertDataC2SPacket(chunk);
                    ModMessages.sendToServer(packet);
                }
//                Thread.sleep(DELAY_PER_CHUNK);
            } catch (IOException e) {
                LOGGER.error("Error reading from FIFO: {}", e.getMessage());
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