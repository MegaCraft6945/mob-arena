# Server Owner's Guide: Setting Up Your Leaderboard

This guide is for **server owners** who want to display a leaderboard website for their players.

## Overview

The Legion Mob Arena plugin automatically saves player stats to a file. You can display these stats on a free website using GitHub Pages!

## What You'll Get

- A beautiful leaderboard website showing your players' stats
- Auto-refreshing display
- Mobile-friendly design
- **100% free hosting** (forever)
- Custom domain support (optional)

---

## Setup Guide (5-10 minutes)

### Step 1: Fork This Repository

1. Go to: https://github.com/MegaCraft6945/mob-arena
2. Click the **"Fork"** button (top-right)
3. Select your account
4. Click **"Create fork"**

You now have your own copy!

### Step 2: Enable GitHub Pages

1. In **your forked repository**, click **"Settings"** tab
2. Scroll down and click **"Pages"** in the left sidebar
3. Under **"Source"**: Select **"Deploy from a branch"**
4. Under **"Branch"**: Select **`main`** (or whichever branch), Folder: **`/docs`**
5. Click **"Save"**

Wait 2-3 minutes, then your leaderboard will be live at:
```
https://YOUR-USERNAME.github.io/mob-arena/
```

### Step 3: Update With Your Stats

**Option A: Manual Update (Simple)**

1. On your Minecraft server, open:
   ```
   plugins/LegionMobArena/leaderboard.json
   ```

2. Copy the entire file contents

3. In your GitHub repository, go to:
   ```
   docs/leaderboard.json
   ```

4. Click the **pencil icon** (Edit)

5. Paste your server's data

6. Click **"Commit changes"**

Wait 1-2 minutes and your website will update!

**Option B: Auto Update Script (Advanced)**

If you want the leaderboard to update automatically:

1. Clone your forked repository to your server machine:
   ```bash
   git clone https://github.com/YOUR-USERNAME/mob-arena.git
   ```

2. Edit `update-leaderboard.ps1` (Windows) or create a bash script (Linux):

   **For Windows/Pterodactyl on Windows:**
   - Open `update-leaderboard.ps1`
   - Update the paths to match your server
   - Set up Windows Task Scheduler (see `LEADERBOARD_SETUP.md`)

   **For Linux/Pterodactyl on Linux:**
   Create `update-leaderboard.sh`:
   ```bash
   #!/bin/bash
   SERVER_PATH="/path/to/server/plugins/LegionMobArena/leaderboard.json"
   REPO_PATH="/path/to/mob-arena"

   cp "$SERVER_PATH" "$REPO_PATH/docs/leaderboard.json"
   cd "$REPO_PATH"
   git add docs/leaderboard.json
   git commit -m "Update leaderboard - $(date)"
   git push
   ```

   Make it executable:
   ```bash
   chmod +x update-leaderboard.sh
   ```

   Add to crontab (updates every 15 minutes):
   ```bash
   crontab -e
   # Add this line:
   */15 * * * * /path/to/update-leaderboard.sh
   ```

---

## Customization

### Change Server Name

Edit `docs/index.html` and change:
```html
<h1>⚔️ Legion Mob Arena</h1>
```
To:
```html
<h1>⚔️ YourServerName Mob Arena</h1>
```

### Change Colors

Edit `docs/styles.css` and modify the gradient:
```css
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
```

Use any colors you want! Try: https://cssgradient.io/

### Add Your Domain

1. In repository settings → Pages → Custom domain
2. Enter your domain (e.g., `stats.yourserver.com`)
3. Add a CNAME record in your DNS pointing to `YOUR-USERNAME.github.io`

---

## Troubleshooting

**Website shows "404 Not Found"**
- Make sure GitHub Pages is enabled in settings
- Check that you selected `/docs` folder
- Wait 2-3 minutes after enabling

**Website shows old data**
- GitHub Pages caches for 1-2 minutes
- Clear your browser cache (Ctrl+F5)
- Check that `docs/leaderboard.json` was actually updated

**"leaderboard.json" not found error on website**
- Make sure the file exists in `docs/leaderboard.json`
- Check that you committed the file to the repository
- Verify the file has valid JSON (use jsonlint.com)

**Players not showing up**
- Make sure they've played at least one game
- Check `plugins/LegionMobArena/leaderboard.json` on your server
- Verify the plugin is running (check console for errors)

---

## Support

If you need help:
1. Check the main README.md for plugin documentation
2. Create an issue on the GitHub repository
3. Include your error messages and what you've tried

---

## For Plugin Developers

Want to contribute or customize the plugin? See the main README.md for build instructions and contribution guidelines.
