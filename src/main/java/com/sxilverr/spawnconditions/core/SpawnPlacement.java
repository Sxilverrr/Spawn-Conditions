package com.sxilverr.spawnconditions.core;

import com.sxilverr.spawnconditions.config.SpawnConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public final class SpawnPlacement {
    private SpawnPlacement() {
    }

    @Nullable
    public static BlockPos findRespawnPos(ServerLevel level, int x, int z) {
        SpawnConfig.Placement placement = SpawnConfig.placement;

        int lowest = Math.max(level.getMinBuildHeight(), placement.minY);
        int highest = Math.min(level.getMaxBuildHeight() - 1, placement.maxY);
        if (lowest > highest || !placement.withinHorizontalBounds(x, z)) {
            return null;
        }

        ChunkAccess chunk = level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        int surface = level.dimensionType().hasCeiling()
                ? level.getChunkSource().getGenerator().getSpawnHeight(level)
                : chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, x & 15, z & 15);
        if (surface < lowest) {
            return null;
        }

        int top = Math.min(surface + 1, highest);
        int floor = placement.maxSearchDepth > 0
                ? Math.max(lowest, top - placement.maxSearchDepth)
                : lowest;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = top; y >= floor; y--) {
            BlockState state = level.getBlockState(cursor.set(x, y, z));
            FluidState fluid = state.getFluidState();

            if (!fluid.isEmpty()) {
                if (!placement.fluidAllowed(fluid)) {
                    if (placement.fluidBlocksColumn) {
                        return null;
                    }
                    continue;
                }
                if (placement.liquidLanding == SpawnConfig.LiquidLanding.SURFACE) {
                    BlockPos accepted = accept(level, new BlockPos(x, y, z), state, placement);
                    if (accepted != null) {
                        return accepted;
                    }
                }
                continue;
            }

            if (isGround(level, cursor, state, placement)) {
                BlockPos accepted = accept(level, new BlockPos(x, y + 1, z), state, placement);
                if (accepted != null) {
                    return accepted;
                }
            }
        }
        return null;
    }

    private static boolean isGround(ServerLevel level, BlockPos pos, BlockState state, SpawnConfig.Placement placement) {
        return switch (placement.groundRequirement) {
            case ANY_BLOCK -> !state.isAir();
            case ANY_COLLISION -> !state.getCollisionShape(level, pos).isEmpty();
            case FULL_FACE -> Block.isFaceFull(state.getCollisionShape(level, pos), Direction.UP);
        };
    }

    @Nullable
    private static BlockPos accept(ServerLevel level, BlockPos pos, BlockState support, SpawnConfig.Placement placement) {
        boolean accepted = pos.getY() <= placement.maxY
                && placement.blockAllowed(support)
                && placement.biomeAllowed(level.getBiome(pos))
                && hasHeadroom(level, pos, placement);
        return accepted ? pos : null;
    }

    private static boolean hasHeadroom(ServerLevel level, BlockPos pos, SpawnConfig.Placement placement) {
        int limit = pos.getY() + Math.min(placement.requiredHeadroom, level.getMaxBuildHeight() - pos.getY());
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = pos.getY(); y < limit; y++) {
            BlockState state = level.getBlockState(cursor.set(pos.getX(), y, pos.getZ()));
            boolean blocked = state.getFluidState().isEmpty()
                    ? !state.getCollisionShape(level, cursor).isEmpty()
                    : !placement.headroomAllowsFluid;
            if (blocked) {
                return false;
            }
        }
        return true;
    }
}
