# Changelog

## 0.1.2

### Added

- Open-sky starlight luminance floor (`starlight_luminance_floor`).
- Auto-disable while an Iris/Oculus shader pack is active (`disable_with_shaders`, enabled by default).

### Changed

- Recalibrated default natural darkness toward more physically grounded lighting:
  - higher new-moon and full-moon sky factors
  - clearer separation between near-black cave darkness and open-sky starlight
  - cave floor raised slightly to reduce black crush without adding visible ambient light
  - slightly softer darkness curve and night color grading
  - slightly brighter Nether / End ambient defaults
- When `respect_gamma` is enabled, the brightness slider now lifts Selas target luminance only.
- Nether, End, and other skyless dimensions use ambient base light only.

### Fixed

- Double gamma application when `respect_gamma` was enabled.
- Redundant skyless floor calculation.
- Iris shader-pack status checks are now cached and more defensive.
- Nether / End dimension routing now uses value equality for dimension keys.

## 0.1.1

### Added

- Configurable moon phase response curve for tuning brightness between new and full moon.
- Separate Nether and End lighting so they are no longer pitch black: a warm Nether ambient and a cool End starlight tint, both configurable.
- Optional brightness-slider (gamma) handling so the slider can still brighten the world while Selas is active.

### Changed

- Increased natural sky brightness during lunar phases, especially during full moon and intermediate phases.
- Improved sky and block light composition.
- Nether and End no longer use the generic skyless profile.
- Slightly raised the default minimum brightness so nights read as moonlit rather than cave-dark.

### Fixed

- Rain and thunder now darken the natural sky during daytime.
- Weather transitions now use the current render partial tick.
- Rain and thunder darkening no longer stacks.
- Invalid twilight schedules now fall back to the default values.

## 0.1.0

Initial client-side darkness module.

### Added

- Lightmap-based night and cave darkening.
- Configurable dusk, full-night and dawn timings.
- Moon phase brightness settings.
- Rain and thunder darkening settings.
- Dimension toggles for Overworld, custom skylit dimensions and skyless dimensions.
- Minimum brightness floor settings.
- Block light preservation setting.
- Low-light desaturation and cool tint settings.
- Night Vision, Conduit Power vision and lightning flash compatibility toggles.
- English and Russian config translations.
- Grouped client config layout.
