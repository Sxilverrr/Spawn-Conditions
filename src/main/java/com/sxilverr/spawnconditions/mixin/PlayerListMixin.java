package com.sxilverr.spawnconditions.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sxilverr.spawnconditions.core.DimensionRespawn;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if >=1.21 {
/*import net.minecraft.world.level.portal.DimensionTransition;
*///?} else {
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
//?}

@Mixin(PlayerList.class)
public class PlayerListMixin {
    //? if >=1.21 {
    /*@WrapOperation(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnPositionAndUseSpawnBlock(ZLnet/minecraft/world/level/portal/DimensionTransition$PostDimensionTransition;)Lnet/minecraft/world/level/portal/DimensionTransition;"
            )
    )
    private DimensionTransition spawnconditions$respawnInDeathDimension(ServerPlayer player, boolean keepInventory, DimensionTransition.PostDimensionTransition after, Operation<DimensionTransition> original) {
        ServerLevel level = DimensionRespawn.deathLevel(player, keepInventory);
        if (level == null) {
            return original.call(player, keepInventory, after);
        }
        if (DimensionRespawn.hasSpawnIn(player, level)) {
            DimensionTransition vanilla = original.call(player, keepInventory, after);
            return vanilla.newLevel() == level ? vanilla : DimensionTransition.missingRespawnBlock(level, player, after);
        }
        return new DimensionTransition(level, player, after);
    }
    *///?} else {
    @WrapOperation(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;getRespawnPosition()Lnet/minecraft/core/BlockPos;"
            )
    )
    private BlockPos spawnconditions$skipSpawnPointOutsideDimension(ServerPlayer player, Operation<BlockPos> original, @Local(argsOnly = true) boolean keepEverything) {
        return DimensionRespawn.skipsSpawnPoint(player, keepEverything) ? null : original.call(player);
    }

    @WrapOperation(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;overworld()Lnet/minecraft/server/level/ServerLevel;"
            )
    )
    private ServerLevel spawnconditions$respawnInDeathDimension(MinecraftServer server, Operation<ServerLevel> original, @Local(argsOnly = true) ServerPlayer player, @Local(argsOnly = true) boolean keepEverything) {
        ServerLevel level = DimensionRespawn.deathLevel(player, keepEverything);
        return level != null ? level : original.call(server);
    }

    @WrapOperation(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V"
            )
    )
    private void spawnconditions$keepSpawnPoint(ServerPlayer respawned, ServerPlayer dead, boolean keepEverything, Operation<Void> original) {
        original.call(respawned, dead, keepEverything);
        if (DimensionRespawn.skipsSpawnPoint(dead, keepEverything)) {
            DimensionRespawn.copySpawnPoint(dead, respawned);
        }
    }
    //?}
}
