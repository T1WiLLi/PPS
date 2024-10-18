package pewpew.smash.game.network.packets;

import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

@NoArgsConstructor
public class PlayerJoinedPacket extends BasePacket {
    private byte[] username;

    public PlayerJoinedPacket(String username) {
        this.username = username.getBytes(StandardCharsets.UTF_8);
    }

    public String getUsername() {
        return new String(this.username, StandardCharsets.UTF_8);
    }
}
