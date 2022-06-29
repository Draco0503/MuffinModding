package com.muffin.discord;

import com.muffin.utils.ConsoleColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Objects;

public class DiscordListener extends ListenerAdapter {

    private static JDA bot = null;
    private static TextChannel channel = null;
    public static boolean chatBridge = false;

    private MinecraftServer ms;

    public DiscordListener(MinecraftServer ms) {
        this.ms = ms;
    }

    public static void connect(MinecraftServer m, String tk, String chId) throws LoginException, InterruptedException, IllegalArgumentException {
        chatBridge = false;
        bot = JDABuilder.createDefault(tk).addEventListeners(new DiscordListener(m)).build();
        bot.awaitReady();
        channel = bot.getTextChannelById(chId);
        if (channel == null) {
            stop();
            throw new IllegalArgumentException();
        }
        chatBridge = true;


    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println(ConsoleColors.CYAN + "[INFO]: Muffin Ready"+
                ConsoleColors.RESET);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (chatBridge && event.getChannel().equals(channel)) {
            if (event.getAuthor().isBot()) return;
            if (event.getMessage().getContentDisplay().equals("!online")) {
                StringBuilder msg = new StringBuilder();
                for (ServerPlayerEntity player : ms.getPlayerManager().getPlayerList()){
                    msg.append("# _").append(player.getName().getString().replace("_", "\\_")).append("_").append("\n");
                }
                event.getChannel().sendMessageEmbeds(embedMsg(msg.toString()).build()).queue();

            } else {
                if (event.getMessage().getContentDisplay().equals("")) return;
                MutableText finalMsg = Text.literal("");
                finalMsg.append(Text.literal("[DC]").styled(style -> style.withColor(Formatting.GRAY).withBold(true)));
                String user = "["+ Objects.requireNonNull(event.getMember()).getEffectiveName()+"]: ";
                String msg = event.getMessage().getContentDisplay();
                finalMsg.append(Text.literal(user).styled(style -> style.withColor(Formatting.DARK_AQUA).withBold(false)));
                finalMsg.append(Text.literal(msg).styled(style -> style.withColor(Formatting.WHITE).withBold(false)));
                ms.getPlayerManager().broadcast(finalMsg, MessageType.SYSTEM);
            }
        }
    }

    public static void stop() {
        bot.shutdownNow();
        chatBridge=false;
    }

    public static void sendMessage(String msg){
        if (chatBridge && channel != null){
            channel.sendMessage(msg).queue();
        }
    }
    public EmbedBuilder embedMsg(String msg) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Players Online");
        eb.setColor(new Color(0x4383BF));
        eb.setDescription(msg);
        return eb;
    }

}
