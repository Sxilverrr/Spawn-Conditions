package com.sxilverr.spawnconditions.core;

import com.sxilverr.spawnconditions.config.SpawnConfig;
import com.sxilverr.spawnconditions.mixin.ServerPlayerAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public final class DimensionRespawn {
    private DimensionRespawn() {
    }

    @Nullable
    public static ServerLevel deathLevel(ServerPlayer player, boolean keepInventory) {
        if (keepInventory || !SpawnConfig.enabled) {
            return null;
        }
        ServerLevel level = player.serverLevel();
        return SpawnConfig.respawn.staysIn(level.dimension()) ? level : null;
    }

    public static boolean hasSpawnIn(ServerPlayer player, ServerLevel level) {
        return player.getRespawnPosition() != null && player.getRespawnDimension() == level.dimension();
    }

    public static boolean skipsSpawnPoint(ServerPlayer player, boolean keepInventory) {
        ServerLevel level = deathLevel(player, keepInventory);
        return level != null && player.getRespawnPosition() != null && !hasSpawnIn(player, level);
    }

    public static void copySpawnPoint(ServerPlayer from, ServerPlayer to) {
        ServerPlayerAccessor target = (ServerPlayerAccessor) to;
        target.spawnconditions$setRespawnDimension(from.getRespawnDimension());
        target.spawnconditions$setRespawnPosition(from.getRespawnPosition());
        target.spawnconditions$setRespawnAngle(from.getRespawnAngle());
        target.spawnconditions$setRespawnForced(from.isRespawnForced());
    }
}
