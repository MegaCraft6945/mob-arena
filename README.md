# Legion Mob Arena Plugin

A comprehensive Minecraft Mob Arena plugin inspired by Mineplex Mob Arena with extensive customization and features.

## Features

### Game Mechanics
- **50 Round System**: Players battle through up to 50 rounds of increasingly difficult mobs
- **Dynamic Mob Spawning**: Mobs spawn in waves with difficulty scaling based on round number
- **Multiplayer Support**: 1-8 players can join each arena
- **Round Progression**: Kill count and gem rewards increase each round

### Kit System
The plugin includes 9 unique kits with special abilities:

1. **Warrior** (Free) - Stone sword, leather armor
   - Ability: Brute Rush (Strength II for 5 seconds)

2. **Archer** (100 gems) - Bow with Power I, 32 arrows
   - Ability: Arrow Storm (Shoot 5 arrows at once)

3. **Tank** (200 gems) - Chainmail armor
   - Ability: Iron Skin (Resistance II for 8 seconds)

4. **Ninja** (300 gems) - Leather armor
   - Ability: Shadow Step (Speed II + Invisibility)

5. **Brute** (400 gems) - Iron sword
   - Ability: Ground Slam (Knock back nearby mobs)

6. **Mage** (500 gems) - Stone sword
   - Ability: Lightning Strike (Strike lightning at target)

7. **Knight** (600 gems) - Iron armor with shield
   - Ability: Shield Wall (4 absorption hearts)

8. **Berserker** (800 gems) - Diamond sword
   - Ability: Rage (Strength III for 10 seconds)

9. **Engineer** (700 gems) - Stone sword, bow
   - Ability: Turret (Place defensive turrets)

### Upgrade Shop
After each round, players can spend gold nuggets (earned from killing mobs) to upgrade:

- **Armor**: Leather → Chainmail → Iron → Diamond
  - Helmet (6 gold base, increases each upgrade)
  - Chestplate (10 gold base)
  - Leggings (8 gold base)
  - Boots (6 gold base)

- **Weapons**:
  - Sword: Stone → Iron → Diamond (15 gold base)
  - Bow: Power I → Power V (10 gold per level)

- **Consumables**:
  - 16 Arrows (5 gold)
  - Cake (40 gold)
  - Golden Apple (10 gold)

### Economy System
- **Gold Nuggets**: Dropped by killed mobs, used for upgrade shop
- **Gems**: Earned after completing rounds, used to unlock kits
- **MySQL Integration**: Player data, gems, and unlocked kits are saved to MySQL database

### Arena Management
Admins can:
- Create multiple arenas with custom boundaries
- Set lobby spawn points for each arena
- Enable/disable arenas
- Delete arenas
- Full region protection (no block breaking/placing except cakes)

### Protection Features
- Arena blocks are protected from breaking (unless admin)
- Only game mobs can damage players
- No natural mob spawning in arenas
- PvP disabled in arenas
- Automatic cleanup after each game

### User Interface
- **Main Menu**: Arena selection GUI
- **Kit Selection**: Visual kit selection with unlock status
- **Upgrade Shop**: Interactive shop with real-time gold display
- **Admin Menu**: Complete arena management interface
- **Action Bar**: Live round info (Round #, Gems reward, Mobs remaining)

## Commands

### Player Commands
- `/legion` - Open arena selection menu
- `/legion join [arena]` - Join an arena
- `/legion leave` - Leave current game
- `/legion stats` - View your statistics

### Admin Commands
- `/legionadmin` - Open admin menu
- `/legionadmin create <name>` - Start creating an arena
- `/legionadmin setpos1` - Set first corner of arena
- `/legionadmin setpos2` - Set second corner of arena
- `/legionadmin setlobby` - Set lobby spawn point
- `/legionadmin finish` - Complete arena setup
- `/legionadmin manage` - Manage existing arenas
- `/legionadmin delete <name>` - Delete an arena
- `/legionadmin reload` - Reload configuration

## Permissions
- `legion.play` - Allows players to join arenas (default: true)
- `legion.admin` - Allows access to admin commands (default: op)

## Configuration

The plugin includes a comprehensive config.yml with settings for:
- Database connection (MySQL)
- Game settings (max rounds, player counts, countdown)
- Round progression formulas
- Economy values
- Shop prices
- Spawn locations
- Custom messages

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Configure MySQL database in `config.yml`
4. Restart your server
5. Create arenas using `/legionadmin create <name>`
6. Players can join using `/legion`

## Database Setup

The plugin automatically creates the following tables:
- `player_data` - Player statistics and gems
- `player_kits` - Unlocked kits for each player
- `arenas` - Arena configurations

Make sure to configure your MySQL credentials in config.yml:

```yaml
database:
  enabled: true
  host: localhost
  port: 3306
  database: legion_mobarena
  username: root
  password: password
```

## Leaderboard System

The plugin includes a built-in leaderboard system that tracks player statistics:
- Highest round reached
- Total games played
- Total kills
- Total gems earned

### Live Stats API

The plugin includes a **built-in REST API server** for displaying live leaderboard stats on any website!

✅ **Real-time stats** - Always up-to-date from your database
✅ **No external hosting** - API runs directly in the plugin
✅ **100% free** - No third-party services
✅ **Simple setup** - Enable in config and open a port
✅ **GitHub Pages ready** - Host the leaderboard website for free

See **[LIVE_STATS_API_SETUP.md](LIVE_STATS_API_SETUP.md)** for complete setup instructions!

**Quick Start:**
1. Enable API in `config.yml`
2. Open port 25566 (or configure a different port)
3. Enable GitHub Pages from the `/docs` folder
4. Your live leaderboard website is ready at `https://YOUR-USERNAME.github.io/mob-arena/`

**API Endpoints:**
- `GET /api/stats` - Server summary
- `GET /api/leaderboard` - Full leaderboard
- `GET /api/player/{uuid}` - Player stats

### GitHub Pages Leaderboard

This repository includes a ready-to-deploy leaderboard website in the `/docs` folder!

**To enable:**
1. Go to repository Settings → Pages
2. Source: "Deploy from a branch"
3. Branch: Select your branch, Folder: `/docs`
4. Click Save

Your leaderboard will be live at `https://YOUR-USERNAME.github.io/mob-arena/` in 2-3 minutes!

See **[docs/README.md](docs/README.md)** for detailed setup instructions.

### In-Game Stats

Players can view their stats anytime with:
```
/legion stats
```

## Building from Source

Requirements:
- Java 17+
- Maven 3.x

```bash
git clone https://github.com/MegaCraft6945/mob-arena.git
cd mob-arena
mvn clean package
```

The compiled JAR will be in the `target/` directory.

## API

Other plugins can interact with Legion Mob Arena through the main plugin instance:

```java
LegionMobArena plugin = (LegionMobArena) Bukkit.getPluginManager().getPlugin("LegionMobArena");

// Get managers
GameManager gameManager = plugin.getGameManager();
ArenaManager arenaManager = plugin.getArenaManager();
KitManager kitManager = plugin.getKitManager();
PlayerDataManager playerDataManager = plugin.getPlayerDataManager();

// Check if player is in game
boolean inGame = gameManager.isPlayerInGame(player.getUniqueId());

// Get player stats
PlayerData data = playerDataManager.getPlayerData(player.getUniqueId());
int gems = data.getGems();
```

## Support

For issues, feature requests, or contributions, please visit:
https://github.com/MegaCraft6945/mob-arena/issues

## License

This plugin is open source and available for use and modification.

---

**Version**: 1.0.0
**Minecraft Version**: 1.20.1+
**Server Software**: Spigot/Paper
