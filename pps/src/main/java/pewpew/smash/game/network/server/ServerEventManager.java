package pewpew.smash.game.network.server;

import pewpew.smash.game.event.StormEvent;
import pewpew.smash.game.event.StormStage;
import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.network.model.StormState;
import pewpew.smash.game.network.packets.StormStatePacket;
import pewpew.smash.game.world.WorldGenerator;

// Will be used to manage 'event' such as 'Item generation', 'Storm', 'Broadcast', 'Special Event' basiaclly whatever... :)
public class ServerEventManager {

    private final GameModeType type;

    // Battle Royale event
    private StormEvent stormEvent;

    private long lastEventUpdateTime;

    public ServerEventManager(GameModeType type) {
        this.type = type;
        initEvents();
    }

    public void update(ServerWrapper server) {
        long currentTime = ServerTime.getInstance().getElapsedTimeMillis();

        switch (type) {
            case SANDBOX -> updateEventSandbox();
            case BATTLE_ROYALE -> updateEventBattleRoyale(server, currentTime);
            case ARENA -> updateEventArena();
        }
    }

    private void updateEventSandbox() {

    }

    private void updateEventBattleRoyale(ServerWrapper server, long currentTime) {
        if (currentTime > 1000 * 10 && stormEvent == null) {
            initializeStorm(server);
        }

        if (stormEvent == null) {
            return;
        }

        stormEvent.update();

        if (stormEvent.getCurrentStage() != null
                && currentTime - lastEventUpdateTime >= stormEvent.getStageDuration(stormEvent.getCurrentStage())) {
            if (stormEvent.getCurrentStage().hasNext()) {
                stormEvent.transitionToNextStage();
                lastEventUpdateTime = currentTime;

                StormStatePacket packet = new StormStatePacket(new StormState(
                        stormEvent.getCenterX(),
                        stormEvent.getCenterY(),
                        stormEvent.getRadius(),
                        stormEvent.getCurrentStage()));
                server.sendToAllTCP(packet);
            }
        }
    }

    private void updateEventArena() {

    }

    private void initEvents() {
        switch (type) {
            case SANDBOX -> {
                System.out.println("Event of sandbox !");
            }
            case BATTLE_ROYALE -> {

            }
            case ARENA -> {

            }
        }
        ;
    }

    private void initializeStorm(ServerWrapper server) {
        System.out.println("Storm Init");
        stormEvent = new StormEvent(WorldGenerator.getWorldWidth() / 2, StormStage.STAGE_1);
        StormStatePacket packet = new StormStatePacket(new StormState(
                stormEvent.getCenterX(),
                stormEvent.getCenterY(),
                stormEvent.getRadius(),
                stormEvent.getCurrentStage()));
        server.sendToAllTCP(packet);
    }
}
