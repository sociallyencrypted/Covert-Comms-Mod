package com.example.netmod.events;

import com.example.netmod.utils.FifoReaderThread;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.example.netmod.NetMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    private static final String FIFO_FILE_PATH = "/Users/sociallyencrypted/try1.txt";
    private static final String START_COMMAND = "hello";
    private static final String STOP_COMMAND = "stop";
    private static FifoReaderThread fifoReaderThread;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        System.out.println("HELLO FROM CLIENT SETUP");
    }

    @SubscribeEvent
    public void onClientChatMessage(ClientChatEvent event) {
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
        fifoReaderThread = new FifoReaderThread(FIFO_FILE_PATH);
        fifoReaderThread.start();
    }

    private static void stopFifoReaderThread() {
        if (fifoReaderThread != null) {
            fifoReaderThread.stopReading();
            fifoReaderThread = null;
        }
    }
}