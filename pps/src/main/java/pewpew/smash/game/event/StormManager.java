package pewpew.smash.game.event;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.packets.StormStatePacket;
import pewpew.smash.game.network.server.ServerWrapper;
import pewpew.smash.game.network.server.ServerTime;

public class StormManager {

    private final StormEvent stormEvent;

    public StormManager() {
        stormEvent = new StormEvent(StormStage.PRE_INITIAL.getTargetRadius(), StormStage.PRE_INITIAL);
    }

    public StormEvent getStormEvent() {
        return stormEvent;
    }

    public void update(ServerWrapper server) {
        long currentTime = ServerTime.getInstance().getElapsedTimeMillis();
        if (stormEvent.getCurrentStage().hasNext() &&
                currentTime >= stormEvent.getCurrentStage().next().getStartTime()) {
            transitionToNextStage();
        }

        stormEvent.update();
        broadcastStormState(server);
    }

    public boolean isPlayerInStorm(Player player) {
        return !stormEvent.isInside(player);
    }

    private void transitionToNextStage() {
        StormStage nextStage = stormEvent.getCurrentStage().next();
        stormEvent.transitionToStage(nextStage);
        System.out.println("Storm transitioned to stage: " + nextStage);
    }

    private void broadcastStormState(ServerWrapper server) {
        StormStatePacket packet = new StormStatePacket(stormEvent.toStormState());
        server.sendToAllTCP(packet);
    }
}
