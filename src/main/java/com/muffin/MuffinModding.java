package com.muffin;

import com.muffin.discord.DiscordCommand;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.command.ServerCommandSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuffinModding implements ModInitializer {

    public static void registerCmds(CommandDispatcher<ServerCommandSource> dispatcher) {

        DiscordCommand.register(dispatcher);

    }
    public static final Logger LOGGER = LoggerFactory.getLogger("muffin");
    @Override
    public void onInitialize() {
        LOGGER.info("MuffinMod ready!");
    }
}
