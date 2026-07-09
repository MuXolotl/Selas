# Selas lighting design

Selas does not use built-in presets. The default values are only the shipped baseline. Users should be able to change individual values directly instead of choosing a named preset.

## v0.1 scope

- Client-side lightmap changes only.
- No full-screen overlay.
- No forced gamma changes.
- No server time changes.
- No block light propagation changes.
- No dynamic lights.
- No fog changes.

## Current behavior

- The mod changes the 16x16 vanilla lightmap before it is uploaded.
- The day/night transition is controlled by four configurable day ticks.
- Moon brightness is taken from the vanilla moon phase value.
- Rain and thunder lower the sky light contribution.
- Block light can be preserved so placed light sources remain useful.
- Low light can desaturate colors and add a small cool tint.
- Night Vision, useful Conduit Power vision and lightning flashes can bypass the effect.

## Known limitation

Minecraft uses a small lightmap and 16 raw light levels. Some texture grain or shimmer during dusk and dawn is also visible in vanilla. A darker lightmap makes it easier to notice.

## Later modules

Possible later work, not part of v0.1:

- Eye adaptation / exposure.
- Fog and atmosphere controls.
- Color temperature by time of day and weather.
- Optional server-controlled day length.
- Dynamic light support or integration.
