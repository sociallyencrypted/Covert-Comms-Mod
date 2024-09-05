package com.example.netmod.utils;

import com.example.netmod.networking.ModMessages;
import com.example.netmod.networking.packet.HelloWorldC2SPacket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FifoReaderThread extends Thread {
    private static final int CHUNK_SIZE = 32;
    private static final int BYTES_PER_SECOND = 800;
    private static final long DELAY_PER_CHUNK_MS = (CHUNK_SIZE * 1000L) / BYTES_PER_SECOND;

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
                String chunk = new String(buffer, 0, bytesRead);
                ModMessages.sendToServer(new HelloWorldC2SPacket(chunk));
                Thread.sleep(DELAY_PER_CHUNK_MS);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error reading from FIFO: " + e.getMessage());
        }
    }

    public void stopReading() {
        running = false;
    }
}