package com.sxilverr.spawnconditions.mixin;

import com.sxilverr.spawnconditions.config.SpawnConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    private static final String BED_MESSAGE_PREFIX = "block.minecraft.bed.";

    @Inject(method = "setRespawnPosition", at = @At("HEAD"), cancellable = true)
    private void spawnconditions$blockPlayerSpawnSetting(ResourceKey<Level> dimension, BlockPos position, float angle, boolean forced, boolean sendMessage, CallbackInfo ci) {
        if (!SpawnConfig.enabled || !SpawnConfig.respawn.preventSpawnSetting) {
            return;
        }
        if (position == null || forced) {
            return;
        }
        spawnconditions$sendBlockedMessage(true);
        ci.cancel();
    }

    @Inject(method = "displayClientMessage", at = @At("HEAD"), cancellable = true)
    private void spawnconditions$replaceBedMessage(Component message, boolean actionBar, CallbackInfo ci) {
        if (!SpawnConfig.enabled || !SpawnConfig.respawn.preventSpawnSetting) {
            return;
        }
        if (!(message.getContents() instanceof TranslatableContents contents)
                || !contents.getKey().startsWith(BED_MESSAGE_PREFIX)) {
            return;
        }
        ci.cancel();
        spawnconditions$sendBlockedMessage(actionBar);
    }

    private void spawnconditions$sendBlockedMessage(boolean actionBar) {
        String message = SpawnConfig.respawn.message();
        if (!message.isEmpty()) {
            ((ServerPlayer) (Object) this).displayClientMessage(Component.literal(message), actionBar);
        }
    }
}
