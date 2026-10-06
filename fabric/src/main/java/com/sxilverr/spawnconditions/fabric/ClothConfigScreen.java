package com.sxilverr.spawnconditions.fabric;

import com.sxilverr.spawnconditions.config.SpawnConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;

import static net.minecraft.network.chat.Component.literal;

public final class ClothConfigScreen {
    private static final int LIMIT = 30000000;

    private ClothConfigScreen() {
    }

    public static Screen create(Screen parent) {
        Config.Data data = Config.data();
        SpawnConfig.WorldSpawn ws = data.worldSpawn;
        SpawnConfig.Placement pl = data.placement;
        SpawnConfig.Respawn rs = data.respawn;
        SpawnConfig.WorldSpawn dws = new SpawnConfig.WorldSpawn();
        SpawnConfig.Placement dpl = new SpawnConfig.Placement();
        SpawnConfig.Respawn drs = new SpawnConfig.Respawn();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(literal("Spawn Conditions"))
                .setSavingRunnable(Config::save);
        ConfigEntryBuilder eb = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(literal("General"));
        general.addEntry(eb.startBooleanToggle(literal("Enabled"), data.enabled).setTooltip(literal("Master switch."))
                .setDefaultValue(true).setSaveConsumer(v -> data.enabled = v).build());

        ConfigCategory worldSpawn = builder.getOrCreateCategory(literal("World Spawn"));
        worldSpawn.addEntry(eb.startBooleanToggle(literal("Randomize world spawn"), ws.randomize)
                .setTooltip(literal("Randomize the world spawn instead of using 0,0."))
                .setDefaultValue(dws.randomize).setSaveConsumer(v -> ws.randomize = v).build());
        worldSpawn.addEntry(eb.startEnumSelector(literal("Area shape"), SpawnConfig.WorldSpawnArea.class, ws.area)
                .setTooltip(literal("Shape to pick the spawn from."))
                .setDefaultValue(dws.area).setSaveConsumer(v -> ws.area = v).build());
        worldSpawn.addEntry(coord(eb, "Ring: x coordinate to start from", ws.centerX, dws.centerX).setSaveConsumer(v -> ws.centerX = v).build());
        worldSpawn.addEntry(coord(eb, "Ring: z coordinate to start from", ws.centerZ, dws.centerZ).setSaveConsumer(v -> ws.centerZ = v).build());
        worldSpawn.addEntry(coord(eb, "Ring: minimum distance out", ws.minRadius, dws.minRadius).setMin(0).setSaveConsumer(v -> ws.minRadius = v).build());
        worldSpawn.addEntry(coord(eb, "Ring: maximum distance out", ws.maxRadius, dws.maxRadius).setMin(0).setSaveConsumer(v -> ws.maxRadius = v).build());
        worldSpawn.addEntry(coord(eb, "Box: lowest x coordinate", ws.minX, dws.minX).setSaveConsumer(v -> ws.minX = v).build());
        worldSpawn.addEntry(coord(eb, "Box: highest x coordinate", ws.maxX, dws.maxX).setSaveConsumer(v -> ws.maxX = v).build());
        worldSpawn.addEntry(coord(eb, "Box: lowest z coordinate", ws.minZ, dws.minZ).setSaveConsumer(v -> ws.minZ = v).build());
        worldSpawn.addEntry(coord(eb, "Box: highest z coordinate", ws.maxZ, dws.maxZ).setSaveConsumer(v -> ws.maxZ = v).build());
        worldSpawn.addEntry(eb.startIntField(literal("Attempts"), ws.attempts)
                .setTooltip(literal("How many attempts before giving up. Each attempt loads a chunk."))
                .setMin(1).setMax(1024).setDefaultValue(dws.attempts).setSaveConsumer(v -> ws.attempts = v).build());

        ConfigCategory placement = builder.getOrCreateCategory(literal("Placement"));
        placement.addEntry(eb.startBooleanToggle(literal("Fluid skips the spot"), pl.fluidBlocksColumn)
                .setTooltip(literal("Skip the whole spot when a fluid is not allowed. Off keeps looking below it."))
                .setDefaultValue(dpl.fluidBlocksColumn).setSaveConsumer(v -> pl.fluidBlocksColumn = v).build());
        placement.addEntry(eb.startEnumSelector(literal("Liquid landing"), SpawnConfig.LiquidLanding.class, pl.liquidLanding)
                .setTooltip(literal("Land on top of a liquid or on the ground under it."))
                .setDefaultValue(dpl.liquidLanding).setSaveConsumer(v -> pl.liquidLanding = v).build());
        placement.addEntry(eb.startEnumSelector(literal("Ground requirement"), SpawnConfig.GroundRequirement.class, pl.groundRequirement)
                .setTooltip(literal("What counts as ground to stand on."))
                .setDefaultValue(dpl.groundRequirement).setSaveConsumer(v -> pl.groundRequirement = v).build());
        placement.addEntry(eb.startIntField(literal("Empty space above player"), pl.requiredHeadroom)
                .setTooltip(literal("Empty space needed above the player. 0 is vanilla."))
                .setMin(0).setMax(64).setDefaultValue(dpl.requiredHeadroom).setSaveConsumer(v -> pl.requiredHeadroom = v).build());
        placement.addEntry(eb.startBooleanToggle(literal("Count liquid as empty space"), pl.headroomAllowsFluid)
                .setDefaultValue(dpl.headroomAllowsFluid).setSaveConsumer(v -> pl.headroomAllowsFluid = v).build());
        placement.addEntry(eb.startIntField(literal("Max search depth"), pl.maxSearchDepth)
                .setTooltip(literal("Blocks the search may descend before giving up. 0 is unlimited."))
                .setMin(0).setMax(4096).setDefaultValue(dpl.maxSearchDepth).setSaveConsumer(v -> pl.maxSearchDepth = v).build());
        placement.addEntry(eb.startIntField(literal("Lowest y coordinate"), pl.minY)
                .setMin(-2048).setMax(2048).setDefaultValue(dpl.minY).setSaveConsumer(v -> pl.minY = v).build());
        placement.addEntry(eb.startIntField(literal("Highest y coordinate"), pl.maxY).setTooltip(literal("Lower it for cave spawns."))
                .setMin(-2048).setMax(2048).setDefaultValue(dpl.maxY).setSaveConsumer(v -> pl.maxY = v).build());
        placement.addEntry(eb.startBooleanToggle(literal("Turn on the x and z limits"), pl.limitHorizontally)
                .setDefaultValue(dpl.limitHorizontally).setSaveConsumer(v -> pl.limitHorizontally = v).build());
        placement.addEntry(coord(eb, "Lowest x coordinate", pl.minX, dpl.minX).setSaveConsumer(v -> pl.minX = v).build());
        placement.addEntry(coord(eb, "Highest x coordinate", pl.maxX, dpl.maxX).setSaveConsumer(v -> pl.maxX = v).build());
        placement.addEntry(coord(eb, "Lowest z coordinate", pl.minZ, dpl.minZ).setSaveConsumer(v -> pl.minZ = v).build());
        placement.addEntry(coord(eb, "Highest z coordinate", pl.maxZ, dpl.maxZ).setSaveConsumer(v -> pl.maxZ = v).build());

        ConfigCategory filters = builder.getOrCreateCategory(literal("Filters"));
        filters.addEntry(eb.startStrList(literal("Biome allow list"), pl.biomeAllowList)
                .setTooltip(literal("Leave empty for any. Use modid:biome or #modid:tag"))
                .setDefaultValue(List.of()).setSaveConsumer(v -> pl.biomeAllowList = v).build());
        filters.addEntry(eb.startStrList(literal("Biome deny list"), pl.biomeDenyList)
                .setDefaultValue(List.of()).setSaveConsumer(v -> pl.biomeDenyList = v).build());
        filters.addEntry(eb.startStrList(literal("Block allow list"), pl.blockAllowList)
                .setTooltip(literal("Only spawn on these blocks. Leave empty for any. Use modid:block or #modid:tag"))
                .setDefaultValue(List.of()).setSaveConsumer(v -> pl.blockAllowList = v).build());
        filters.addEntry(eb.startStrList(literal("Block deny list"), pl.blockDenyList)
                .setTooltip(literal("Never spawn on these blocks."))
                .setDefaultValue(List.of()).setSaveConsumer(v -> pl.blockDenyList = v).build());
        filters.addEntry(eb.startStrList(literal("Fluid allow list"), pl.fluidAllowList)
                .setTooltip(literal("Only spawn in these fluids. Leave empty to never spawn in fluids. Use modid:fluid or #modid:tag. Tags also cover flowing fluid."))
                .setDefaultValue(List.of()).setSaveConsumer(v -> pl.fluidAllowList = v).build());
        filters.addEntry(eb.startStrList(literal("Fluid deny list"), pl.fluidDenyList)
                .setTooltip(literal("Never spawn in these fluids."))
                .setDefaultValue(List.of()).setSaveConsumer(v -> pl.fluidDenyList = v).build());

        ConfigCategory respawn = builder.getOrCreateCategory(literal("Respawn"));
        respawn.addEntry(eb.startBooleanToggle(literal("Player cannot set their spawn"), rs.preventSpawnSetting)
                .setTooltip(literal("Player cannot set their spawn. Beds and respawn anchors will not save a spawn point."))
                .setDefaultValue(drs.preventSpawnSetting).setSaveConsumer(v -> rs.preventSpawnSetting = v).build());
        respawn.addEntry(eb.startStrList(literal("Dimensions where spawn cannot be set"), rs.preventSpawnSettingDimensions)
                .setTooltip(literal("Player cannot set their spawn in these dimensions. Use modid:dimension"))
                .setDefaultValue(List.of()).setSaveConsumer(v -> rs.preventSpawnSettingDimensions = v).build());
        respawn.addEntry(eb.startStrField(literal("Blocked message"), rs.spawnBlockedMessage)
                .setTooltip(literal("Message shown when a player cannot set their spawn. Leave empty for no message."),
                        literal("Use & for colour and format codes, like &c for red or &l for bold."))
                .setDefaultValue(drs.spawnBlockedMessage).setSaveConsumer(v -> rs.spawnBlockedMessage = v).build());
        respawn.addEntry(eb.startBooleanToggle(literal("Beds only set spawn when slept in"), rs.bedSpawnRequiresSleep)
                .setTooltip(literal("Beds only save a spawn point when the player sleeps in them."))
                .setDefaultValue(drs.bedSpawnRequiresSleep).setSaveConsumer(v -> rs.bedSpawnRequiresSleep = v).build());
        respawn.addEntry(eb.startStrList(literal("Respawn in the same dimension"), rs.stayInDimensions)
                .setTooltip(literal("Players who die in these dimensions respawn in the same dimension. Use modid:dimension"),
                        literal("Spread out by the spawnRadius gamerule unless the player has a spawn point there."))
                .setDefaultValue(List.of()).setSaveConsumer(v -> rs.stayInDimensions = v).build());

        return builder.build();
    }

    private static IntFieldBuilder coord(ConfigEntryBuilder eb, String label, int value, int defaultValue) {
        return eb.startIntField(literal(label), value).setMin(-LIMIT).setMax(LIMIT).setDefaultValue(defaultValue);
    }
}
