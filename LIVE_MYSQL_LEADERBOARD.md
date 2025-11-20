# Live MySQL Leaderboard Setup

This guide shows you how to set up a **live leaderboard** that reads directly from your MySQL database - no manual updates needed!

## What You'll Get

- ✅ **Always up-to-date** - Reads directly from your MySQL database
- ✅ **100% automatic** - No scripts or manual copying
- ✅ **100% free** - Uses Vercel's free tier
- ✅ **Fast & reliable** - Serverless edge functions
- ✅ **Secure** - Database credentials stored safely

---

## Setup (10 minutes)

### Step 1: Fork & Clone Repository

1. Fork this repository on GitHub (if you haven't already)
2. Clone it to your computer:
   ```bash
   git clone https://github.com/YOUR-USERNAME/mob-arena.git
   cd mob-arena
   ```

### Step 2: Install Vercel CLI

```bash
npm install -g vercel
```

### Step 3: Deploy to Vercel

```bash
vercel login
vercel
```

Follow the prompts:
- **Set up and deploy?** Yes
- **Which scope?** Your account
- **Link to existing project?** No
- **Project name?** mob-arena (or whatever you want)
- **Directory?** ./ (just press Enter)
- **Override settings?** No

### Step 4: Add Database Credentials

Add your MySQL credentials as environment variables:

```bash
vercel env add DB_HOST
# Enter your database host (e.g., mysql.example.com or localhost)

vercel env add DB_PORT
# Enter 3306 (or your custom port)

vercel env add DB_USER
# Enter your database username

vercel env add DB_PASSWORD
# Enter your database password

vercel env add DB_NAME
# Enter your database name (default: legion_mobarena)
```

For each variable, select:
- **Environment:** Production, Preview, Development (select all 3)

### Step 5: Redeploy with Environment Variables

```bash
vercel --prod
```

You'll get a URL like: `https://mob-arena-username.vercel.app`

### Step 6: Update Website to Use Live API

1. Open `docs/script.js`
2. Find this line:
   ```javascript
   const apiUrl = 'https://your-project.vercel.app/api/leaderboard';
   ```
3. Replace with your actual Vercel URL:
   ```javascript
   const apiUrl = 'https://mob-arena-username.vercel.app/api/leaderboard';
   ```
4. Commit and push:
   ```bash
   git add docs/script.js
   git commit -m "Update API URL for live leaderboard"
   git push
   ```

### Step 7: Enable GitHub Pages

1. Go to your repository settings
2. Navigate to **Pages**
3. Source: **Deploy from a branch**
4. Branch: **main**, Folder: **/docs**
5. Click **Save**

Your leaderboard is now live at:
```
https://YOUR-USERNAME.github.io/mob-arena/
```

**It will automatically update from your MySQL database every 60 seconds!** 🎉

---

## How It Works

```
Plugin → MySQL Database ← Vercel API ← GitHub Pages Website
```

1. Plugin saves stats to MySQL (already happening)
2. Vercel API reads from MySQL when requested
3. Website fetches from Vercel API every 60 seconds
4. Always shows live data!

---

## Troubleshooting

### "Failed to fetch leaderboard"

**Check API is working:**
```bash
curl https://your-project.vercel.app/api/leaderboard
```

Should return JSON with player data.

**If you see database connection error:**
- Check environment variables: `vercel env ls`
- Verify database credentials are correct
- Make sure your database allows connections from external IPs
- For Pterodactyl: Use the external IP, not `localhost`

### Database Connection from Pterodactyl

If your database is on Pterodactyl panel:
- **Host:** Use your server's IP address (not localhost)
- **Port:** Usually 3306
- **User:** Your database user
- **Password:** Your database password
- **Database:** `legion_mobarena` (or whatever you configured)

You may need to allow external connections in your MySQL config.

### Website shows old data

- Clear browser cache (Ctrl+F5)
- Check API URL is correct in `docs/script.js`
- Verify Vercel deployment succeeded: `vercel ls`

### Rate Limits

Vercel free tier includes:
- ✅ 100GB bandwidth/month
- ✅ 100,000 function invocations/month
- ✅ Unlimited projects

This is more than enough for any Minecraft server leaderboard!

---

## Security Notes

- ✅ Database credentials stored securely in Vercel (not in code)
- ✅ Read-only database queries (no writes)
- ✅ CORS enabled only for GET requests
- ✅ No authentication needed (read-only public leaderboard)

**Best practice:** Create a separate MySQL user with read-only permissions:

```sql
CREATE USER 'leaderboard_readonly'@'%' IDENTIFIED BY 'secure_password';
GRANT SELECT ON legion_mobarena.player_data TO 'leaderboard_readonly'@'%';
FLUSH PRIVILEGES;
```

Use this user for the Vercel API.

---

## Updating

If you modify the API code:

```bash
git add .
git commit -m "Update API"
git push
vercel --prod
```

The website will automatically use the new version!

---

## Custom Domain (Optional)

Want `stats.yourserver.com` instead of the Vercel URL?

1. In Vercel dashboard → Your project → Settings → Domains
2. Add your custom domain
3. Add CNAME record in your DNS: `stats → your-project.vercel.app`
4. Update `docs/script.js` with your custom domain

Same process for GitHub Pages if you want a custom domain there too!

---

## For Other Server Owners

This setup is specific to your server. Other servers using your plugin would need to:
1. Fork the repository
2. Deploy their own Vercel API with their database credentials
3. Set up their own GitHub Pages

Or they can use the simpler manual update method from `LEADERBOARD_FOR_SERVER_OWNERS.md`.
