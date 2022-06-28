package com.muffin.mixin.discord;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;


public class ChatMixin {
    @Mixin(ServerPlayNetworkHandler.class)
    public static class ServerPlayNetworkMixin {
        @Shadow @Final private MinecraftServer server;

        @Shadow public ServerPlayerEntity player;

        @ModifyArg(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V"))
        public Text onLeftMsg(Text message) {
            String[] old = message.getString().split("[ ]");
            MutableText finalMsg = Text.literal("[-] ").styled(style -> style.withColor(Formatting.DARK_RED).withBold(true));
            int i= 0;
            StringBuilder name = new StringBuilder();
            while (!old[i].equals("left")) {
                name.append(old[i]).append(" ");
                i++;
            }
            finalMsg.append(Text.literal(name.toString()).styled(style -> style.withColor(Formatting.AQUA).withBold(false)));
            finalMsg.append(Text.literal("left the game").styled(style -> style.withColor(Formatting.RED).withBold(false)));
            return finalMsg;
        }

        @Redirect(method = "handleDecoratedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/server/filter/FilteredMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/registry/RegistryKey;)V"))
        public void chatMsgModifier(PlayerManager instance, FilteredMessage<SignedMessage> message, ServerPlayerEntity sender, RegistryKey<MessageType> typeKey) {
            MutableText text = Text.literal("");
            text.append(Text.literal(this.player.getName().getString()).styled(style -> style.withColor(Formatting.AQUA)));
            text.append(Text.literal(" » ").styled(style -> style.withColor(Formatting.DARK_GRAY)));
            text.append(Text.literal(message.raw().getContent().getString()).styled(style -> style.withColor(Formatting.WHITE)));
            this.server.getPlayerManager().broadcast(text, MessageType.CHAT);
        }

    }
    @Mixin(PlayerManager.class)
    public static class PlayerManagerMixin {
        @ModifyArg(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V"))
        public Text onJoinMsg(Text message) {
            String[] old = message.getString().split("[ ]");
            MutableText finalMsg = Text.literal("[+] ").styled(style -> style.withColor(Formatting.DARK_GREEN).withBold(true));
            int i= 0;
            StringBuilder name = new StringBuilder();
            while (!old[i].equals("joined")) {
                name.append(old[i]).append(" ");
                i++;
            }
            finalMsg.append(Text.literal(name.toString()).styled(style -> style.withColor(Formatting.AQUA).withBold(false)));
            finalMsg.append(Text.literal("joined the game").styled(style -> style.withColor(Formatting.GREEN).withBold(false)));
            return finalMsg;
        }
    }
    @Mixin(ServerPlayerEntity.class)
    public static class ServerPlayerEntityMixin {
        @ModifyArg(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V"))
        public Text onDeathMsg(Text message) {
            String[] old = message.getString().split("[ ]");
            MutableText finalMsg = Text.literal("[*] ").styled(style -> style.withColor(Formatting.GRAY).withBold(true));
            String name = old[0];
            StringBuilder deathmsg = new StringBuilder(" ");
            for (int i = 1; i < old.length; i++) {
                deathmsg.append(old[i]).append(" ");
            }
            finalMsg.append(Text.literal(name).styled(style -> style.withColor(Formatting.AQUA).withBold(false)));
            finalMsg.append(Text.literal(deathmsg.toString()).styled(style -> style.withColor(Formatting.GRAY).withBold(false)));
            return finalMsg;
        }
    }
}
