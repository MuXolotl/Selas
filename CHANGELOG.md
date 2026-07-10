# Changelog

## 0.1.1

### Added

- Configurable moon phase response curve for tuning brightness between new and full moon.

### Changed

- Increased natural sky brightness during lunar phases, especially during full moon and intermediate phases.

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
