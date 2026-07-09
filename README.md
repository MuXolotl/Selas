# Selas

Selas is a NeoForge mod by MuXolotl focused on realistic, configurable lighting and time-cycle systems for Minecraft.

The first development target is **natural darkness**: harsher, more believable night and cave visibility without turning the game into a flat black screen.

## Current scope

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

## Design notes

Selas intentionally avoids built-in presets. All visual behavior should be controlled through explicit, flexible configuration values.
