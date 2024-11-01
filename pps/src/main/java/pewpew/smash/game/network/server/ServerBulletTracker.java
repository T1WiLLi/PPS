package pewpew.smash.game.network.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pewpew.smash.game.entities.Bullet;

public class ServerBulletTracker {

    private static ServerBulletTracker instance;
    private final List<Bullet> bullets = new ArrayList<>();

    public static ServerBulletTracker getInstance() {
        if (instance == null) {
            instance = new ServerBulletTracker();
        }
        return instance;
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void removeBullet(Bullet bullet) {
        bullets.remove(bullet);
    }

    public void update() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.updateServer();

            if (bullet.getDistanceTraveled() >= bullet.getMaxRange()) {
                iterator.remove();
            }
        }
    }

    public List<Bullet> getActiveBullet() {
        return bullets;
    }
}
