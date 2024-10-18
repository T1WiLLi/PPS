package pewpew.smash.game.network.packets;

import java.io.Serializable;

import lombok.Getter;

@Getter
public abstract class BasePacket implements Serializable {
    private long timestamp;
    private String packetID;

    public BasePacket() {
        this.timestamp = System.currentTimeMillis();
        this.packetID = generatePacketID();
    }

    private String generatePacketID() {
        return getClass().getSimpleName() + "-" + System.nanoTime();
    }
}
