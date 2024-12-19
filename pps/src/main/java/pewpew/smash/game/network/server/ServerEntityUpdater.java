package pewpew.smash.game.network.server;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import pewpew.smash.engine.controls.Direction;
import pewpew.smash.game.audio.AudioClip;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.MouseActionPacket;
import pewpew.smash.game.network.packets.PositionPacket;
import pewpew.smash.game.world.WorldGenerator;

public class ServerEntityUpdater {

    private final Map<Integer, Long> playerSoundCooldown = new ConcurrentHashMap<>();
    private static final long MOVEMENT_SOUND_COOLDOWN = 500;

    private final EntityManager entityManager;
    private final ServerCombatManager combatManager;
    private final byte[][] worldData;

    public ServerEntityUpdater(EntityManager entityManager, byte[][] worldData) {
        this.entityManager = entityManager;
        this.combatManager = new ServerCombatManager(entityManager);
        this.worldData = worldData;
    }

    public void update(ServerWrapper server) {
        entityManager.getPlayerEntities().forEach(player -> player.updateServer());
        entityManager.getMovableEntities().forEach(entity -> entity.updateServer());
        combatManager.updateCombat(server);
        playMovementSound();
    }

    public void sendPlayerPositions(ServerWrapper server) {
        entityManager.getPlayerEntities().forEach(player -> {
            PositionPacket packet = new PositionPacket(player.getId(), player.getX(), player.getY(),
                    player.getRotation());
            server.sendToAllUDP(packet);
        });
    }

    public void sendPlayerMouseInput(ServerWrapper server) {
        entityManager.getPlayerEntities().forEach(player -> {
            MouseActionPacket packet = new MouseActionPacket(player.getId(), player.getMouseInput());
            server.sendToAllUDP(packet);
        });
    }

    private void playMovementSound() {
        entityManager.getPlayerEntities().forEach(player -> {
            if (player.getDirection() != Direction.NONE) {
                long currentTime = System.currentTimeMillis();
                long lastSoundTime = playerSoundCooldown.getOrDefault(player.getId(), 0L);

                int playerX = player.getX() / WorldGenerator.TILE_SIZE;
                int playerY = player.getY() / WorldGenerator.TILE_SIZE;

                if (playerX >= 0 && playerX < worldData.length && playerY >= 0 && playerY < worldData[0].length) {
                    byte currentTile = worldData[playerX][playerY];

                    if (currentTile == WorldGenerator.WATER && currentTime - lastSoundTime > MOVEMENT_SOUND_COOLDOWN) {
                        ServerAudioManager.getInstance().play(AudioClip.WALKING_WATER, player, 1000, Optional.of(0.5));
                        playerSoundCooldown.put(player.getId(), currentTime);
                    } else if (currentTile == WorldGenerator.GRASS
                            && currentTime - lastSoundTime > MOVEMENT_SOUND_COOLDOWN) {
                        ServerAudioManager.getInstance().play(AudioClip.WALKING_GRASS, player, 1000, Optional.of(0.5));
                        playerSoundCooldown.put(player.getId(), currentTime);
                    }
                }
            }
        });
    }
}
