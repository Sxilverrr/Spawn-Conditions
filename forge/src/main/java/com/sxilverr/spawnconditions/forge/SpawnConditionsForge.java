package com.sxilverr.spawnconditions.forge;

import com.sxilverr.spawnconditions.SpawnConditions;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(SpawnConditions.MOD_ID)
public final class SpawnConditionsForge {
    public SpawnConditionsForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
