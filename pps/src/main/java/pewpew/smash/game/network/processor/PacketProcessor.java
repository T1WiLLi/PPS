package pewpew.smash.game.network.processor;

import com.esotericsoftware.kryonet.Connection;

public interface PacketProcessor {

    void handle(Connection connection, Object packet);

    default void process(Connection connection, Object packet) {
        if (!isValidPacket(packet)) {
            return;
        }

        try {
            handle(connection, packet);
        } catch (Exception e) {
            handleError(e, connection, packet);
        }
    }

    default void handleError(Exception e, Connection connection, Object packet) {
        System.err.println("Error processing packet of type " + packet.getClass().getSimpleName()
                + " from connection ID " + connection.getID() + ": " + e.getMessage());
        e.printStackTrace();
    }

    default boolean isValidPacket(Object packet) {
        if (packet == null) {
            System.err.println("Received null packet. [Timestamp=" + System.currentTimeMillis() + "]");
            return false;
        }
        return true;
    }
}
