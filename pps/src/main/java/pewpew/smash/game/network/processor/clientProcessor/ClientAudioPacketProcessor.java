package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.AudioPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientAudioPacketProcessor extends ClientProcessor implements PacketProcessor<AudioPacket> {

    public ClientAudioPacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, AudioPacket packet) {
        AudioPlayer.getInstance().playWithSpatialProperties(packet.getClip(), packet.getVolume(), packet.getPan(),
                false);
    }
}
