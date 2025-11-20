# Legion Mob Arena - Auto Leaderboard Updater
# This script automatically copies the leaderboard to GitHub

# ============================================
# CONFIGURATION - UPDATE THESE PATHS
# ============================================

# Path to your Minecraft server's leaderboard file
$serverLeaderboardPath = "C:\path\to\your\server\plugins\LegionMobArena\leaderboard.json"

# Path to your local git repository
$repoPath = "C:\Users\craig\OneDrive\Documents\mob-arena"

# ============================================
# SCRIPT - Don't modify below this line
# ============================================

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  Legion Mob Arena - Leaderboard Auto-Updater" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Check if server leaderboard file exists
if (-Not (Test-Path $serverLeaderboardPath)) {
    Write-Host "[ERROR] Server leaderboard file not found at:" -ForegroundColor Red
    Write-Host "        $serverLeaderboardPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please update the `$serverLeaderboardPath variable in this script." -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if repository exists
if (-Not (Test-Path $repoPath)) {
    Write-Host "[ERROR] Repository not found at:" -ForegroundColor Red
    Write-Host "        $repoPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please update the `$repoPath variable in this script." -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

# Copy leaderboard file
Write-Host "[1/4] Copying leaderboard.json from server..." -ForegroundColor Yellow
try {
    Copy-Item -Path $serverLeaderboardPath -Destination "$repoPath\docs\leaderboard.json" -Force
    Write-Host "      ✓ File copied successfully" -ForegroundColor Green
} catch {
    Write-Host "      ✗ Failed to copy file: $_" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Navigate to repository
Write-Host "[2/4] Navigating to repository..." -ForegroundColor Yellow
Set-Location $repoPath

# Check for changes
Write-Host "[3/4] Checking for changes..." -ForegroundColor Yellow
$status = git status --porcelain docs/leaderboard.json

if ([string]::IsNullOrWhiteSpace($status)) {
    Write-Host "      ✓ No changes detected - leaderboard is up to date" -ForegroundColor Green
    Write-Host ""
    Write-Host "Leaderboard is already current. No update needed." -ForegroundColor Cyan
    exit 0
}

Write-Host "      ✓ Changes detected" -ForegroundColor Green

# Commit and push
Write-Host "[4/4] Pushing to GitHub..." -ForegroundColor Yellow
try {
    git add docs/leaderboard.json
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    git commit -m "Update leaderboard - $timestamp"
    git push
    Write-Host "      ✓ Successfully pushed to GitHub" -ForegroundColor Green
} catch {
    Write-Host "      ✗ Failed to push: $_" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  ✓ Leaderboard updated successfully!" -ForegroundColor Green
Write-Host "  Your website will update in 1-2 minutes." -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
