package com.sxilverr.spawnconditions.client;

import com.sxilverr.spawnconditions.config.SpawnConfig;
import com.sxilverr.spawnconditions.fabric.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class ClothConfigScreen {
    private static final int HORIZONTAL_LIMIT = 30000000;

    private ClothConfigScreen() {
    }

    public static Screen create(Screen parent) {
        Config.Data data = Config.data();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Spawn Conditions"))
                .setSavingRunnable(Config::persist);
        ConfigEntryBuilder eb = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
        general.addEntry(eb.startBooleanToggle(Component.literal("Enabled"), data.enabled)
                .setTooltip(Component.literal("Master switch."))
                .setDefaultValue(true).setSaveConsumer(v -> data.enabled = v).build());

        ConfigCategory worldSpawn = builder.getOrCreateCategory(Component.literal("World Spawn"));
        worldSpawn.addEntry(eb.startBooleanToggle(Component.literal("Randomize world spawn"), data.worldSpawn.randomize)
                .setTooltip(Component.literal("Randomize the world spawn instead of using 0,0."))
                .setDefaultValue(false).setSaveConsumer(v -> data.worldSpawn.randomize = v).build());
        worldSpawn.addEntry(eb.startEnumSelector(Component.literal("Area shape"), SpawnConfig.WorldSpawnArea.class,
                        SpawnConfig.parseEnum(data.worldSpawn.area, SpawnConfig.WorldSpawnArea.values(), SpawnConfig.WorldSpawnArea.RING))
                .setTooltip(Component.literal("Shape to pick the spawn from."))
                .setDefaultValue(SpawnConfig.WorldSpawnArea.RING)
                .setSaveConsumer(v -> data.worldSpawn.area = v.name()).build());
        worldSpawn.addEntry(eb.startIntField(Component.literal("Ring: x coordinate to start from"), data.worldSpawn.centerX)
                .setMin(-HORIZONTAL_LIMIT).setMax(HORIZONTAL_LIMIT).setDefaultValue(0)
                .setSaveConsumer(v -> data.worldSpawn.centerX = v).build());
        worldSpawn.addEntry(eb.startIntField(Component.literal("Ring: z coordinate to start from"), data.worldSpawn.centerZ)
                .setMin(-HORIZONTAL_LIMIT).setMax(HORIZONTAL_LIMIT).setDefaultValue(0)
                .setSaveConsumer(v -> data.worldSpawn.centerZ = v).build());
        worldSpawn.addEntry(eb.startIntField(Component.literal("Ring: minimum distance out"), data.worldSpawn.minRadius)
                .setMin(0).setMax(HORIZONTAL_LIMIT).setDefaultValue(0)
                .setSaveConsumer(v -> data.worldSpawn.minRadius = v).build());
        worldSpawn.addEntry(eb.startIntField(Component.literal("Ring: maximum distance out"), data.worldSpawn.maxRadius)
                .setMin(0).setMax(HORIZONTAL_LIMIT).setDefaultValue(10000)
                .setSaveConsumer(v -> data.worldSpawn.maxRadius = v).build());
        worldSpawn.addEntry(eb.startIntField(Component.literal("Box: lowest x coordinate"), data.worldSpawn.minX)
                .setMin(-HORIZONTAL_LIMIT).setMax(HORIZONTAL_LIMIT).setDefaultValue(-10000)
                .setSaveConsumer(v -> data.worldSpawn.minX = v).build());
        worldSpawn.addEntry(eb.startIntField(Component.literal("Box: highest x coordinate"), data.worldSpawn.maxX)
                .setMin(-HORIZONTAL_LIMIT).setMax(HORIZONTAL_LIMIT).setDefaultValue(10000)
                .setSaveConsumer(v -> data.worldSpawn.maxX = v).build());
        worldSpawn.addEntry(eb.startIntField(Component.literal("Box: lowest z coordinate"), data.worldSpawn.minZ)
                .setMin(-HORIZONTAL_LIMIT).setMax(HORIZONTAL_LIMIT).setDefaultValue(-10000)
                .setSaveConsumer(v -> data.worldSpawn.minZ = v).build());
        worldSpawn.addEntry(eb.startIntField(Component.literal("Box: highest z coordinate"), data.worldSpawn.maxZ)
                .setMin(-HORIZONTAL_LIMIT).setMax(HORIZONTAL_LIMIT).setDefaultValue(10000)
                .setSaveConsumer(v -> data.worldSpawn.maxZ = v).build());
        worldSpawn.addEntry(eb.startIntField(Component.literal("Attempts"), data.worldSpawn.attempts)
                .setTooltip(Component.literal("How many attempts before giving up. Each attempt loads a chunk."))
                .setMin(1).setMax(1024).setDefaultValue(32)
                .setSaveConsumer(v -> data.worldSpawn.attempts = v).build());

        ConfigCategory placement = builder.getOrCreateCategory(Component.literal("Placement"));
        placement.addEntry(eb.startBooleanToggle(Component.literal("Fluid skips the spot"), data.placement.fluidBlocksColumn)
                .setTooltip(Component.literal("Skip the whole spot when a fluid is not allowed. Off keeps looking below it."))
                .setDefaultValue(true).setSaveConsumer(v -> data.placement.fluidBlocksColumn = v).build());
        placement.addEntry(eb.startEnumSelector(Component.literal("Liquid landing"), SpawnConfig.LiquidLanding.class,
                        SpawnConfig.parseEnum(data.placement.liquidLanding, SpawnConfig.LiquidLanding.values(), SpawnConfig.LiquidLanding.SURFACE))
                .setTooltip(Component.literal("Land on top of a liquid or on the ground under it."))
                .setDefaultValue(SpawnConfig.LiquidLanding.SURFACE)
                .setSaveConsumer(v -> data.placement.liquidLanding = v.name()).build());
        placement.addEntry(eb.startEnumSelector(Component.literal("Ground requirement"), SpawnConfig.GroundRequirement.class,
                        SpawnConfig.parseEnum(data.placement.groundRequirement, SpawnConfig.GroundRequirement.values(), SpawnConfig.GroundRequirement.FULL_FACE))
                .setTooltip(Component.literal("What counts as ground to stand on."))
                .setDefaultValue(SpawnConfig.GroundRequirement.FULL_FACE)
                .setSaveConsumer(v -> data.placement.groundRequirement = v.name()).build());
        placement.addEntry(eb.startIntField(Component.literal("Empty space above player"), data.placement.requiredHeadroom)
                .setTooltip(Component.literal("Empty space needed above the player. 0 is vanilla."))
                .setMin(0).setMax(64).setDefaultValue(0)
                .setSaveConsumer(v -> data.placement.requiredHeadroom = v).build());
        placement.addEntry(eb.startBooleanToggle(Component.literal("Count liquid as empty space"), data.placement.headroomAllowsFluid)
                .setDefaultValue(true).setSaveConsumer(v -> data.placement.headroomAllowsFluid = v).build());
        placement.addEntry(eb.startIntField(Component.literal("Max search depth"), data.placement.maxSearchDepth)
                .setTooltip(Component.literal("Blocks the search may descend before giving up. 0 is unlimited."))
                .setMin(0).setMax(4096).setDefaultValue(0)
                .setSaveConsumer(v -> data.placement.maxSearchDepth = v).build());
        placement.addEntry(eb.startIntField(Component.literal("Lowest y coordinate"), data.placement.minY)
                .setMin(-2048).setMax(2048).setDefaultValue(-2048)
                .setSaveConsumer(v -> data.placement.minY = v).build());
        placement.addEntry(eb.startIntField(Component.literal("Highest y coordinate"), data.placement.maxY)
                .setTooltip(Component.literal("Lower it for cave spawns."))
                .setMin(-2048).setMax(2048).setDefaultValue(2048)
                .setSaveConsumer(v -> data.placement.maxY = v).build());
        placement.addEntry(eb.startBooleanToggle(Component.literal("Turn on the x and z limits"), data.placement.limitHorizontally)
                .setDefaultValue(false).setSaveConsumer(v -> data.placement.limitHorizontally = v).build());
        placement.addEntry(eb.startIntField(Component.literal("Lowest x coordinate"), data.placement.minX)
                .setMin(-HORIZONTAL_LIMIT).setMax(HORIZONTAL_LIMIT).setDefaultValue(-HORIZONTAL_LIMIT)
                .setSaveConsumer(v -> data.placement.minX = v).build());
        placement.addEntry(eb.startIntField(Component.literal("Highest x coordinate"), data.placement.maxX)
                .setMin(-HORIZONTAL_LIMIT).setMax(HORIZONTAL_LIMIT).setDefaultValue(HORIZONTAL_LIMIT)
                .setSaveConsumer(v -> data.placement.maxX = v).build());
        placement.addEntry(eb.startIntField(Component.literal("Lowest z coordinate"), data.placement.minZ)
                .setMin(-HORIZONTAL_LIMIT).setMax(HORIZONTAL_LIMIT).setDefaultValue(-HORIZONTAL_LIMIT)
                .setSaveConsumer(v -> data.placement.minZ = v).build());
        placement.addEntry(eb.startIntField(Component.literal("Highest z coordinate"), data.placement.maxZ)
                .setMin(-HORIZONTAL_LIMIT).setMax(HORIZONTAL_LIMIT).setDefaultValue(HORIZONTAL_LIMIT)
                .setSaveConsumer(v -> data.placement.maxZ = v).build());

        ConfigCategory filters = builder.getOrCreateCategory(Component.literal("Filters"));
        filters.addEntry(eb.startStrList(Component.literal("Biome allow list"), data.placement.biomeAllowList)
                .setTooltip(Component.literal("Leave empty for any. Use modid:biome or #modid:tag"))
                .setDefaultValue(List.of()).setSaveConsumer(v -> data.placement.biomeAllowList = v).build());
        filters.addEntry(eb.startStrList(Component.literal("Biome deny list"), data.placement.biomeDenyList)
                .setDefaultValue(List.of()).setSaveConsumer(v -> data.placement.biomeDenyList = v).build());
        filters.addEntry(eb.startStrList(Component.literal("Block allow list"), data.placement.blockAllowList)
                .setTooltip(Component.literal("Only spawn on these blocks. Leave empty for any. Use modid:block or #modid:tag"))
                .setDefaultValue(List.of()).setSaveConsumer(v -> data.placement.blockAllowList = v).build());
        filters.addEntry(eb.startStrList(Component.literal("Block deny list"), data.placement.blockDenyList)
                .setTooltip(Component.literal("Never spawn on these blocks."))
                .setDefaultValue(List.of()).setSaveConsumer(v -> data.placement.blockDenyList = v).build());
        filters.addEntry(eb.startStrList(Component.literal("Fluid allow list"), data.placement.fluidAllowList)
                .setTooltip(Component.literal("Only spawn in these fluids. Leave empty to never spawn in fluids. Use modid:fluid or #modid:tag. Tags also cover flowing fluid."))
                .setDefaultValue(List.of()).setSaveConsumer(v -> data.placement.fluidAllowList = v).build());
        filters.addEntry(eb.startStrList(Component.literal("Fluid deny list"), data.placement.fluidDenyList)
                .setTooltip(Component.literal("Never spawn in these fluids."))
                .setDefaultValue(List.of()).setSaveConsumer(v -> data.placement.fluidDenyList = v).build());

        ConfigCategory respawn = builder.getOrCreateCategory(Component.literal("Respawn"));
        respawn.addEntry(eb.startBooleanToggle(Component.literal("Player cannot set their spawn"), data.respawn.preventSpawnSetting)
                .setTooltip(Component.literal("Player cannot set their spawn. Beds and respawn anchors will not save a spawn point."))
                .setDefaultValue(false)
                .setSaveConsumer(v -> data.respawn.preventSpawnSetting = v).build());
        respawn.addEntry(eb.startStrField(Component.literal("Blocked message"), data.respawn.spawnBlockedMessage)
                .setTooltip(Component.literal("Message shown when a player cannot set their spawn. Leave empty for no message."), Component.literal("Use & for colour and format codes, like &c for red or &l for bold."))
                .setDefaultValue("&cYou cannot set your spawn.")
                .setSaveConsumer(v -> data.respawn.spawnBlockedMessage = v).build());

        return builder.build();
    }
}
