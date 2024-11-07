package pewpew.smash.game.network.server;

// Will be used to manage 'event' such as 'Item generation', 'Storm', 'Broadcast', 'Special Event' basiaclly whatever... :)
public class ServerEventManager {
    public void runEvent(Runnable event) {
        event.run();
    }
}
