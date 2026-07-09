# Selas configuration

Selas v0.1 is configured through the client config file:

```text
config/selas-client.toml
```

The mod has no built-in presets. The default values are the baseline, and each value controls one part of the lightmap calculation.

If the config layout changes during development, delete the old `selas-client.toml` and let NeoForge generate it again.

## `natural_darkness.general`

General switches.

| Option | Default | What it does |
| --- | ---: | --- |
| `enabled` | `true` | Turns the Selas lightmap changes on or off. |
| `smooth_lightmap_updates` | `true` | Updates the lightmap every rendered frame while Selas is active. This makes day/night transitions smoother. |
| `respect_night_vision` | `true` | Stops Selas from darkening the view while Night Vision is active. Also covers useful underwater Conduit Power vision. |
| `respect_lightning_flashes` | `true` | Stops Selas from darkening lightning flashes. |

## `natural_darkness.dimensions`

Where the module is active.

| Option | Default | What it does |
| --- | ---: | --- |
| `affect_overworld` | `true` | Applies the effect in the Overworld. |
| `affect_custom_sky_dimensions` | `true` | Applies the effect in custom dimensions that have skylight. |
| `affect_skyless_dimensions` | `true` | Applies the effect in dimensions without skylight. |
| `skyless_dimension_light_factor` | `0.09` | Brightness factor used where there is no sun or moon. Lower values make skyless dimensions darker. |

## `natural_darkness.twilight`

Day/night transition timing. Values are Minecraft day ticks. One full Minecraft day is `24000` ticks.

| Option | Default | What it does |
| --- | ---: | --- |
| `dusk_transition_start_tick` | `11800` | Selas starts fading from day brightness into night brightness. |
| `full_night_start_tick` | `14000` | Full Selas night begins. |
| `full_night_end_tick` | `21200` | Full Selas night starts fading into dawn. |
| `dawn_transition_end_tick` | `400` | Dawn fade ends after midnight wraps back to day tick `0`. |

Useful time commands:

```mcfunction
/time set 11800
/time set 14000
/time set 18000
/time set 21200
/time set 23000
/time set 0
/time set 400
```

## `natural_darkness.natural_light`

Moon and weather values.

| Option | Default | What it does |
| --- | ---: | --- |
| `moonless_night_sky_factor` | `0.018` | Sky brightness at midnight during a new moon. Lower values make moonless nights darker. |
| `full_moon_sky_factor` | `0.105` | Sky brightness at midnight during a full moon. Lower values make full-moon nights darker. |
| `rain_darkening` | `0.16` | Extra darkening when rain is at full strength. |
| `thunder_darkening` | `0.32` | Extra darkening when thunder is at full strength. |

Moon phase test commands. The moon phase depends on the day number, so these commands set both the day and the time of night:

```mcfunction
# Full moon, day 0
/time set 18000

# Waning gibbous, day 1
/time set 42000

# Third quarter, day 2
/time set 66000

# Waning crescent, day 3
/time set 90000

# New moon, day 4
/time set 114000

# Waxing crescent, day 5
/time set 138000

# First quarter, day 6
/time set 162000

# Waxing gibbous, day 7
/time set 186000
```

## `natural_darkness.darkness`

Brightness curve and minimum brightness.

| Option | Default | What it does |
| --- | ---: | --- |
| `minimum_luminance_floor` | `0.006` | Lowest general brightness Selas allows. `0` allows true black. |
| `cave_luminance_floor` | `0.003` | Lowest brightness used when both block light and sky light are low. |
| `darkness_curve` | `1.72` | Controls how quickly low light falls into darkness. Higher values make low light darker. |
| `block_light_preservation` | `1.0` | Controls how much block light resists darkening. Higher values keep torches and lava brighter. |

## `natural_darkness.color`

Color changes in low light.

| Option | Default | What it does |
| --- | ---: | --- |
| `night_desaturation` | `0.34` | Removes color in darker areas. `0` disables this. |
| `night_cool_tint` | `0.055` | Adds a small blue/cold tint in low natural light. `0` disables this. |
