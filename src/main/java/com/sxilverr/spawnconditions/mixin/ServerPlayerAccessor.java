package com.sxilverr.spawnconditions.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayer.class)
public interface ServerPlayerAccessor {
    @Accessor("respawnDimension")
    void spawnconditions$setRespawnDimension(ResourceKey<Level> dimension);

    @Accessor("respawnPosition")
    void spawnconditions$setRespawnPosition(BlockPos position);

    @Accessor("respawnAngle")
    void spawnconditions$setRespawnAngle(float angle);

    @Accessor("respawnForced")
    void spawnconditions$setRespawnForced(boolean forced);
}
