package com.example.netmod;

import com.example.netmod.events.ClientEvents;
import com.example.netmod.networking.ModMessages;
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

@Mod(NetMod.MODID)
public class NetMod {
    public static final String MODID = "netmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NetMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        LOGGER.info("HELLO FROM CLIENT SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}