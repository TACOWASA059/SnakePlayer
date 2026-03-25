# Snake Player MOD

- Minecraft 1.20.1
- Forge / Fabric


![image](https://github.com/user-attachments/assets/cc4da5a4-3b42-460c-a41b-5a5f1f0b3c25)

Snake Player is a mod that turns the player into a snake-like character with a head and multiple body segments.
It supports configurable movement, damage, body size, and spawn spreading behavior.

## Features

- Snake-style player body rendering and behavior
- Configurable body segment growth
- Adjustable size, speed, and damage values
- Optional spawn spreading
- Command and config-driven parameter control

## Config

The config file is generated at `config/snakeplayer.toml`.
If an old `snakeplayer.properties` file exists, its values are migrated when `snakeplayer.toml` does not exist yet.

| Key | Default | Type | Description |
|-----|-----|-----|-----|
| `enable_spread` | `false` | boolean | Enables spread-out player spawning |
| `cx` | `0.0` | double | Center X for spawn spreading |
| `cz` | `0.0` | double | Center Z for spawn spreading |
| `L` | `50.0` | double | Side length of the spawn spread area |
| `r` | `5.0` | double | Minimum distance between players |
| `expValue` | `10` | int | Experience required per segment increase |
| `default_is_snake` | `true` | boolean | Enables snake mode by default |
| `default_head_size` | `1.0` | double | Default head size |
| `default_body_segment_size` | `1.0` | double | Default body segment size |
| `default_damage` | `1000.0` | double | Damage dealt by segment contact |
| `default_speed` | `0.3` | double | Automatic movement speed |
| `spawn_block_view_distance` | `8` | int | Search distance for spawn candidates |
| `spawn_block_view_half_width` | `2` | int | Half-width used when scanning spawn candidates |

## Commands

### Set config

```mcfunction
/snake config set <key> <value>
```

### Get config

```mcfunction
/snake config get <key>
```

### Set player parameters

```mcfunction
/snake <targets> <dataparameter_key> <value>
```

- `targets`: selectors such as `@s`, `@p`, and `@a`
- `dataparameter_key`: available keys
  - `isSnake`
  - `headSize`
  - `bodySegmentSize`
  - `damage`
  - `speed`

![image](https://github.com/user-attachments/assets/811860e8-cffe-4b3f-b020-d5b2beb733ed)
