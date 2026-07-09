# Selas lighting design

Selas does not ship built-in presets. The default configuration should be opinionated, harsh, and realistic enough to define the mod's identity, but every behavior must remain adjustable through explicit config values.

## v0.1 direction

- Client-side natural darkness through the vanilla lightmap.
- No full-screen black overlay.
- No forced gamma changes.
- Strong block lights should remain useful.
- Moon phase and weather should matter.
- Dusk and dawn should use a continuous, configurable twilight curve instead of a delayed on/off night gate.
- Per-frame lightmap updates are useful for smoother transitions.
- Night Vision, useful underwater Conduit vision, and lightning flashes should remain readable.

## Later modules

- Eye adaptation / exposure.
- Fog and atmospheric density.
- Color temperature by time of day and weather.
- Optional server-authoritative time-cycle module.
- Optional dynamic-light integration or native dynamic lights.
