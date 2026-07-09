# Selas

Selas is a client-side NeoForge mod for Minecraft 1.21.1.

The current version changes the vanilla lightmap to make night, caves, rain and thunder darker. It does not add blocks, items, mobs, dimensions or server-side mechanics.

## Current scope

Selas v0.1 is focused on one feature: configurable darkness.

It changes:

- how dark the world becomes after sunset;
- when the transition into full night starts and ends;
- how much moon phase affects night brightness;
- how much rain and thunder reduce sky light;
- how low the minimum visible brightness can go;
- how strongly block light sources are preserved;
- how much color is desaturated or cooled in low light.

It does not currently change:

- the actual Minecraft day length;
- mob spawning rules;
- server time;
- block light propagation;
- sky rendering;
- fog rendering;
- dynamic lights.

## Configuration

Selas does not provide built-in presets. The default values are the intended baseline, and each part of the lighting behavior is configured directly.

The generated client config is:

```text
config/selas-client.toml
```

See [`docs/configuration.md`](docs/configuration.md) for the current config layout and test commands.

## Known visual behavior

Minecraft lighting uses a small lightmap and only 16 raw light levels. During dusk and dawn, some block textures can show visible grain or shimmer. This also exists in vanilla, but a darker night can make it easier to see.

Selas currently keeps this behavior instead of hiding it with post-processing or texture overlays.

## Requirements

- Minecraft: 1.21.1
- Loader: NeoForge
- Java: 21
- License: MIT

## Development

Open the project in IntelliJ IDEA Community Edition and import it as a Gradle project.

Useful Gradle tasks:

```bash
./gradlew runClient
./gradlew build
```
