package pewpew.smash.game.network.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

@NoArgsConstructor
public class PlayerJoinedPacket extends BasePacket {
    @Getter
    private int id;
    private byte[] username;

    public PlayerJoinedPacket(int id, String username) {
        this.id = id;
        this.username = username.getBytes(StandardCharsets.UTF_8);
    }

    public String getUsername() {
        return new String(this.username, StandardCharsets.UTF_8);
    }
}
