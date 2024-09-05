package com.example.netmod.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class HelloWorldC2SPacket {
    private final String fileData;

    public HelloWorldC2SPacket(String fileData) {
        this.fileData = fileData;
    }

    public HelloWorldC2SPacket(FriendlyByteBuf buf) {
        this.fileData = buf.readUtf(32767);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.fileData);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                System.out.println("Bytes read: " + this.fileData.length());
                player.sendSystemMessage(Component.translatable("Bytes read: " + this.fileData.length()));
                writeFileData(this.fileData);
            }
        });
    }

    private void writeFileData(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("server-side-file.txt", true))) {
            writer.write(data);
            writer.newLine();
            System.out.println("Written to file: " + data);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}