package com.sxilverr.spawnconditions.fabric;

import net.fabricmc.api.ModInitializer;

public class SpawnConditionsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Config.load();
    }
}
