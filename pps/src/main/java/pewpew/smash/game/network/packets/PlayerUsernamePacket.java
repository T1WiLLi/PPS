package pewpew.smash.game.network.packets;

import java.nio.charset.StandardCharsets;

public class PlayerUsernamePacket extends BasePacket {
    private byte[] username;

    public PlayerUsernamePacket() {
        this.username = new byte[0];
    }

    public PlayerUsernamePacket(String username) {
        this.username = validateUsername(username).getBytes(StandardCharsets.UTF_8);
    }

    public String getUsername() {
        return new String(this.username, StandardCharsets.UTF_8);
    }

    private String validateUsername(String username) {
        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        return username;
    }
}
