package pewpew.smash.database.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pewpew.smash.database.Database;
import pewpew.smash.database.ResultSetProcessor;
import pewpew.smash.database.models.Achievement;
import pewpew.smash.database.models.Player;
import pewpew.smash.database.models.Rank;

public class AuthService {

    public Player authenticate(String username, String password) {
        username = sanitize(username);
        password = sanitize(password);

        Player player = getPlayerData(username);

        if (player == null) {
            return null;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword.equals(player.getPasswordHash())) {
            return player;
        } else {
            return null;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: SHA-256 algorithm not found.", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private Player getPlayerData(String username) {
        final Player[] player = { null };
        String query = "SELECT p.id, p.username, p.password, pr.rank_id, pr.current_xp, r.name as rank_name, r.description as rank_description, r.image_url as rank_image_url, r.max_xp, r.min_xp FROM players p "
                +
                "LEFT JOIN player_ranks pr ON p.id = pr.player_id " +
                "LEFT JOIN ranks r ON pr.rank_id = r.id " +
                "WHERE p.username = ?";
        Database.getInstance().executeQuery(query, new ResultSetProcessor() {
            @Override
            public void process(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    int playerId = rs.getInt("id");
                    String username = rs.getString("username");
                    String passwordHash = rs.getString("password");
                    Rank rank = new Rank(rs.getInt("rank_id"), rs.getString("rank_name"),
                            rs.getString("rank_description"), rs.getString("rank_image_url"),
                            rs.getInt("current_xp"), rs.getInt("max_xp"), rs.getInt("min_xp"));
                    List<Achievement> achievements = getPlayerAchievements(playerId);
                    player[0] = new Player(playerId, username, passwordHash, rank, achievements);
                }
            }
        }, username);
        return player[0];
    }

    private List<Achievement> getPlayerAchievements(int playerId) {
        List<Achievement> achievements = new ArrayList<>();
        String query = "SELECT a.id, a.name, a.description FROM player_achievements pa " +
                "JOIN achievements a ON pa.achievement_id = a.id WHERE pa.player_id = ?";
        Database.getInstance().executeQuery(query, new ResultSetProcessor() {
            @Override
            public void process(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    achievements
                            .add(new Achievement(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
                }
            }
        }, playerId);
        return achievements;
    }

    private String sanitize(String input) {
        return input.replaceAll("[^a-zA-Z0-9@.\\-_]", "");
    }
}