# Live Stats API Setup Guide

This plugin includes a built-in REST API server that allows you to display live leaderboard stats on any website!

## Features

- ✅ **Real-time stats** - Always up-to-date from your MySQL database
- ✅ **No external hosting needed** - API runs directly in the plugin
- ✅ **100% free** - No third-party services required
- ✅ **Simple setup** - Just enable in config and open a port
- ✅ **Works with any website** - HTML page included, host anywhere

---

## Quick Setup (5 minutes)

### Step 1: Enable the API

1. Open `plugins/LegionMobArena/config.yml`
2. Find the `api` section at the bottom:
   ```yaml
   api:
     enabled: false  # Change this to true
     port: 8080  # Port for the HTTP server
     server-name: "My Legion Mob Arena Server"  # Your server name
   ```
3. Change `enabled: false` to `enabled: true`
4. Customize `server-name` to your server's name
5. Save and restart your server

### Step 2: Open the Port

The API needs port 8080 (or whatever you configured) to be accessible:

**For Local Servers:**
- Open port 8080 in your router/firewall
- Forward port 8080 to your server's local IP

**For Pterodactyl/Hosting:**
- Go to your server's Network settings
- Add allocation for port 8080
- Set the port in config.yml to match the allocated port

**Test it works:**
Visit `http://YOUR-SERVER-IP:8080/api/stats` in a browser. You should see JSON data!

### Step 3: Set Up the Website

1. **Download** the `leaderboard.html` file from this repository
2. **Open it** in a browser (or host it anywhere - GitHub Pages, your own web server, etc.)
3. **Enter your API URL** in the config box:
   - Format: `http://YOUR-SERVER-IP:8080`
   - Example: `http://123.45.67.89:8080`
4. Click **"Connect to Server"**
5. Your leaderboard is now live! 🎉

---

## API Endpoints

The plugin exposes three endpoints:

### `GET /api/stats`
Server summary statistics

**Example Response:**
```json
{
  "totalPlayers": 50,
  "totalGames": 234,
  "totalKills": 12450,
  "highestRound": 42,
  "serverName": "My Legion Mob Arena Server",
  "timestamp": 1700000000000
}
```

### `GET /api/leaderboard`
Full leaderboard with all players

**Example Response:**
```json
[
  {
    "playerName": "Player1",
    "playerId": "uuid-here",
    "highestRound": 42,
    "totalGamesPlayed": 25,
    "totalKills": 2500,
    "totalGemsEarned": 300,
    "lastPlayed": 1700000000000
  }
]
```

### `GET /api/player/{uuid}`
Individual player statistics

**Example:** `/api/player/550e8400-e29b-41d4-a716-446655440000`

**Example Response:**
```json
{
  "playerName": "Player1",
  "playerId": "550e8400-e29b-41d4-a716-446655440000",
  "gems": 150,
  "highestRound": 42,
  "gamesPlayed": 25,
  "gamesWon": 5,
  "totalKills": 2500,
  "totalDeaths": 20,
  "kdr": 125.0,
  "winRate": 20.0,
  "unlockedKits": 7,
  "lastSeen": 1700000000000
}
```

---

## Hosting the Leaderboard Website

You have several options for hosting the `leaderboard.html` file:

### Option 1: GitHub Pages (Free, Recommended)
1. Create a new repository on GitHub
2. Upload `leaderboard.html`
3. Go to Settings → Pages
4. Select main branch
5. Your site will be live at `https://yourusername.github.io/repo-name/leaderboard.html`

### Option 2: Your Own Web Server
1. Upload `leaderboard.html` to your web server
2. Access it at `http://yourwebsite.com/leaderboard.html`

### Option 3: Local File
1. Just open `leaderboard.html` directly in a browser
2. Works perfectly for local testing or LAN servers

---

## Troubleshooting

### "Failed to connect to server"

**Check these:**
1. Is the API enabled in config.yml?
2. Is the server running?
3. Is port 8080 open and forwarded?
4. Are you using the correct IP address?
5. Try accessing `http://YOUR-IP:8080/api/stats` directly in browser

**Common issues:**
- **Localhost:** Use `http://localhost:8080` if testing locally
- **External IP:** Use your public IP if hosting publicly
- **HTTPS websites:** Can't connect to HTTP APIs (browser security)
  - Solution: Host the HTML on HTTP too, or set up HTTPS for the API

### "No players on leaderboard"

- Make sure database is enabled in config.yml
- Make sure players have played at least one game
- Check console for database errors

### Port already in use

Change the port in config.yml:
```yaml
api:
  port: 8081  # Or any other available port
```

Don't forget to update the port in your firewall/router too!

---

## Security Notes

The API is **read-only** and only exposes public leaderboard data. However:

- **Don't expose sensitive information** in server names
- **Consider using a firewall** to restrict access if needed
- **Player UUIDs are public** - this is normal for Minecraft
- **No authentication required** - it's a public leaderboard

---

## Advanced: CORS and HTTPS

**If hosting the HTML on a different domain:**

The API already has CORS enabled, so it should work from any website.

**If you need HTTPS:**

You'll need to put a reverse proxy (like nginx) in front of the API with SSL certificates. This is advanced and beyond the scope of this guide.

---

## Support

If you need help:
1. Check the console for error messages
2. Make sure all requirements are met (MySQL enabled, port open, etc.)
3. Test the API endpoint directly in your browser
4. Create an issue on GitHub with error details

---

## How It Works

```
Plugin (Port 8080)
    ↓
  Exposes REST API
    ↓
Website fetches data every 60s
    ↓
Displays live leaderboard
```

No external services needed - everything runs on your server!