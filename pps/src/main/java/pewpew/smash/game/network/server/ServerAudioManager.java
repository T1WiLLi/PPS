package pewpew.smash.game.network.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pewpew.smash.game.audio.AudioClip;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;

public class ServerAudioManager {
    private static final int MAX_AUDIO_RADIUS = 1500; // Pixels around the player, fade-in from the original point.
    private static final int FADE_THRESHOLD = 800;

    private final ServerWrapper server;
    private final EntityManager entityManager;

    private final ExecutorService audioPool;

    public ServerAudioManager(ServerWrapper server, EntityManager entityManager) {
        this.server = server;
        this.entityManager = entityManager;
        this.audioPool = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
    }

    public void update() {

    }

    private boolean isInRadius(Player source, Player target) {
        double distance = calculateDistance(source.getX(), source.getY(), target.getX(), target.getY());
        return distance <= MAX_AUDIO_RADIUS;
    }

    private int getFadeDistance(Player source, Player target) {
        double distance = calculateDistance(source.getX(), source.getY(), target.getX(), target.getY());
        // Transform the distance to be a volume amount, closer to 1500, less sound,
        // 1500 being no sound. and 0 being max sound. but with a threshold of 800,
        // meaning we only start fading at 800, and not 0.
        return 0;
    }

    private void sendAudioToBePlayed(AudioClip clip, int id) {

    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
