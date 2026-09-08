package com.sxilverr.spawnconditions.mixin;

import com.sxilverr.spawnconditions.config.SpawnConfig;
import com.sxilverr.spawnconditions.core.SpawnPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRespawnLogic.class)
public class PlayerRespawnLogicMixin {
    @Inject(method = "getOverworldRespawnPos", at = @At("HEAD"), cancellable = true)
    private static void spawnconditions$applySpawnConditions(ServerLevel level, int x, int z, CallbackInfoReturnable<BlockPos> cir) {
        if (!SpawnConfig.enabled) {
            return;
        }
        cir.setReturnValue(SpawnPlacement.findRespawnPos(level, x, z));
    }
}
