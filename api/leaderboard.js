// Serverless function to fetch leaderboard from MySQL
// Deploy this to Vercel (free)

const mysql = require('mysql2/promise');

module.exports = async (req, res) => {
  // CORS headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  try {
    // Connect to MySQL
    const connection = await mysql.createConnection({
      host: process.env.DB_HOST,
      port: process.env.DB_PORT || 3306,
      user: process.env.DB_USER,
      password: process.env.DB_PASSWORD,
      database: process.env.DB_NAME
    });

    // Query player data
    const [rows] = await connection.execute(`
      SELECT
        uuid as playerId,
        name as playerName,
        highest_round as highestRound,
        games_played as totalGamesPlayed,
        total_kills as totalKills,
        gems as totalGemsEarned,
        last_seen as lastPlayed
      FROM player_data
      ORDER BY highest_round DESC, total_kills DESC
    `);

    await connection.end();

    // Return JSON
    res.status(200).json(rows);

  } catch (error) {
    console.error('Database error:', error);
    res.status(500).json({
      error: 'Failed to fetch leaderboard',
      message: error.message
    });
  }
};
