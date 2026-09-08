package com.sxilverr.spawnconditions.mixin;

import com.sxilverr.spawnconditions.core.WorldSpawnPicker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Redirect(
            method = "setInitialSpawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Climate$Sampler;findSpawnPosition()Lnet/minecraft/core/BlockPos;"
            )
    )
    private static BlockPos spawnconditions$randomiseWorldSpawn(Climate.Sampler sampler, ServerLevel level, ServerLevelData levelData, boolean bonusChest, boolean debug) {
        BlockPos randomised = WorldSpawnPicker.pick(level);
        return randomised != null ? randomised : sampler.findSpawnPosition();
    }
}
