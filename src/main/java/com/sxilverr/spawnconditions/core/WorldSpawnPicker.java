package com.sxilverr.spawnconditions.core;

import com.sxilverr.spawnconditions.config.SpawnConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public final class WorldSpawnPicker {
    private static final long SEED_SALT = 0x5350415744434E44L;

    private WorldSpawnPicker() {
    }

    @Nullable
    public static BlockPos pick(ServerLevel level) {
        SpawnConfig.WorldSpawn worldSpawn = SpawnConfig.worldSpawn;
        if (!SpawnConfig.enabled || !worldSpawn.randomize) {
            return null;
        }

        boolean box = worldSpawn.area == SpawnConfig.WorldSpawnArea.BOX;
        if (!box && worldSpawn.maxRadius <= 0) {
            return null;
        }

        RandomSource random = RandomSource.create(level.getSeed() ^ SEED_SALT);
        int attempts = worldSpawn.attempts;
        BlockPos fallback = null;

        for (int attempt = 0; attempt < attempts; attempt++) {
            BlockPos candidate = box
                    ? inBox(random, worldSpawn)
                    : inRing(random, worldSpawn);

            if (fallback == null) {
                fallback = candidate;
            }
            if (PlayerRespawnLogic.getSpawnPosInChunk(level, new ChunkPos(candidate)) != null) {
                return candidate;
            }
        }
        return fallback;
    }

    private static BlockPos inRing(RandomSource random, SpawnConfig.WorldSpawn worldSpawn) {
        double minSquared = (double) worldSpawn.minRadius * (double) worldSpawn.minRadius;
        double maxSquared = (double) worldSpawn.maxRadius * (double) worldSpawn.maxRadius;
        double angle = random.nextDouble() * Math.PI * 2.0;
        double distance = Math.sqrt(minSquared + random.nextDouble() * (maxSquared - minSquared));
        int x = worldSpawn.centerX + (int) Math.round(Math.cos(angle) * distance);
        int z = worldSpawn.centerZ + (int) Math.round(Math.sin(angle) * distance);
        return new BlockPos(x, 0, z);
    }

    private static BlockPos inBox(RandomSource random, SpawnConfig.WorldSpawn worldSpawn) {
        return new BlockPos(
                between(random, worldSpawn.minX, worldSpawn.maxX),
                0,
                between(random, worldSpawn.minZ, worldSpawn.maxZ)
        );
    }

    private static int between(RandomSource random, int min, int max) {
        long span = (long) max - (long) min + 1L;
        return (int) (min + (long) (random.nextDouble() * span));
    }
}
