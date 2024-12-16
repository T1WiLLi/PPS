package pewpew.smash.game.network.server;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import pewpew.smash.game.audio.AudioClip;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.AudioPacket;

public class ServerAudioManager {

    @Getter
    private static ServerAudioManager instance = new ServerAudioManager();

    private static final int FADE_THRESHOLD = 800;

    private ServerWrapper server;
    private EntityManager entityManager;

    private final ExecutorService audioPool;

    public ServerAudioManager() {
        this.audioPool = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
    }

    public void setDependencies(ServerWrapper server, EntityManager entityManager) {
        this.server = server;
        this.entityManager = entityManager;
    }

    public void play(AudioClip clip, Player source, int maxRadius, Optional<Double> volumeDamper) {
        if (server == null || entityManager == null) {
            throw new IllegalStateException("ServerAudioManager dependencies are not set.");
        }

        audioPool.execute(() -> {
            for (Player targetPlayer : entityManager.getPlayerEntities()) {
                if (isInRadius(source, targetPlayer, maxRadius)) {
                    double distance = calculateDistance(source.getX(), source.getY(), targetPlayer.getX(),
                            targetPlayer.getY());
                    double volume = calculateFadeVolume(distance, maxRadius)
                            - (volumeDamper.isPresent() ? volumeDamper.get() : 0);
                    double pan = calculatePan(source.getX(), targetPlayer, maxRadius);
                    AudioPacket packet = new AudioPacket(clip, volume, pan);
                    server.sendToTCP(targetPlayer.getId(), packet);
                }
            }
        });
    }

    public void play(AudioClip clip, int[] pos, int maxRadius) {
        if (server == null || entityManager == null) {
            throw new IllegalStateException("ServerAudioManager dependencies are not set.");
        }

        audioPool.execute(() -> {
            for (Player targetPlayer : entityManager.getPlayerEntities()) {
                if (isInRadius(pos, targetPlayer, maxRadius)) {
                    double distance = calculateDistance(pos[0], pos[1], targetPlayer.getX(),
                            targetPlayer.getY());
                    double volume = calculateFadeVolume(distance, maxRadius);
                    double pan = calculatePan(pos[0], targetPlayer, maxRadius);

                    AudioPacket packet = new AudioPacket(clip, volume, pan);
                    server.sendToTCP(targetPlayer.getId(), packet);
                }
            }
        });
    }

    public void stop() {
        audioPool.shutdownNow();
    }

    private boolean isInRadius(Player source, Player target, int maxRadius) {
        double distance = calculateDistance(source.getX(), source.getY(), target.getX(), target.getY());
        return distance <= maxRadius;
    }

    private boolean isInRadius(int[] pos, Player target, int maxRadius) {
        double distance = calculateDistance(pos[0], pos[1], target.getX(), target.getY());
        return distance <= maxRadius;
    }

    private double calculateFadeVolume(double distance, int maxRadius) {
        if (distance > FADE_THRESHOLD) {
            double relativeDistance = distance - FADE_THRESHOLD;
            double maxFadeRange = maxRadius - FADE_THRESHOLD;
            return Math.max(0, 1 - (relativeDistance / maxFadeRange));
        }
        return 1.0;
    }

    private double calculatePan(int sourceX, Player target, int maxRadius) {
        double dx = target.getX() - sourceX;
        double pan = -dx / maxRadius; // -1 (left) & 1 (right)
        return Math.max(-1.0, Math.min(1.0, pan));
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
