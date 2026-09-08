# Spawn Conditions

Customizable player spawn conditions.

## Configuration

| Loader | File |
| ------ | ---- |
| Forge, NeoForge | `config/spawnconditions-common.toml` |
| Fabric | `config/spawnconditions.json` |

| Option | Default | Meaning |
| ------ | ------- | ------- |
| `enabled` | `true` | Master switch. |
| `worldSpawn.randomize` | `false` | Randomize the world spawn instead of using 0,0. |
| `worldSpawn.area` | `RING` | Shape to pick the spawn from. |
| `worldSpawn.centerX` / `centerZ` | `0` | RING: the x and z coordinates to start from. |
| `worldSpawn.minRadius` / `maxRadius` | `0` / `10000` | RING: minimum and maximum for how far out to spawn. |
| `worldSpawn.minX` / `maxX` / `minZ` / `maxZ` | `-10000` / `10000` | BOX: lowest and highest x and z coordinates a spawn can use. |
| `worldSpawn.attempts` | `32` | How many attempts before giving up. Each attempt loads a chunk. |
| `placement.fluidBlocksColumn` | `true` | Skip the whole spot when a fluid is not allowed. Off keeps looking below it. |
| `placement.liquidLanding` | `SURFACE` | Land on top of a liquid or on the ground under it. |
| `placement.groundRequirement` | `FULL_FACE` | What counts as ground to stand on. |
| `placement.requiredHeadroom` | `0` | Empty space needed above the player. 0 is vanilla. |
| `placement.headroomAllowsFluid` | `true` | Count liquid as empty space above the player. |
| `placement.maxSearchDepth` | `0` | Blocks the search may descend before giving up. 0 is unlimited. |
| `placement.minY` / `maxY` | `-2048` / `2048` | Lowest and highest y coordinate that a spawn can use. |
| `placement.limitHorizontally` | `false` | Turn on the x and z limits below. |
| `placement.minX` / `maxX` / `minZ` / `maxZ` | `-30000000` / `30000000` | Lowest and highest x and z coordinates a spawn can use. |
| `placement.biomeAllowList` | `[]` | Only spawn in these biomes. Leave empty for any. |
| `placement.biomeDenyList` | `[]` | Never spawn in these biomes. |
| `placement.blockAllowList` | `[]` | Only spawn on these blocks. Leave empty for any. |
| `placement.blockDenyList` | `[]` | Never spawn on these blocks. |
| `placement.fluidAllowList` | `[]` | Only spawn in these fluids. Leave empty to never spawn in fluids. |
| `placement.fluidDenyList` | `[]` | Never spawn in these fluids. |
| `respawn.preventSpawnSetting` | `false` | Player cannot set their spawn. Beds and respawn anchors will not save a spawn point. |
| `respawn.spawnBlockedMessage` | `&cYou cannot set your spawn.` | Message shown when a player cannot set their spawn. Leave empty for no message. |

## Lists

Entries are ids like `modid:name`, or tags with a `#` in front like `#modid:name`. Anything a mod
adds works the same as a vanilla entry. The deny list is checked first, so an entry in both lists is
denied.

Fluid lists work the other way round from the biome and block lists. An empty `fluidAllowList` means
no fluid spawning, which is what vanilla does. Use tags for fluids because a fluid has a state for still and flowing, so
`#minecraft:water` will do both while `minecraft:water` will only touch the still state.

The block lists check whatever the player ends up standing on.

## Ground requirement

| Value | Ground is |
| ----- | --------- |
| `FULL_FACE` | A block with a full top face. This is vanilla, so slabs, stairs, fences and walls are skipped. |
| `ANY_COLLISION` | Any block with a collision box, so slabs, stairs, fences and walls all count. |
| `ANY_BLOCK` | Any block that is not air, including ones you fall through like grass, flowers and torches. |

Leaves have a full collision box, so they already count as ground under `FULL_FACE`.

## Cave spawns

`maxY` is what makes cave spawns possible. The search always runs down from `maxY`, so setting it below
the surface starts the search underground and takes the first floor it finds:

```json
"placement": {
  "maxY": -10,
  "minY": -60,
  "requiredHeadroom": 2
}
```

`requiredHeadroom` is needed here. Without it the search stops on the first full block at `maxY`,
which is usually stone, and spawns the player inside it. With 2 the search skips solid rock and
keeps going down until it finds a floor with air above it. Add
`"biomeAllowList": ["minecraft:lush_caves"]` to pick a cave biome. Biomes are 3D, so the check runs
at the spawn spot, not at the surface.

## Notes

`worldSpawn` sets the point everything else is measured from. It only runs for the overworld of a
new world. A world that already exists has saved its spawn.

The vanilla `spawnRadius` gamerule can be used to specify spawn radius in blocks around 0,0. `/gamerule spawnRadius 2000`.
If you want this to occur on every world, use the [Global GameRules](https://www.curseforge.com/minecraft/mc-mods/global-gamerules) mod.

`preventSpawnSetting` stops beds and respawn anchors saving a spawn point, so every death goes back
through the rules you set. `/spawnpoint` still works.

`spawnBlockedMessage` This is what the player sees above their hotbar when they cannot set their spawn. Put `&` in
front of a color or format code, like `&c` for red, `&l` for bold or `&r` to reset. Leave the option empty for no message.

Every default is vanilla, so a new config with `enabled` on changes nothing until you turn something on. 

For those who specifically want RLCraft style spawning, put `#minecraft:water` in `placement.fluidAllowList`,
set `placement.liquidLanding` to `FLOOR` so players land on the sea bed, and run
`/gamerule spawnRadius 20000`.
