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

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    private static final String FIFO_FILE_PATH = "/Users/sociallyencrypted/try1.txt";
    private static final String START_COMMAND = "hello";
    private static final String STOP_COMMAND = "stop";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static FifoReaderThread fifoReaderThread;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("HELLO FROM CLIENT SETUP");
    }

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

    private static void startFifoReaderThread() {
        if (fifoReaderThread != null) {
            LOGGER.warn("FIFO reader thread already running");
            return;
        }
        LOGGER.info("Starting FIFO reader thread");
        fifoReaderThread = new FifoReaderThread(FIFO_FILE_PATH);
        fifoReaderThread.start();
    }

    private static void stopFifoReaderThread() {
        LOGGER.info("Stopping FIFO reader thread");
        if (fifoReaderThread != null) {
            fifoReaderThread.stopReading();
            fifoReaderThread = null;
        }
    }
}