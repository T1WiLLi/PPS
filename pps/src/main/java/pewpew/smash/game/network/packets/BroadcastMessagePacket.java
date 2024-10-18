package pewpew.smash.game.network.packets;

import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

@NoArgsConstructor
public class BroadcastMessagePacket extends BasePacket {
    private byte[] message;

    public BroadcastMessagePacket(String message) {
        this.message = validateMessage(message).getBytes(StandardCharsets.UTF_8);
    }

    public String getMessage() {
        return new String(this.message, StandardCharsets.UTF_8);
    }

    private String validateMessage(String message) {
        if (message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        return message;
    }
}
