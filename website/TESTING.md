# Testing Your Website Before Deployment

This guide helps you test the website locally before uploading to InfinityFree or other hosting services.

## ⚠️ Important: Don't Open HTML Directly!

**DON'T DO THIS:** Double-clicking `index.html` will open it with `file://` protocol, which prevents JavaScript from loading `config.js` properly. You'll see a 404 error.

**DO THIS INSTEAD:** Use a local web server (see options below).

---

## 🧪 Testing Methods

### Option 1: Python Web Server (Easiest)

**If you have Python installed:**

```bash
# Navigate to the website folder
cd website

# Start a simple web server
python -m http.server 8000

# Or for Python 2:
python -m SimpleHTTPServer 8000
```

Then visit: `http://localhost:8000`

### Option 2: Node.js http-server

**If you have Node.js installed:**

```bash
# Install http-server globally (one time)
npm install -g http-server

# Navigate to the website folder
cd website

# Start the server
http-server -p 8000
```

Then visit: `http://localhost:8000`

### Option 3: PHP Built-in Server

**If you have PHP installed:**

```bash
# Navigate to the website folder
cd website

# Start PHP server
php -S localhost:8000
```

Then visit: `http://localhost:8000`

### Option 4: VS Code Live Server Extension

If you use Visual Studio Code:

1. Install the "Live Server" extension
2. Right-click on `index.html`
3. Select "Open with Live Server"

---

## 📝 Testing Checklist

### Step 1: Check Files Are Present

Make sure both files are in the same directory:
- ✅ `index.html`
- ✅ `config.js`

```bash
# Verify files exist
ls -la
# Should show both index.html and config.js
```

### Step 2: Configure Your Server IP

Edit `config.js`:

```javascript
const CONFIG = {
    API_BASE_URL: 'http://YOUR-ACTUAL-IP:25566',  // ← Change this!
    SERVER_NAME: 'Legion Mob Arena',
    REFRESH_INTERVAL: 30000
};
```

**Example:**
```javascript
API_BASE_URL: 'http://123.45.67.89:25566'
```

### Step 3: Test API Directly

Before testing the website, make sure your API is working:

```bash
# Test with curl
curl http://YOUR-SERVER-IP:25566/api/stats

# Should return JSON like:
# {"totalPlayers":5,"totalGames":23,...}
```

**Or** open in your browser:
```
http://YOUR-SERVER-IP:25566/api/stats
```

If this doesn't work, fix your server/API first!

### Step 4: Start Local Web Server

Use one of the methods above to start a local web server.

### Step 5: Open in Browser

Visit `http://localhost:8000` (or whatever port you used).

### Step 6: Check for Errors

Open browser DevTools (F12) and check the Console tab:

**✅ Good Signs:**
- No errors in console
- Stats load correctly
- Numbers show up in stat cards
- Leaderboard displays players

**❌ Bad Signs & Solutions:**

| Error | Cause | Solution |
|-------|-------|----------|
| "config.js Not Found" | File missing or wrong directory | Ensure both files are in same folder |
| "Configuration Required" | Didn't update config.js | Edit config.js with your server IP |
| "Failed to load stats" | Can't reach API | Check server is running, port is open |
| "CORS error" | Server not allowing connections | CORS is already enabled in plugin |
| "Failed to fetch" | Server offline or wrong IP | Verify server IP and port |

---

## 🌐 Testing with Remote Server

If your Minecraft server is already online:

1. **Update config.js** with your real server IP
2. **Start local web server** (using one of the methods above)
3. **Visit** `http://localhost:8000`
4. Stats should load from your live server!

---

## 📤 Deploying to InfinityFree

Once testing works locally, you're ready to deploy!

### Upload Process

1. **Login to InfinityFree** control panel
2. **Open File Manager**
3. **Navigate to** `htdocs` folder
4. **Upload BOTH files:**
   - `index.html`
   - `config.js`
5. **Visit your site:** `http://yoursite.infinityfreeapp.com`

### Double-Check Deployment

After uploading, verify both files are accessible:
- `http://yoursite.infinityfreeapp.com/index.html` ✅
- `http://yoursite.infinityfreeapp.com/config.js` ✅

If `config.js` shows a 404, you forgot to upload it!

---

## 🔧 Common Issues & Solutions

### Issue: "404 Not Found" for config.js

**Causes:**
1. File not uploaded to hosting
2. File in wrong directory
3. Typo in filename (must be exactly `config.js`)

**Solutions:**
1. Make sure you uploaded **both** files
2. Both files must be in the **same directory**
3. Check filename is exactly `config.js` (case-sensitive on some systems)

### Issue: Stats Don't Load (After Config is Working)

**Causes:**
1. Minecraft server is offline
2. API is disabled in config.yml
3. Port 25566 is blocked
4. Wrong IP address in config.js

**Solutions:**
1. Make sure your Minecraft server is running
2. Check `plugins/LegionMobArena/config.yml`:
   ```yaml
   api:
     enabled: true
     port: 25566
   ```
3. Open port in firewall:
   ```bash
   sudo ufw allow 25566/tcp
   ```
4. Test API directly: `http://YOUR-IP:25566/api/stats`

### Issue: Works Locally, Fails on Hosting

**Cause:** Usually a wrong IP address or port issue

**Solutions:**
1. Make sure you're using your **public IP** (not localhost or 127.0.0.1)
2. Check port forwarding if hosting from home
3. Verify firewall allows incoming connections on port 25566

---

## 🎯 Quick Troubleshooting Commands

```bash
# Check if server is listening on port 25566
netstat -tulpn | grep 25566

# Test API from command line
curl http://YOUR-SERVER-IP:25566/api/stats

# Check firewall status
sudo ufw status

# Allow port through firewall
sudo ufw allow 25566/tcp
```

---

## ✅ Success Checklist

Before deploying, make sure:

- [ ] Both files (index.html and config.js) exist
- [ ] config.js has your actual server IP (not YOUR-SERVER-IP)
- [ ] Minecraft server is running
- [ ] API is enabled in config.yml
- [ ] Port 25566 is open in firewall
- [ ] Website works when testing locally
- [ ] Can access API directly: `http://YOUR-IP:25566/api/stats`

If all boxes are checked, you're ready to deploy! 🚀

---

## 📞 Still Having Issues?

1. Check browser console (F12) for specific errors
2. Test API directly in browser
3. Verify firewall/port forwarding settings
4. Make sure Minecraft server is running
5. Check server logs for API startup messages

For plugin issues, see the main README or create a GitHub issue.
