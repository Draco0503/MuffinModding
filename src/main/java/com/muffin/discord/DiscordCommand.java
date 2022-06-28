package com.muffin.discord;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


public class DiscordCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(literal("discord").then(
                CommandManager.literal("setBot").executes(context -> setup(context.getSource()))).then(
                        CommandManager.literal("start").executes(context -> start(context.getSource()))).then(
                                CommandManager.literal("stop").executes(context -> stop(context.getSource()))));

    }

    public static int setup(ServerCommandSource source) {
        MutableText finalMsg = Text.literal("");
        if (DiscordListener.chatBridge){
            finalMsg.append(Text.literal("[WARN]: ").styled(style -> style.withColor(Formatting.GOLD).withBold(true)));
            finalMsg.append(Text.literal("Stop the server before making any changes").styled(style -> style.withColor(Formatting.YELLOW)));
        } else {
            finalMsg.append(Text.literal("[INFO]: ").styled(style -> style.withColor(Formatting.GREEN).withBold(true)));
            finalMsg.append(Text.literal("Done").styled(style -> style.withColor(Formatting.GRAY)));
        }
        source.sendFeedback(finalMsg, false);
        return 1;
    }

    private static int start(ServerCommandSource source) {
        MutableText finalMsg = Text.literal("");
        if (!DiscordListener.chatBridge){
            try {
                finalMsg.append(Text.literal("[INFO]: ").styled(style -> style.withColor(Formatting.GREEN).withBold(true)));
                finalMsg.append(Text.literal("Discord integration is running").styled(style -> style.withColor(Formatting.GRAY)));
                DiscordListener.connect(source.getServer(), DiscordListener.token, DiscordListener.channelId);
                source.sendFeedback(finalMsg, false);

            } catch (Exception e) {
                finalMsg.append(Text.literal("[ERROR]: ").styled(style -> style.withColor(Formatting.DARK_RED).withBold(true)));
                finalMsg.append(Text.literal("Unable to start the process, check the token").styled(style -> style.withColor(Formatting.RED)));
                source.sendFeedback(finalMsg, false);
            }
        } else {
            finalMsg.append(Text.literal("[WARN]: ").styled(style -> style.withColor(Formatting.GOLD).withBold(true)));
            finalMsg.append(Text.literal("Discord integration already on").styled(style -> style.withColor(Formatting.YELLOW)));
            source.sendFeedback(finalMsg, false);
        }
        return 1;
    }

    private static int stop(ServerCommandSource source) {
        MutableText finalMsg = Text.literal("");
        if (DiscordListener.chatBridge) {
            finalMsg.append(Text.literal("[INFO]: ").styled(style -> style.withColor(Formatting.DARK_GREEN).withBold(true)));
            finalMsg.append(Text.literal("Discord integration has stopped").styled(style -> style.withColor(Formatting.GREEN)));
            DiscordListener.stop();
        } else {
            finalMsg.append(Text.literal("[WARN]: ").styled(style -> style.withColor(Formatting.GOLD).withBold(true)));
            finalMsg.append(Text.literal("Discord integration is already off").styled(style -> style.withColor(Formatting.YELLOW)));
        }
        source.sendFeedback(finalMsg, false);
        return 1;
    }


}
