# Legion Mob Arena - Website Deployment Guide

This folder contains the standalone website files for hosting the Mob Arena stats dashboard on any web hosting service, including **InfinityFree**.

## 📁 Files Included

- `index.html` - Main stats dashboard page
- `config.js` - Configuration file for API endpoint
- `README.md` - This deployment guide

## 🚀 Quick Start

### Step 1: Configure the API Endpoint

1. Open `config.js` in a text editor
2. Replace `YOUR-SERVER-IP` with your Minecraft server's public IP address
3. Update the port if you changed it from default (25566)

Example:
```javascript
const CONFIG = {
    API_BASE_URL: 'http://123.45.67.89:25566',
    SERVER_NAME: 'Legion Mob Arena',
    REFRESH_INTERVAL: 30000
};
```

### Step 2: Enable API in Server Config

Make sure your server's `config.yml` has the API enabled:

```yaml
api:
  enabled: true
  port: 25566
  server-name: "Legion Mob Arena"
```

### Step 3: Configure Firewall & Port Forwarding

**For VPS/Dedicated Servers:**
```bash
# Allow port 25566 through firewall
sudo ufw allow 25566/tcp
```

**For Home Hosting:**
- Forward port 25566 to your server in your router settings
- Use your public IP address (find it at: https://whatismyipaddress.com)

**For Pterodactyl/Game Hosting:**
- Create a new port allocation for 25566
- Add it to your server's allocations

## 📤 Deploying to InfinityFree

### Method 1: File Manager Upload

1. Go to your InfinityFree control panel
2. Open **File Manager**
3. Navigate to `htdocs` folder (this is your web root)
4. Upload both `index.html` and `config.js`
5. Edit `config.js` directly in the file manager with your server IP
6. Your website will be live at: `http://yoursite.infinityfreeapp.com`

### Method 2: FTP Upload

1. Get your FTP credentials from InfinityFree control panel
2. Use an FTP client (FileZilla recommended)
3. Connect to your InfinityFree account
4. Upload `index.html` and `config.js` to the `htdocs` folder
5. Done!

**FTP Settings:**
- Host: `ftpupload.net`
- Port: `21`
- Username: Your InfinityFree FTP username
- Password: Your InfinityFree FTP password

## 🌐 Other Hosting Options

### GitHub Pages

1. Create a new GitHub repository
2. Upload `index.html` and `config.js`
3. Go to Settings → Pages
4. Enable GitHub Pages from main branch
5. Update `config.js` with your server IP
6. Access at: `https://yourusername.github.io/repo-name`

### Netlify / Vercel

1. Create account on Netlify or Vercel
2. Drag and drop the `website` folder
3. Update `config.js` with your server IP
4. Instant deployment!

### Any Web Server

Simply upload both files to any web hosting service that supports static HTML/JavaScript.

## ⚠️ Important Notes

### CORS is Already Enabled

The Legion Mob Arena plugin automatically enables CORS (Cross-Origin Resource Sharing), which allows your hosted website to communicate with your Minecraft server's API.

### Port Accessibility

Your API port **must be publicly accessible** for the website to work:

- ✅ VPS/Dedicated: Open port in firewall
- ✅ Home Hosting: Port forward in router
- ✅ Pterodactyl: Allocate additional port
- ❌ Some shared hosting may block external ports

### Security Considerations

The API is **read-only** and exposes only leaderboard statistics. No sensitive data or game-breaking information is exposed.

If you want to restrict access:
1. Use a reverse proxy (nginx/Apache)
2. Add IP whitelisting
3. Use authentication headers (requires plugin modification)

## 🔧 Troubleshooting

### "Configuration Required" Error
- You haven't updated `config.js` with your server IP
- Edit the file and replace `YOUR-SERVER-IP`

### "Failed to load stats" Error
Check:
1. Is your Minecraft server running?
2. Is the API enabled in `config.yml`?
3. Is port 25566 open and accessible?
4. Try accessing directly: `http://YOUR-IP:25566/api/stats`
5. Check browser console (F12) for CORS errors

### "Mixed Content" Warning (HTTPS sites)
If your website is hosted on HTTPS but your API is HTTP, browsers may block the connection.

Solutions:
- Use a reverse proxy with SSL certificate
- Host website on HTTP (InfinityFree supports both)
- Use Cloudflare Tunnel for secure access

### API Not Responding
```bash
# Test API from command line
curl http://YOUR-SERVER-IP:25566/api/stats

# Should return JSON with server stats
```

## 📊 API Endpoints

Your website uses these API endpoints:

- `GET /api/stats` - Server summary statistics
- `GET /api/leaderboard` - Full player leaderboard
- `GET /api/player/{uuid}` - Individual player stats

Test them directly in your browser:
- `http://YOUR-IP:25566/api/stats`
- `http://YOUR-IP:25566/api/leaderboard`

## 🎨 Customization

### Change Colors

Edit the CSS in `index.html` to match your server's theme:

```css
/* Header gradient */
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

/* Change to your colors */
background: linear-gradient(135deg, #YOUR_COLOR_1 0%, #YOUR_COLOR_2 100%);
```

### Change Refresh Rate

Edit `config.js`:
```javascript
REFRESH_INTERVAL: 60000  // Refresh every 60 seconds
```

### Add Custom Domain

Most hosting services allow custom domains:
1. Purchase a domain (Namecheap, GoDaddy, etc.)
2. Update DNS to point to your hosting
3. Configure in hosting control panel

## 📞 Support

- Plugin Issues: Check plugin README or create GitHub issue
- Hosting Issues: Contact your hosting provider
- API Issues: Ensure server is running and port is accessible

## 📜 License

This website is part of the Legion Mob Arena plugin.
Free to use and modify for your server.

---

**Enjoy your new Mob Arena stats website! 🎮**
