package com.example.covertmod;

import com.example.covertmod.events.ClientEvents;
import com.example.covertmod.networking.ModMessages;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Main class for the CovertMod.
 * This class is responsible for initializing the mod and registering event listeners.
 */
@Mod(CovertMod.MODID)
public class CovertMod {
    // The mod ID
    public static final String MODID = "covertmod";
    // Logger instance for logging events
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Constructor for the CovertMod class.
     * Registers event listeners for mod and MinecraftForge event buses.
     *
     * @param context the mod loading context
     */
    public CovertMod(FMLJavaModLoadingContext context) {
        // Get the mod event bus
        IEventBus modEventBus = context.getModEventBus();
        // Register common setup listener
        modEventBus.addListener(this::commonSetup);
        // Register client setup listener
        modEventBus.addListener(this::clientSetup);
        // Register this class to the MinecraftForge event bus
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Common setup method.
     * This method is called during the common setup phase of mod loading.
     *
     * @param event the common setup event
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Register mod messages
        event.enqueueWork(ModMessages::register);
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    /**
     * Client setup method.
     * This method is called during the client setup phase of mod loading.
     *
     * @param event the client setup event
     */
    private void clientSetup(final FMLClientSetupEvent event) {
        // Register client events
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        LOGGER.info("HELLO FROM CLIENT SETUP");
    }

    /**
     * Server starting event handler.
     * This method is called when the server is starting.
     *
     * @param event the server starting event
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}