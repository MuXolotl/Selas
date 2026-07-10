# Changelog

## 0.1.1

### Fixed

- Rain and thunder now darken the natural sky during daytime as well as at night.
- Rain and thunder transitions now use the current render partial tick.
- Rain and thunder no longer stack their full penalties, keeping storms visibly brighter than night.
- Invalid twilight tick ordering now falls back to the default day/night schedule with a warning.

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
