// Fetch and display leaderboard data
async function loadLeaderboard() {
    try {
        const response = await fetch('leaderboard.json');
        if (!response.ok) {
            throw new Error('Failed to load leaderboard data');
        }

        const data = await response.json();
        displayLeaderboard(data);
        updateStatsSummary(data);
        updateLastUpdated();
    } catch (error) {
        console.error('Error loading leaderboard:', error);
        document.getElementById('leaderboardBody').innerHTML =
            '<div class="error">Failed to load leaderboard data. Please try again later.</div>';
    }
}

function displayLeaderboard(players) {
    const leaderboardBody = document.getElementById('leaderboardBody');

    if (!players || players.length === 0) {
        leaderboardBody.innerHTML = '<div class="loading">No players on the leaderboard yet!</div>';
        return;
    }

    // Sort players by highest round, then by total kills
    players.sort((a, b) => {
        if (b.highestRound !== a.highestRound) {
            return b.highestRound - a.highestRound;
        }
        return b.totalKills - a.totalKills;
    });

    let html = '';
    players.forEach((player, index) => {
        html += `
            <div class="leaderboard-row">
                <div class="rank">${getRankDisplay(index + 1)}</div>
                <div class="player-name">${escapeHtml(player.playerName)}</div>
                <div class="round-col"><span class="round-value">${player.highestRound}</span></div>
                <div class="games-col">${player.totalGamesPlayed.toLocaleString()}</div>
                <div class="kills-col">${player.totalKills.toLocaleString()}</div>
                <div class="gems-col">${player.totalGemsEarned.toLocaleString()}</div>
            </div>
        `;
    });

    leaderboardBody.innerHTML = html;
}

function updateStatsSummary(players) {
    if (!players || players.length === 0) {
        return;
    }

    const totalPlayers = players.length;
    const totalGames = players.reduce((sum, p) => sum + p.totalGamesPlayed, 0);
    const totalKills = players.reduce((sum, p) => sum + p.totalKills, 0);
    const highestRound = Math.max(...players.map(p => p.highestRound));

    document.getElementById('totalPlayers').textContent = totalPlayers.toLocaleString();
    document.getElementById('totalGames').textContent = totalGames.toLocaleString();
    document.getElementById('totalKills').textContent = totalKills.toLocaleString();
    document.getElementById('highestRound').textContent = highestRound.toLocaleString();
}

function getRankDisplay(rank) {
    if (rank === 1) return '🥇';
    if (rank === 2) return '🥈';
    if (rank === 3) return '🥉';
    return `#${rank}`;
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function updateLastUpdated() {
    const now = new Date();
    const formatted = now.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
    document.getElementById('lastUpdated').textContent = formatted;
}

// Initial load
loadLeaderboard();

// Auto-refresh every 60 seconds
setInterval(loadLeaderboard, 60000);
