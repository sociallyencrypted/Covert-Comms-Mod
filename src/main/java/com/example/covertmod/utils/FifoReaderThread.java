package com.example.covertmod.utils;

import com.example.covertmod.networking.ModMessages;
import com.example.covertmod.networking.packet.CovertDataC2SPacket;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FifoReaderThread extends Thread {
    private static final int CHUNK_SIZE = 32;
    private static final int BYTES_PER_SECOND = 800;
    private static final long DELAY_PER_CHUNK_MS = (CHUNK_SIZE * 1000L) / BYTES_PER_SECOND;
    private static final Logger LOGGER = LogUtils.getLogger();

    private final String fifoPath;
    private volatile boolean running = true;

    public FifoReaderThread(String fifoPath) {
        this.fifoPath = fifoPath;
    }

    @Override
    public void run() {
        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(fifoPath)))) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            while (running && (bytesRead = inputStream.read(buffer)) != -1) {
                LOGGER.info("Read {} bytes from FIFO", bytesRead);
                String chunk = new String(buffer, 0, bytesRead);
                ModMessages.sendToServer(new CovertDataC2SPacket(chunk));
                //noinspection BusyWait
                Thread.sleep(DELAY_PER_CHUNK_MS);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error reading FIFO: {}", e.getMessage());
        }
    }

    public void stopReading() {
        running = false;
    }
}