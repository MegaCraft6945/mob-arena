# Migration Guide - Embedded Website Removed

## What Changed?

The embedded stats website (`stats.html`) has been **removed** from the plugin. You should now use the **standalone website** in the `website/` folder.

## Why This Change?

- ✅ **Better flexibility** - Host the website anywhere (InfinityFree, GitHub Pages, Netlify, etc.)
- ✅ **Easier updates** - Update website without rebuilding the plugin
- ✅ **Custom domains** - Use your own domain name
- ✅ **No plugin restart needed** - Update website files anytime
- ✅ **Better performance** - Static hosting is faster than embedding

## Migration Steps

### Step 1: Update Your Server

After installing the new plugin version, update your `config.yml`:

**Old config (port 2001):**
```yaml
api:
  enabled: true
  port: 2001  # Old default
  server-name: "My Legion Mob Arena Server"
```

**New config (port 25566):**
```yaml
api:
  enabled: true
  port: 25566  # New default - easier to remember!
  server-name: "Legion Mob Arena"
```

### Step 2: Open New Port

The new default port is **25566**. Make sure it's accessible:

**Firewall:**
```bash
sudo ufw allow 25566/tcp
```

**Port Forwarding:**
- Forward port **25566** (not 2001) in your router

**Pterodactyl:**
- Allocate port **25566** in your panel

### Step 3: Deploy Standalone Website

1. **Download** the `website/` folder from GitHub
2. **Edit** `config.js` with your server IP:
   ```javascript
   API_BASE_URL: 'http://YOUR-SERVER-IP:25566'
   ```
3. **Upload** to your web hosting service

See `website/README.md` for complete instructions.

## What Happens to the Old Embedded Website?

When you visit `http://YOUR-SERVER-IP:25566/` you'll now see:

- ✅ API status page
- ✅ Links to test API endpoints
- ✅ Instructions for setting up the standalone website
- ❌ No embedded stats dashboard (use standalone website instead)

## Quick Setup Guide

### Option 1: InfinityFree (Free Hosting)

1. Sign up at InfinityFree
2. Upload `index.html` and `config.js` to `htdocs/`
3. Edit `config.js` with your server IP
4. Visit: `http://yoursite.infinityfreeapp.com`

### Option 2: GitHub Pages (Free)

1. Create GitHub repo
2. Upload website files
3. Enable GitHub Pages in Settings
4. Edit `config.js` with your server IP
5. Visit: `https://yourusername.github.io/repo-name`

### Option 3: Local Testing

```bash
cd website
python -m http.server 8000
# Visit: http://localhost:8000
```

## Troubleshooting

### "Port 2001 is already in use"

Your old config still has port 2001. Update it to 25566.

### "Website shows 404 for config.js"

Make sure you uploaded BOTH files:
- ✅ `index.html`
- ✅ `config.js`

### "Failed to load stats"

1. Check your server is running
2. Verify API is enabled: `api.enabled: true`
3. Test API directly: `http://YOUR-IP:25566/api/stats`
4. Check firewall/port forwarding

## Need Help?

- See `website/README.md` for detailed hosting instructions
- See `website/TESTING.md` for testing and troubleshooting
- Check `WEBSITE-HOSTING.md` for quick start guide

---

**Ready to migrate?** Build the new plugin version, update your config, and deploy the standalone website! 🚀
