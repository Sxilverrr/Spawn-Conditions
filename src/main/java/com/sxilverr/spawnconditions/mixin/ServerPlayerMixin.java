package com.sxilverr.spawnconditions.mixin;

import com.mojang.datafixers.util.Either;
import com.sxilverr.spawnconditions.config.SpawnConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    private static final String BED_MESSAGE_PREFIX = "block.minecraft.bed.";

    @Unique
    private BlockPos spawnconditions$sleepAttemptPos;
    @Unique
    private ResourceKey<Level> spawnconditions$deferredDimension;
    @Unique
    private float spawnconditions$deferredAngle;
    @Unique
    private boolean spawnconditions$deferredSendMessage;

    @Inject(method = "startSleepInBed", at = @At("HEAD"))
    private void spawnconditions$beginSleepAttempt(BlockPos at, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {
        this.spawnconditions$sleepAttemptPos = at;
        this.spawnconditions$deferredDimension = null;
    }

    @Inject(method = "startSleepInBed", at = @At("RETURN"))
    private void spawnconditions$setSpawnOnSleep(BlockPos at, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {
        ResourceKey<Level> dimension = this.spawnconditions$deferredDimension;
        this.spawnconditions$sleepAttemptPos = null;
        this.spawnconditions$deferredDimension = null;
        if (dimension != null && cir.getReturnValue().right().isPresent()) {
            ((ServerPlayer) (Object) this).setRespawnPosition(
                    dimension, at, this.spawnconditions$deferredAngle, false, this.spawnconditions$deferredSendMessage);
        }
    }

    @Inject(method = "setRespawnPosition", at = @At("HEAD"), cancellable = true)
    private void spawnconditions$blockPlayerSpawnSetting(ResourceKey<Level> dimension, BlockPos position, float angle, boolean forced, boolean sendMessage, CallbackInfo ci) {
        if (!SpawnConfig.enabled || position == null || forced) {
            return;
        }
        if (SpawnConfig.respawn.bedSpawnRequiresSleep && position.equals(this.spawnconditions$sleepAttemptPos)) {
            this.spawnconditions$deferredDimension = dimension;
            this.spawnconditions$deferredAngle = angle;
            this.spawnconditions$deferredSendMessage = sendMessage;
            ci.cancel();
            return;
        }
        if (!SpawnConfig.respawn.blocksSpawnSetting(dimension)) {
            return;
        }
        spawnconditions$sendBlockedMessage(true);
        ci.cancel();
    }

    @Inject(method = "displayClientMessage", at = @At("HEAD"), cancellable = true)
    private void spawnconditions$replaceBedMessage(Component message, boolean actionBar, CallbackInfo ci) {
        if (!SpawnConfig.enabled
                || !SpawnConfig.respawn.blocksSpawnSetting(((ServerPlayer) (Object) this).level().dimension())) {
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
