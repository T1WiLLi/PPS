package pewpew.smash.game.network.server;

import java.util.Timer;
import java.util.TimerTask;

import pewpew.smash.game.event.StormManager;
import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.network.packets.StormEventCreationPacket;

public class ServerEventManager {

    private final GameModeType gameModeType;
    private StormManager stormManager;

    public ServerEventManager(GameModeType gameModeType) {
        this.gameModeType = gameModeType;
    }

    public void update(ServerWrapper server) {
        switch (gameModeType) {
            case SANDBOX -> updateEventSandbox();
            case BATTLE_ROYALE -> updateEventBattleRoyale(server);
            case ARENA -> updateEventArena();
        }
    }

    public void initEvents(ServerWrapper server) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                switch (gameModeType) {
                    case SANDBOX -> System.out.println("Sandbox mode does not have events.");
                    case BATTLE_ROYALE -> initializeStorm(server);
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

    private void updateEventSandbox() {
        // No event logic for Sandbox mode
    }

    private void updateEventBattleRoyale(ServerWrapper server) {
        if (stormManager != null) {
            stormManager.update(server);
        }
    }

    private void updateEventArena() {
        // No event logic for Arena mode
    }
}
