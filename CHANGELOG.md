# Changelog

All notable changes to Selas are documented here. Newest version first.

Each release is split into two parts:

- **Player-facing changes** come first — what actually changed in-game and what you will see or feel.
- A collapsible **Technical / internal** section follows — code-level changes that do not affect gameplay.

---

## 0.2.0

### Added

- Distant fog now blends into the sky color, so the horizon melts into the sky instead of looking like a separate band, adding a real sense of atmospheric depth (sunsets, blue daytime haze, and so on).
- Atmospheric fog reacts to the time of day and weather: nights pull the fog toward a darker blue-gray, and rain and thunder desaturate it toward a muted gray. Works in the Overworld and can be tuned or turned off in the new Atmosphere config section.

## 0.1.3

### Added

- Full-moon nights now carry a faint, warm glow, so a full moon feels a little warmer than the plain cool starlight of a new moon. Adjustable, and can be turned off.

### Changed

- Rebalanced the default night look for stronger realism:
  - moonless nights are a touch darker while full-moon nights are brighter,
  - open skies keep a clearer starlight glow, and caves sit a little deeper in the dark,
  - night now reads as a cooler blue-gray rather than just losing its reds.
- Night colors now fade toward gray only deep in the night, so dusk and dawn keep more of their color.
- Nights hold their full darkness slightly longer before dawn begins.
- Removed the brightness-slider (gamma) option. It was off by default and worked inconsistently; Selas now always keeps its own darkness targets.

### Performance

- Reduced per-frame overhead by reading configuration values once per frame instead of once per lightmap pixel.

## 0.1.2

### Added

- Open night skies now keep a faint starlight glow instead of fading to pure black, so surface nights read as moonlit rather than blind-dark.
- Selas now steps aside automatically while a shader pack is active, so it no longer fights your shaders. (You can force it back on in the config.)

### Changed

- Rebalanced the default night look toward something more natural:
  - brighter, more believable moonlit and full-moon nights,
  - a clearer difference between near-black caves and open, starlit surfaces,
  - a gentler fade into darkness and softer night colors.
- The Nether and End feel a little less flat, with slightly brighter ambient light so they are no longer pitch black.
- When "let the brightness slider brighten" is enabled, the slider now lifts Selas' own brightness cleanly instead of stacking on top of the world.

### Fixed

- Fixed the brightness slider being able to wash out nights and caves twice when the gamma option was enabled.

<details><summary>Technical / internal</summary>

- Removed a redundant brightness-floor calculation for skyless dimensions.
- Nether/End lighting is now routed by dimension-key value equality, fixing cases where their dedicated profile could be missed.
- Skyless dimensions (Nether, End, and modded skyless worlds) now use ambient base light only.

</details>

## 0.1.1

### Added

- The moon phase now has a tunable response curve, so brightness between new moon and full moon can be adjusted to taste.
- The Nether and End are no longer pitch black: the Nether gets a warm ambient glow and the End a cool one, both adjustable.
- Optional brightness-slider handling, so the slider can still brighten the world while Selas is active.

### Changed

- Nights are brighter and more readable during lunar phases, especially around the full moon and the phases in between.
- Improved how sky light and block light blend together.
- Nights now read as moonlit rather than cave-dark by default.

### Fixed

- Rain and thunder now correctly darken the sky during the daytime, not just at night.
- Rain and thunder darkening no longer stack, so storms are no longer overly dark.
- Weather brightness transitions are smoother.

<details><summary>Technical / internal</summary>

- The Nether and End no longer fall back to the generic skyless lighting profile.
- Weather calculations now use the current render partial tick.
- Invalid twilight tick schedules fall back to the default values, with a single warning logged.

</details>

## 0.1.0

Initial release: a client-side, natural darkness module for the vanilla lightmap.

### Added

- Darker, more natural nights and caves.
- Configurable dusk, full-night, and dawn timing.
- Moon phase brightness.
- Rain and thunder darkening.
- Per-dimension toggles for the Overworld, custom skylit dimensions, and skyless dimensions.
- Minimum brightness floors so scenes never crush to unreadable black.
- A block-light preservation setting, so torches and lava stay usefully bright.
- Low-light desaturation and a cool tint for a more natural night mood.
- Compatibility toggles for Night Vision, Conduit Power vision, and lightning flashes.
- English and Russian translations for the config.

<details><summary>Technical / internal</summary>

- Lightmap post-processing via a single client mixin on `LightTexture`.
- Grouped client config layout.

</details>
