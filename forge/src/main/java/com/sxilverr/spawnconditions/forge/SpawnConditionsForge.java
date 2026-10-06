package com.sxilverr.spawnconditions.forge;

import com.sxilverr.spawnconditions.config.SpawnConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(SpawnConfig.MOD_ID)
public final class SpawnConditionsForge {
    public SpawnConditionsForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
