package com.muffin.mixin.discord;


import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.muffin.MuffinModding;
import com.muffin.discord.DiscordListener;
import com.muffin.utils.JSONFile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.DifficultyCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


public class DiscordMixin {

    //private static final String ENVPATH = new File("").getAbsolutePath()+"/src/main/resources/env.json";

    @Mixin(MinecraftServer.class)
    public static class DiscordRunMixin {
        @Inject(method = "runServer", at = @At("HEAD"))
        public void runBot(CallbackInfo ci) {
            try {
                // here you put the DiscordBot token and the channel Id where you want to use as a bridge
                String[] data = JSONFile.getDataFile();
                DiscordListener.connect((MinecraftServer) (Object) this, data[0], data[1]);
            } catch (Exception e) {
                System.err.println("[ERROR]: File not created");
            }
        }

        @Inject(method = "runServer", at = @At("RETURN"))
        public void stopBot(CallbackInfo ci) {
            if (DiscordListener.chatBridge)
                DiscordListener.stop();
        }
    }

    @Mixin(ServerPlayNetworkHandler.class)
    public static class DiscordChatMixin {
        @Shadow public ServerPlayerEntity player;
        @Inject(method = "onChatMessage", at = @At("RETURN"))
        public void chatMsg(ChatMessageC2SPacket packet, CallbackInfo ci) {
            System.out.println("["+player.getName().getString()+"]: " + packet.getChatMessage());
            DiscordListener.sendMessage("**"+player.getName().getString()+"** » " + packet.getChatMessage());
            //DiscordListener.sendMessage(packet.getChatMessage());
        }

        @Inject(method = "onDisconnected", at = @At("RETURN"))
        public void onLeftServer(Text reason, CallbackInfo ci) {
            if (DiscordListener.chatBridge) {
                DiscordListener.sendMessage(":x: ***"+player.getName().getString()+"*** has left the game");
            }
        }
    }

    @Mixin(PlayerManager.class)
    public static class DiscordJoinMixin {
        @Inject(method = "onPlayerConnect", at = @At("RETURN"))
        public void onJoinServer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
            if (DiscordListener.chatBridge) {
                DiscordListener.sendMessage(":white_check_mark: ***" + player.getName().getString()+"*** has connected");
            }
        }
    }

    @Mixin(ServerPlayerEntity.class)
    public abstract static class DiscordDeathMixin extends PlayerEntity {

        public DiscordDeathMixin(World w, BlockPos b, float y, GameProfile g, @Nullable PlayerPublicKey p) {
            super(w, b, y, g, p);
        }

        @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/DeathMessageS2CPacket;<init>(Lnet/minecraft/entity/damage/DamageTracker;Lnet/minecraft/text/Text;)V"))
        public void onDeathServer(DamageSource damageSource, CallbackInfo ci) {
            if (DiscordListener.chatBridge) {

                DiscordListener.sendMessage(":skull: " + this.getDamageTracker().getDeathMessage().getString());
            }
        }
    }

    @Mixin(DifficultyCommand.class)
    public static class DiscordCommandMixin {
        @Inject(method = "register", at = @At("RETURN"))
        private static void registerMuffin(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo ci) {
            MuffinModding.registerCmds(dispatcher);
        }
    }

}
