<div align="center">

![Selas-banner](https://raw.githubusercontent.com/MuXolotl/Selas/main/assets/image/banner.png)

# ☀️ Selas 🌑

Selas is a small client-side NeoForge mod for Minecraft 1.21.1.

It makes nights, caves, rain, and thunder darker by changing Minecraft's vanilla lightmap. It aims for physically grounded darkness: sealed unlit spaces approach black, while open nights retain moonlight, starlight, and atmospheric glow. It does not add blocks, items, mobs, dimensions, dynamic lights, fog, or server-side time changes.

## Downloads

[![CurseForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/selas) [![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/selas) [![GitHub Releases](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/MuXolotl/Selas/releases/latest)

</div>

---

## What it changes

- night brightness
- cave darkness
- rain and thunder darkness
- moon phase brightness
- dusk and dawn transition timing
- minimum brightness floor
- open-sky starlight floor
- block light preservation
- low-light desaturation
- low-light cool tint
- Nether and End lighting (warm Nether ambient, cool End starlight)
- optional brightness-slider (gamma) handling
- optional auto-disable while a shader pack is active

---

## Configuration

Selas uses a client config file:

```text
config/selas-client.toml
```

The config is split into sections:

```text
natural_darkness.general
natural_darkness.dimensions
natural_darkness.dimension_lighting
natural_darkness.twilight
natural_darkness.natural_light
natural_darkness.darkness
natural_darkness.color
```

Selas does **not** ship presets or profiles. Change the values directly if the defaults do not fit your setup.

When updating from an earlier 0.1.x release, existing config values are preserved. To use the recalibrated 0.1.2 defaults, delete `config/selas-client.toml` once before starting the game and let Selas regenerate it.

> [!Note]
>
> Minecraft uses a small lightmap and 16 raw light levels. Some texture grain or shimmer during dusk and dawn is visible in vanilla too. Selas can make it easier to notice because the world is darker.
>
> Shader packs usually replace vanilla lightmap lighting. By default Selas disables itself while an Iris/Oculus shader pack is active (`disable_with_shaders`). You can force Selas back on in the config if you are testing, but most packs will ignore or override the result.
>
> The brightness slider (`respect_gamma`) works like this:
> - **off (default):** Selas keeps its own darkness targets, so the slider cannot wash out nights and caves. Vanilla still builds the lightmap with gamma first, so in already-bright areas (daylight, strong block light) you may still notice a small slider effect. That residual is normal for lightmap post-processing, not a broken toggle.
> - **on:** the slider is allowed to lift Selas target brightness in a controlled way (without double-applying gamma to RGB).

---

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.235 or newer
- Java 21

## Building

```bash
./gradlew build
```
