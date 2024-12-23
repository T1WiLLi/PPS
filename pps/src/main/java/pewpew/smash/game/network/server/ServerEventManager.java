package pewpew.smash.game.network.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import pewpew.smash.game.audio.AudioClip;
import pewpew.smash.game.event.AirdropManager;
import pewpew.smash.game.event.StormManager;
import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.network.packets.BroadcastMessagePacket;
import pewpew.smash.game.network.packets.PlayerDeathPacket;
import pewpew.smash.game.network.packets.PlayerStatePacket;
import pewpew.smash.game.network.packets.StormEventCreationPacket;
import pewpew.smash.game.utils.HelpMethods;

public class ServerEventManager {

    private final GameModeType gameModeType;
    private StormManager stormManager;
    private AirdropManager airdropManager;
    private Map<Integer, Long> lastDamageTime;

    private byte[][] world;

    public ServerEventManager(GameModeType gameModeType, byte[][] world) {
        this.gameModeType = gameModeType;
        this.world = world;
    }

    public void update(ServerWrapper server, EntityManager entityManager) {
        switch (gameModeType) {
            case SANDBOX -> updateEventSandbox(server, entityManager);
            case BATTLE_ROYALE -> updateEventBattleRoyale(server, entityManager);
            case ARENA -> updateEventArena();
        }
    }

    public void initEvents(ServerWrapper server) {
        switch (gameModeType) {
            case SANDBOX -> {
                airdropManager = new AirdropManager();
            }
            case BATTLE_ROYALE -> {
                this.lastDamageTime = new HashMap<>();
                airdropManager = new AirdropManager();
                initializeStorm(server);
            }
            case ARENA -> System.out.println("Arena mode events are not implemented.");
        }
    }

    private void initializeStorm(ServerWrapper server) {
        stormManager = new StormManager();
        System.out.println("Storm initialized.");
        StormEventCreationPacket creationPacket = new StormEventCreationPacket(
                stormManager.getStormEvent().getCenterX(),
                stormManager.getStormEvent().getCenterY(),
                stormManager.getStormEvent().getRadius());
        server.sendToAllTCP(creationPacket);
    }

    private void updateEventSandbox(ServerWrapper server, EntityManager entityManager) {
        if (airdropManager != null) {
            airdropManager.update(server, entityManager, world);
        }
    }

    private void updateEventBattleRoyale(ServerWrapper server, EntityManager entityManager) {
        if (stormManager != null) {
            stormManager.update(server);
        }
        if (airdropManager != null) {
            airdropManager.update(server, entityManager, world);
        }

        checkForStormDamage(server, entityManager);
    }

    private void updateEventArena() {
        // No event logic for Arena mode
    }

    private void checkForStormDamage(ServerWrapper server, EntityManager entityManager) {
        long currentTime = System.currentTimeMillis();
        entityManager.getPlayerEntities().forEach(player -> {
            if (stormManager.isPlayerInStorm(player)) {
                long lastDamage = lastDamageTime.getOrDefault(player.getId(), 0L);
                if (currentTime - lastDamage >= 1000) {
                    ServerAudioManager.getInstance().play(AudioClip.PLAYER_DAMAGE, player, 1000, Optional.of(0.5));
                    player.setHealth(player.getHealth() - stormManager.getStormEvent().getHitDamage());
                    PlayerState newState = new PlayerState(player.getId(), player.getHealth());
                    PlayerStatePacket packet = new PlayerStatePacket(newState);
                    server.sendToUDP(player.getId(), packet);

                    lastDamageTime.put(player.getId(), currentTime);

                    if (player.getHealth() <= 0) {
                        entityManager.removePlayerEntity(player.getId());
                        String deathMessage = String.format("%s was killed by the storm!", player.getUsername());
                        BroadcastMessagePacket messagePacket = new BroadcastMessagePacket(deathMessage);
                        server.sendToAllTCP(messagePacket);

                        HelpMethods.dropInventoryOfDeadPlayer(player.getInventory(), server);

                        PlayerDeathPacket deathPacket = new PlayerDeathPacket(player.getId(),
                                entityManager.getRandomAlivePlayerID());
                        server.sendToAllTCP(deathPacket);
                        ServerAudioManager.getInstance().play(AudioClip.PLAYER_DEATH, player, 1500, Optional.empty());
                    }
                }
            }
        });
    }
}