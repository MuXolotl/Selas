<div align="center">

![Selas-banner](https://raw.githubusercontent.com/MuXolotl/Selas/main/assets/image/banner.png)

# ☀️ Selas 🌑

Selas is a client-side NeoForge mod for Minecraft 1.21.1.

It reworks Minecraft's vanilla lightmap to make nights, caves, rain, and thunder darker in a more natural and readable way. The goal is not pure black darkness, but lighting that feels more grounded: sealed unlit spaces should approach black, while open nights should still keep moonlight, starlight, and a little atmospheric glow.

Selas does **not** add blocks, items, mobs, dimensions, or dynamic lights. It is a visual lighting mod focused on the vanilla lightmap, with light atmospheric fog tuning layered on top.

## Downloads

[![CurseForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/selas) [![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/selas) [![GitHub Releases](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/MuXolotl/Selas/releases/latest)

</div>

---

## What it changes

- night brightness
- cave darkness
- rain and thunder darkness
- moon phase brightness
- dusk, full-night, and dawn timing
- minimum brightness floors
- open-sky starlight floor
- block light preservation
- low-light desaturation
- low-light cool tint
- full-moon warmth
- Nether and End lighting (warm Nether ambient, cool End ambient)
- atmospheric fog color (blends into the sky, night darkening, weather desaturation)
- optional auto-disable while a shader pack is active

## What it does not change

- day length
- mob spawning
- server time
- block light propagation
- sky rendering
- fog geometry or render distance (only fog color, for now)
- dynamic lights
- blocks, items, mobs, or dimensions

---

## How it works

Selas post-processes Minecraft's vanilla lightmap after the game builds it. In practice, that means it can:

- darken natural sky light more aggressively than vanilla
- keep caves and sealed spaces much darker than open-sky night surfaces
- preserve useful block light from torches, lava, and other emissive sources
- react to moon phase, rain, thunder, and twilight timing
- apply separate ambient handling for the Nether, the End, and other skyless dimensions
- add small low-light color changes without replacing the entire renderer

This is still a vanilla-lightmap approach, not a custom shader pack or dynamic-light engine.

---

## Configuration

Selas uses a client config file:

```text
config/selas-client.toml
```

The config is organized under these sections:

```text
natural_darkness.general
natural_darkness.dimensions
natural_darkness.dimension_lighting
natural_darkness.twilight
natural_darkness.natural_light
natural_darkness.darkness
natural_darkness.color
atmosphere.general
atmosphere.dimensions
atmosphere.fog_color
```

### Section overview

- `natural_darkness.general`  
  Main switches and compatibility options, including enable/disable, smooth lightmap updates, Night Vision handling, lightning handling, and shader-pack auto-disable.

- `natural_darkness.dimensions`  
  Controls where Selas is active: Overworld, custom skylit dimensions, and skyless dimensions.

- `natural_darkness.dimension_lighting`  
  Separate ambient brightness and tint behavior for the Nether and the End.

- `natural_darkness.twilight`  
  Dusk, full-night, and dawn timing in Minecraft day ticks.

- `natural_darkness.natural_light`  
  Moon and weather response: new moon brightness, full moon brightness, moon phase curve, rain darkening, and thunder darkening.

- `natural_darkness.darkness`  
  Brightness floors, darkness curve, starlight floor, and block-light preservation.

- `natural_darkness.color`  
  Low-light desaturation, cool tint, and full-moon warmth behavior.

- `atmosphere.general`  
  Main switches for the atmospheric fog module, including enable/disable and shader-pack auto-disable.

- `atmosphere.dimensions`  
  Controls where fog changes are active: Overworld, Nether, and End.

- `atmosphere.fog_color`  
  Fog color response: blending the horizon into the sky color, night darkening, weather desaturation, and color smoothing.

Selas does **not** use presets or profiles. Values are changed directly in the config.

Selas keeps your existing config values when you update the mod. If you want to apply the current default settings, delete `config/selas-client.toml` once and let the game regenerate it.

---

## Compatibility notes

Selas is a visual client-side mod. It can be used on servers because it does not change server logic, world time, spawning rules, or block light propagation.

Shader packs usually replace or override vanilla lightmap lighting. Because of that, Selas disables itself by default while an Iris/Oculus shader pack is active (`disable_with_shaders`). You can force it back on in the config if you want to test behavior, but most shader packs will ignore or override the result.

Minecraft's vanilla lightmap is small and only uses 16 raw light levels. That means visible grain or shimmer during dusk and dawn is still possible in vanilla, and Selas can make it easier to notice because darker scenes make the artifact more visible.

---

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.235 or newer
- Java 21

## Building

```bash
./gradlew build
```
