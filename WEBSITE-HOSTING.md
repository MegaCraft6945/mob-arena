# 🌐 Website Hosting Guide

## Quick Start: Host Your Stats Website

The Legion Mob Arena plugin includes a standalone website that you can host on **any web hosting service** (including free services like InfinityFree, GitHub Pages, or Netlify).

### 📁 Website Files Location

All website files are in the **`website/`** folder:
- `website/index.html` - The main stats dashboard
- `website/config.js` - Configuration file (update with your server IP)
- `website/README.md` - Detailed deployment instructions

### ⚡ Quick Setup (3 Steps)

#### 1. Configure API Endpoint
Edit `website/config.js` and replace `YOUR-SERVER-IP` with your server's IP:
```javascript
API_BASE_URL: 'http://123.45.67.89:25566'
```

#### 2. Enable API in Plugin
Make sure `config.yml` has API enabled:
```yaml
api:
  enabled: true
  port: 25566
```

#### 3. Upload to Hosting
Upload `index.html` and `config.js` to your web hosting service.

### 🎯 Hosting Options

| Service | Free? | Difficulty | Best For |
|---------|-------|------------|----------|
| **InfinityFree** | ✅ Yes | Easy | Beginners, custom domains |
| **GitHub Pages** | ✅ Yes | Easy | Developers, version control |
| **Netlify** | ✅ Yes | Very Easy | Drag-and-drop deployment |
| **Vercel** | ✅ Yes | Very Easy | Modern deployment |
| **Your own VPS** | ❌ No | Medium | Full control, custom setup |

### 📖 Full Instructions

See **[website/README.md](website/README.md)** for:
- Detailed InfinityFree deployment steps
- Firewall and port forwarding setup
- Troubleshooting common issues
- Customization options
- Security considerations

### ⚠️ Requirements

1. **Plugin API must be enabled** in your server's config
2. **Port 25566 must be accessible** from the internet
3. **Web hosting** that supports static HTML/JavaScript files

### 🔧 Testing Your Setup

1. Make sure your server is running
2. Test the API directly in your browser:
   ```
   http://YOUR-SERVER-IP:25566/api/stats
   ```
3. If you see JSON data, your API is working!
4. If not, check firewall and port forwarding

### 📞 Need Help?

- **Website deployment issues**: Check `website/README.md`
- **API not responding**: Check firewall and port forwarding
- **Plugin issues**: See main `README.md` or create a GitHub issue

---

**Ready to host?** Go to the `website/` folder and follow the README! 🚀
