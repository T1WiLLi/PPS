package pewpew.smash.game.network.processor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.packets.BasePacket;

public interface PacketProcessor<T extends BasePacket> {

    void handle(Connection connection, T packet);

    default void process(Connection connection, T packet) {
        if (!isValidPacket(packet)) {
            return;
        }

        try {
            handle(connection, packet);
        } catch (Exception e) {
            handleError(e, connection, packet);
        }
    }

    default void handleError(Exception e, Connection connection, T packet) {
        System.err.println("Error processing packet of type " + packet.getClass().getSimpleName()
                + " from connection ID " + connection.getID() + ": " + e.getMessage());
        e.printStackTrace();
    }

    default boolean isValidPacket(T packet) {
        if (packet == null) {
            System.err.println("Received null packet. [Timestamp=" + System.currentTimeMillis() + "]");
            return false;
        }
        return true;
    }
}
