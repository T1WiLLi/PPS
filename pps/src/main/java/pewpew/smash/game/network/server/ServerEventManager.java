package pewpew.smash.game.network.server;

import java.util.Timer;
import java.util.TimerTask;

import pewpew.smash.game.event.AirdropManager;
import pewpew.smash.game.event.StormManager;
import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.StormEventCreationPacket;

public class ServerEventManager {

    private final GameModeType gameModeType;
    private StormManager stormManager;
    private AirdropManager airdropManager;

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
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                switch (gameModeType) {
                    case SANDBOX -> {
                        airdropManager = new AirdropManager();
                    }
                    case BATTLE_ROYALE -> {
                        airdropManager = new AirdropManager();
                        initializeStorm(server);
                    }
                    case ARENA -> System.out.println("Arena mode events are not implemented.");
                }
            }
        }, 100);
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
    }

    private void updateEventArena() {
        // No event logic for Arena mode
    }
}
