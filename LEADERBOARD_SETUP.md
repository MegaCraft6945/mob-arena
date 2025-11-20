# Auto Leaderboard Updater Setup Guide

This guide will help you set up automatic leaderboard updates for your GitHub Pages website.

## Step 1: Configure the Script

1. Open `update-leaderboard.ps1` in Notepad or any text editor

2. Update these two paths at the top of the file:

```powershell
# Path to your Minecraft server's leaderboard file
$serverLeaderboardPath = "C:\path\to\your\server\plugins\LegionMobArena\leaderboard.json"

# Path to your local git repository
$repoPath = "C:\Users\craig\OneDrive\Documents\mob-arena"
```

**Example:**
```powershell
$serverLeaderboardPath = "C:\Users\craig\Desktop\MinecraftServer\plugins\LegionMobArena\leaderboard.json"
$repoPath = "C:\Users\craig\OneDrive\Documents\mob-arena"
```

3. Save the file

## Step 2: Test the Script

1. Open PowerShell
2. Navigate to the script location:
   ```powershell
   cd C:\Users\craig\OneDrive\Documents\mob-arena
   ```
3. Run the script:
   ```powershell
   .\update-leaderboard.ps1
   ```

If it works, you'll see green checkmarks and the leaderboard will be pushed to GitHub!

## Step 3: Set Up Automatic Updates (Windows Task Scheduler)

To make this run automatically every 15 minutes:

### Option A: Quick Setup (Run this command)

Open PowerShell **as Administrator** and run:

```powershell
$action = New-ScheduledTaskAction -Execute "PowerShell.exe" -Argument "-NoProfile -ExecutionPolicy Bypass -File `"C:\Users\craig\OneDrive\Documents\mob-arena\update-leaderboard.ps1`""
$trigger = New-ScheduledTaskTrigger -Once -At (Get-Date) -RepetitionInterval (New-TimeSpan -Minutes 15) -RepetitionDuration ([TimeSpan]::MaxValue)
$settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -StartWhenAvailable
Register-ScheduledTask -TaskName "Legion Mob Arena Leaderboard Updater" -Action $action -Trigger $trigger -Settings $settings -Description "Automatically updates the Legion Mob Arena leaderboard on GitHub Pages every 15 minutes"
```

### Option B: Manual Setup

1. Press `Win + R`, type `taskschd.msc`, press Enter
2. Click "Create Basic Task"
3. Name: `Legion Mob Arena Leaderboard Updater`
4. Trigger: Daily
5. Start date/time: Today, current time
6. Action: Start a program
7. Program: `PowerShell.exe`
8. Arguments: `-NoProfile -ExecutionPolicy Bypass -File "C:\Users\craig\OneDrive\Documents\mob-arena\update-leaderboard.ps1"`
9. Finish, then find the task in the list
10. Right-click → Properties → Triggers tab → Edit
11. Check "Repeat task every" → 15 minutes
12. Duration: Indefinitely
13. Click OK

## Step 4: Verify It's Working

1. Wait 15 minutes (or run the task manually from Task Scheduler)
2. Check your GitHub repository - you should see automatic commits
3. Visit your leaderboard website: https://megacraft6945.github.io/mob-arena/
4. The stats should match your in-game `/legion stats` command!

## Troubleshooting

**Script won't run:**
- Make sure execution policy allows scripts: `Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser`

**File not found errors:**
- Double-check your paths in the script
- Make sure the Minecraft server has created the leaderboard.json file (play at least one game)

**Git push fails:**
- Make sure you've configured Git credentials
- Try running `git push` manually in the repository first

## How Often Does It Update?

- **Script runs:** Every 15 minutes (you can change this in Task Scheduler)
- **GitHub Pages updates:** 1-2 minutes after each push
- **Total delay:** Your leaderboard will be at most 17 minutes behind

You can change the frequency in Task Scheduler if you want faster/slower updates!

## Manual Update

To update immediately without waiting:
```powershell
cd C:\Users\craig\OneDrive\Documents\mob-arena
.\update-leaderboard.ps1
```
