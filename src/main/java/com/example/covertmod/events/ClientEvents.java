package com.example.covertmod.events;

import com.example.covertmod.utils.FifoReaderThread;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

import static com.example.covertmod.CovertMod.MODID;

/**
 * This class handles client-side events for the mod.
 * It listens for client chat messages and manages a FIFO reader thread.
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    // Path to the FIFO file
    private static final String FIFO_FILE_PATH = "/Users/sociallyencrypted/try1.txt";
    // Command to start the FIFO reader thread
    private static final String START_COMMAND = "hello";
    // Command to stop the FIFO reader thread
    private static final String STOP_COMMAND = "stop";
    // Logger instance for logging events
    private static final Logger LOGGER = LogUtils.getLogger();
    // Instance of the FIFO reader thread
    private static FifoReaderThread fifoReaderThread;

    /**
     * Handles the client setup event.
     * This method is called during the client setup phase of mod loading.
     *
     * @param event the client setup event
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("HELLO FROM CLIENT SETUP");
    }

    /**
     * Handles client chat messages.
     * This method is called when a chat message is received.
     *
     * @param event the client chat event
     */
    @SubscribeEvent
    public void onClientChatMessage(ClientChatEvent event) {
        LOGGER.info("Received chat message: {}", event.getMessage());
        String chatMessage = event.getMessage();
        if (START_COMMAND.equals(chatMessage)) {
            startFifoReaderThread();
            event.setCanceled(true);
        } else if (STOP_COMMAND.equals(chatMessage)) {
            stopFifoReaderThread();
            event.setCanceled(true);
        }
    }

    /**
     * Starts the FIFO reader thread.
     * If the thread is already running, logs a warning message.
     */
    private static void startFifoReaderThread() {
        if (fifoReaderThread != null) {
            LOGGER.warn("FIFO reader thread already running");
            return;
        }
        LOGGER.info("Starting FIFO reader thread");
        fifoReaderThread = new FifoReaderThread(FIFO_FILE_PATH);
        fifoReaderThread.start();
    }

    /**
     * Stops the FIFO reader thread.
     * If the thread is not running, does nothing.
     */
    private static void stopFifoReaderThread() {
        LOGGER.info("Stopping FIFO reader thread");
        if (fifoReaderThread != null) {
            fifoReaderThread.stopReading();
            fifoReaderThread = null;
        }
    }
}