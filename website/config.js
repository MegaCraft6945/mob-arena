/**
 * Legion Mob Arena - Website Configuration
 *
 * IMPORTANT: Update the API_BASE_URL to point to your Minecraft server's API
 *
 * Format: http://YOUR-SERVER-IP:PORT
 * Example: http://123.45.67.89:25566
 *
 * Make sure:
 * 1. Your server has the API enabled in config.yml (api.enabled: true)
 * 2. The port 25566 (or your custom port) is open in your firewall
 * 3. Port forwarding is set up if hosting from home
 */

const CONFIG = {
    // Replace this with your actual server IP and API port
    API_BASE_URL: 'http://YOUR-SERVER-IP:25566',

    // Optional: Server display name
    SERVER_NAME: 'Legion Mob Arena',

    // Auto-refresh interval in milliseconds (default: 30 seconds)
    REFRESH_INTERVAL: 30000
};
