package com.example.covertmod.utils;

import com.example.covertmod.networking.ModMessages;
import com.example.covertmod.networking.packets.CovertDataC2SPacket;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class represents a thread that reads data from a FIFO (named pipe) and sends it to the server.
 */
public class FifoReaderThread extends Thread {
    // The size of each chunk to read from the FIFO
    private static final int CHUNK_SIZE = 32;
    // The number of bytes to read per second
    private static final int BYTES_PER_SECOND = 800;
    // The delay in milliseconds between reading each chunk
    private static final long DELAY_PER_CHUNK_MS = (CHUNK_SIZE * 1000L) / BYTES_PER_SECOND;
    // Logger instance for logging events
    private static final Logger LOGGER = LogUtils.getLogger();

    // The path to the FIFO file
    private final String fifoPath;
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
        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(fifoPath)))) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            while (running && (bytesRead = inputStream.read(buffer)) != -1) {
                LOGGER.info("Read {} bytes from FIFO", bytesRead);
                String chunk = new String(buffer, 0, bytesRead);
                // Send the read chunk to the server
                ModMessages.sendToServer(new CovertDataC2SPacket(chunk));
                // Delay to control the read rate
                //noinspection BusyWait
                Thread.sleep(DELAY_PER_CHUNK_MS);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error reading FIFO: {}", e.getMessage());
        }
    }

    /**
     * Stops the reading process by setting the running flag to false.
     */
    public void stopReading() {
        running = false;
    }
}