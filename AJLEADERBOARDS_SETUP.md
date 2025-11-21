# ajLeaderboards Integration Guide

This guide explains how to set up ajLeaderboards to display Legion Mob Arena statistics.

## Prerequisites

1. **PlaceholderAPI** - Required dependency
2. **ajLeaderboards** - Install from SpigotMC
3. **Legion Mob Arena** - This plugin

## Available Placeholders

### Player Statistics
These placeholders show individual player stats:

| Placeholder | Description |
|------------|-------------|
| `%legion_gems%` | Player's current gems |
| `%legion_highest_round%` | Player's best round |
| `%legion_kills%` | Total kills |
| `%legion_deaths%` | Total deaths |
| `%legion_kdr%` | Kill/Death ratio |
| `%legion_games_played%` | Total games played |
| `%legion_wins%` | Games won |
| `%legion_winrate%` | Win rate percentage |
| `%legion_unlocked_kits%` | Number of kits unlocked |

### Player Rankings
These show where a player ranks globally:

| Placeholder | Description |
|------------|-------------|
| `%legion_rank_kills%` | Player's rank by kills |
| `%legion_rank_rounds%` | Player's rank by highest round |
| `%legion_rank_games%` | Player's rank by games played |
| `%legion_rank_wins%` | Player's rank by wins |

### Leaderboard Placeholders
Format: `%legion_top_<stat>_<position>_<value>%`

**Stats available:** `kills`, `rounds`, `games`, `wins`, `deaths`, `kdr`, `winrate`, `gems`

**Values:** `name` (player name) or `value` (stat number)

#### Examples:
- `%legion_top_rounds_1_name%` - Name of #1 player by rounds
- `%legion_top_rounds_1_value%` - Highest round value
- `%legion_top_kills_5_name%` - Name of #5 player by kills
- `%legion_top_kdr_3_value%` - K/D ratio of #3 player

## Example ajLeaderboards Configuration

Create leaderboards in ajLeaderboards config:

### 1. Top Players by Highest Round

```yaml
boards:
  mobarena-rounds:
    type: 'PLACEHOLDER'
    order: 'DESCENDING'
    size: 10
    refresh-interval: 60
    placeholder: '%legion_top_rounds_{pos}_name%'
    value-placeholder: '%legion_top_rounds_{pos}_value%'
    display-name: '&6&lTop Players - Highest Round'
    entry-format:
      - '&7{pos}. &e{name}'
      - '&7Round: &a{value}'
```

### 2. Top Players by Kills

```yaml
  mobarena-kills:
    type: 'PLACEHOLDER'
    order: 'DESCENDING'
    size: 10
    refresh-interval: 60
    placeholder: '%legion_top_kills_{pos}_name%'
    value-placeholder: '%legion_top_kills_{pos}_value%'
    display-name: '&6&lTop Players - Most Kills'
    entry-format:
      - '&7{pos}. &e{name}'
      - '&7Kills: &c{value}'
```

### 3. Top Players by Win Rate

```yaml
  mobarena-winrate:
    type: 'PLACEHOLDER'
    order: 'DESCENDING'
    size: 10
    refresh-interval: 60
    placeholder: '%legion_top_winrate_{pos}_name%'
    value-placeholder: '%legion_top_winrate_{pos}_value%'
    display-name: '&6&lTop Players - Win Rate'
    entry-format:
      - '&7{pos}. &e{name}'
      - '&7Win Rate: &a{value}%'
```

### 4. Top Players by K/D Ratio

```yaml
  mobarena-kdr:
    type: 'PLACEHOLDER'
    order: 'DESCENDING'
    size: 10
    refresh-interval: 60
    placeholder: '%legion_top_kdr_{pos}_name%'
    value-placeholder: '%legion_top_kdr_{pos}_value%'
    display-name: '&6&lTop Players - K/D Ratio'
    entry-format:
      - '&7{pos}. &e{name}'
      - '&7K/D: &b{value}'
```

### 5. Top Players by Games Won

```yaml
  mobarena-wins:
    type: 'PLACEHOLDER'
    order: 'DESCENDING'
    size: 10
    refresh-interval: 60
    placeholder: '%legion_top_wins_{pos}_name%'
    value-placeholder: '%legion_top_wins_{pos}_value%'
    display-name: '&6&lTop Players - Most Wins'
    entry-format:
      - '&7{pos}. &e{name}'
      - '&7Wins: &6{value}'
```

## In-Game Commands

### Creating Hologram Leaderboards

```
/ajlb holo create mobarena-rounds <location>
/ajlb holo create mobarena-kills <location>
/ajlb holo create mobarena-winrate <location>
/ajlb holo create mobarena-kdr <location>
/ajlb holo create mobarena-wins <location>
```

### Creating Sign Leaderboards

```
/ajlb sign create mobarena-rounds
```

Then place a sign with:
```
[ajlb]
mobarena-rounds
{pos}
```

## Tips

1. **Refresh Interval**: Set to 60 seconds for good balance between performance and freshness
2. **Size**: Top 10 works well for most displays
3. **Multiple Boards**: Create different boards for different stats to give players various goals
4. **Hologram Placement**: Place near spawn or arena entrance for visibility

## Troubleshooting

**Placeholders show as text:**
- Ensure PlaceholderAPI is installed
- Run `/papi register legion` to register placeholders
- Verify Legion Mob Arena is enabled

**Empty leaderboards:**
- Players need to play games first to generate stats
- Check that MySQL is connected (stats are saved to database)

**Not updating:**
- Increase refresh-interval if needed
- Check ajLeaderboards config for errors

## Advanced Configuration

### Custom Entry Format with Multiple Lines

```yaml
entry-format:
  - '&7━━━━━━━━━━━━━━━━━━━━'
  - '  &7#{pos} &e{name}'
  - '  &7Round &a{value} &7| Kills &c%legion_top_rounds_{pos}_kills%'
  - '  &7K/D &b%legion_top_rounds_{pos}_kdr% &7| Wins &6%legion_top_rounds_{pos}_wins%'
```

### Multi-Stat Display

You can combine different placeholders in the entry format to show multiple stats for each player.

## Support

For issues with:
- **Legion Mob Arena**: Check plugin documentation
- **ajLeaderboards**: Visit ajLeaderboards SpigotMC page
- **PlaceholderAPI**: Ensure latest version installed
