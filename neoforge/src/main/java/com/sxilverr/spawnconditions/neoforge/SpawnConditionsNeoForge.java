package com.sxilverr.spawnconditions.neoforge;

import com.sxilverr.spawnconditions.SpawnConditions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

@Mod(SpawnConditions.MOD_ID)
public final class SpawnConditionsNeoForge {
    public SpawnConditionsNeoForge(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modBus.addListener(ModConfigEvent.Loading.class, event -> Config.bake());
        modBus.addListener(ModConfigEvent.Reloading.class, event -> Config.bake());
    }
}
