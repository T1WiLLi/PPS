package pewpew.smash.game.network.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pewpew.smash.game.audio.AudioClip;
import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.network.packets.BulletCreatePacket;
import pewpew.smash.game.network.packets.BulletRemovePacket;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;
import pewpew.smash.game.objects.RangedWeapon;

public class ServerBulletTracker {

    private ServerWrapper server;

    private static final ServerBulletTracker instance = new ServerBulletTracker();
    private final Map<Integer, Bullet> bullets = new ConcurrentHashMap<>();
    private int nextBulletId = 0;

    public static ServerBulletTracker getInstance() {
        return instance;
    }

    public void setServerReference(ServerWrapper server) {
        this.server = server;
    }

    public void addBullet(Bullet bullet, RangedWeapon weaponFrom) {
        int bulletId = nextBulletId++;
        bullet.setId(bulletId);
        bullets.put(bulletId, bullet);
        BulletCreatePacket packet = new BulletCreatePacket(
                bulletId,
                bullet.getX(),
                bullet.getY(),
                bullet.getPlayerOwnerID());
        server.sendToAllTCP(packet);
        server.sendToAllTCP(WeaponStateSerializer.serializeWeaponState(weaponFrom));
    }

    public void removeBullet(Bullet bullet, AudioClip sound) {
        bullets.remove(bullet.getId());
        server.sendToAllTCP(new BulletRemovePacket(bullet.getId()));
        ServerAudioManager.getInstance().play(sound,
                new int[] { (int) bullet.getX(), (int) bullet.getY() },
                800);
    }

    public void update(ServerWrapper server) {
        Iterator<Map.Entry<Integer, Bullet>> iterator = bullets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Bullet> entry = iterator.next();
            Bullet bullet = entry.getValue();

            bullet.updateServer();
            if (bullet.getDistanceTraveled() > bullet.getMaxRange()) {
                server.sendToAllTCP(new BulletRemovePacket(entry.getKey()));
                iterator.remove();
                ServerAudioManager.getInstance().play(AudioClip.BULLET_EXPLODE,
                        new int[] { (int) bullet.getX(), (int) bullet.getY() },
                        800);
            }
        }
    }

    public Collection<Bullet> getBullets() {
        return bullets.values();
    }
}
