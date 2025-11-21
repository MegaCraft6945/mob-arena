# Legion Mob Arena - Live Leaderboard

This is the GitHub Pages website that displays the live leaderboard for your Legion Mob Arena server.

## Setup Instructions

### Step 1: Enable GitHub Pages

1. Go to your repository settings: `https://github.com/YOUR-USERNAME/mob-arena/settings`
2. Scroll down and click **"Pages"** in the left sidebar
3. Under **"Source"**: Select **"Deploy from a branch"**
4. Under **"Branch"**: Select your branch (e.g., `main`), Folder: **`/docs`**
5. Click **"Save"**

Wait 2-3 minutes, then your leaderboard will be live at:
```
https://YOUR-USERNAME.github.io/mob-arena/
```

### Step 2: Configure Your Server API

Once the website is live:

1. **Visit your GitHub Pages URL** (see above)
2. **Enter your server's API URL** in the configuration box:
   - Format: `http://YOUR-SERVER-IP:25566`
   - Example: `http://123.45.67.89:25566`
3. Click **"Connect to Server"**
4. Your live leaderboard will appear!

The website will remember your API URL, so you only need to configure it once.

### Step 3: Make Sure Your Server API is Running

Don't forget to:
1. Enable the API in `plugins/LegionMobArena/config.yml`:
   ```yaml
   api:
     enabled: true
     port: 25566
   ```
2. Open port 25566 on your server (firewall/Pterodactyl allocation)
3. Restart your server

## How It Works

```
Your Server (Port 25566)
    ↓
  Exposes REST API
    ↓
GitHub Pages Website
    ↓
Displays Live Leaderboard
```

The website fetches fresh data from your server every 60 seconds!

## Troubleshooting

**"Failed to connect to server"**
- Make sure API is enabled in config.yml
- Check that port 25566 is open and accessible
- Try accessing `http://YOUR-IP:25566/api/stats` directly in your browser
- If using HTTPS GitHub Pages, you may need to allow mixed content in your browser

**Website not updating**
- Check GitHub Pages deployment status in repository Actions tab
- Wait a few minutes after pushing changes
- Clear your browser cache (Ctrl+F5)

## Customization

Want to customize the website?

- Edit `docs/index.html` to change colors, layout, or server name
- Commit and push your changes
- GitHub Pages will automatically update within 1-2 minutes

## For Other Servers

If you're running a different Legion Mob Arena server:
1. Fork this repository
2. Follow the setup steps above for your own fork
3. Your leaderboard will be at `https://YOUR-USERNAME.github.io/mob-arena/`
