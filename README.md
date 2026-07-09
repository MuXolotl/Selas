# Selas

Selas is a small client-side NeoForge mod for Minecraft 1.21.1.

It makes nights, caves, rain, and thunder darker by changing Minecraft's vanilla lightmap. It does not add blocks, items, mobs, dimensions, dynamic lights, fog, or server-side time changes.

## Downloads

- GitHub Releases: https://github.com/MuXolotl/Selas/releases/tag/v0.1
- Modrinth: https://modrinth.com/mod/selas
- CurseForge: https://www.curseforge.com/minecraft/mc-mods/selas

## What it changes

- night brightness
- cave darkness
- rain and thunder darkness
- moon phase brightness
- dusk and dawn transition timing
- minimum brightness floor
- block light preservation
- low-light desaturation
- low-light cool tint

## Configuration

Selas uses a client config file:

```text
config/selas-client.toml
```

The config is split into sections:

```text
natural_darkness.general
natural_darkness.dimensions
natural_darkness.twilight
natural_darkness.natural_light
natural_darkness.darkness
natural_darkness.color
```

Selas does not use built-in presets. Change the values directly if the defaults do not fit your setup.

## Notes

Minecraft uses a small lightmap and 16 raw light levels. Some texture grain or shimmer during dusk and dawn is visible in vanilla too. Selas can make it easier to notice because the world is darker.

Shader packs may change or override the final lighting result.

## Requirements

- Minecraft 1.21.1
- NeoForge
- Java 21

## Building

```bash
./gradlew build
```
