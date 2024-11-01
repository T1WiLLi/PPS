package pewpew.smash.game.network.server;

import java.awt.Polygon;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.network.packets.BroadcastMessagePacket;
import pewpew.smash.game.network.packets.PlayerDeathPacket;
import pewpew.smash.game.network.packets.PlayerStatePacket;
import pewpew.smash.game.objects.MeleeWeapon;

public class ServerCombatManager {
    private final EntityManager entityManager;
    private final ServerBulletTracker bulletTracker;
    private final Map<Player, Boolean> damageDealtMap = new HashMap<>();

    public ServerCombatManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.bulletTracker = new ServerBulletTracker();
    }

    public void updateCombat(ServerWrapper server) {
        checkMeleeCombat(server);
        checkRangedCombat(server);
    }

    private void checkMeleeCombat(ServerWrapper server) {
        Collection<Player> players = this.entityManager.getPlayerEntities();

        players.forEach(player -> {
            if (player.getEquippedWeapon() instanceof MeleeWeapon) {
                MeleeWeapon weapon = (MeleeWeapon) player.getEquippedWeapon();
                if (weapon.isAttacking()) {
                    Polygon damageZone = getDamageZone(player, weapon);
                    if (!damageDealtMap.getOrDefault(player, false)) {
                        checkDamage(player, damageZone, server);
                    }
                } else {
                    damageDealtMap.put(player, false); // Reset when not attacking
                }
            }
        });
    }

    private void checkRangedCombat(ServerWrapper server) {
        bulletTracker.update();

        for (Bullet bullet : bulletTracker.getActiveBullet()) {
            for (Player targetPlayer : entityManager.getPlayerEntities()) {
                if (targetPlayer.getId() != bullet.getPlayerOwnerID()
                        && bullet.getHitbox().intersects(targetPlayer.getX(), targetPlayer.getY(),
                                targetPlayer.getWidth(), targetPlayer.getHeight())) {
                    handleBulletDamage(bullet, targetPlayer, server);
                    bulletTracker.removeBullet(bullet);
                }
            }
        }
    }

    private Polygon getDamageZone(Player player, MeleeWeapon weapon) {
        double angleRad = Math.toRadians(player.getRotation());

        int centerX = player.getX() + player.getWidth() / 2;
        int centerY = player.getY() + player.getHeight() / 2;

        double baseCenterX = centerX + (weapon.getRange() * 1.6) * Math.cos(angleRad);
        double baseCenterY = centerY + (weapon.getRange() * 1.6) * Math.sin(angleRad);

        int basePoint1X = (int) (baseCenterX + ((20 * 2) / 1.4) * -Math.sin(angleRad));
        int basePoint1Y = (int) (baseCenterY + ((20 * 2) / 1.4) * Math.cos(angleRad));

        int basePoint2X = (int) (baseCenterX - ((20 * 2) / 1.4) * -Math.sin(angleRad));
        int basePoint2Y = (int) (baseCenterY - ((20 * 2) / 1.4) * Math.cos(angleRad));

        Polygon zone = new Polygon();
        zone.addPoint(centerX, centerY);
        zone.addPoint(basePoint1X, basePoint1Y);
        zone.addPoint(basePoint2X, basePoint2Y);
        return zone;
    }

    private void checkDamage(Player attacker, Polygon damageZone, ServerWrapper server) {
        Collection<Player> players = this.entityManager.getPlayerEntities();

        for (Player target : players) {
            if (target != attacker) {
                if (damageZone.intersects(target.getX(), target.getY(), target.getWidth(), target.getHeight())) {
                    handleDamage(attacker, target, server);
                }
            }
        }
    }

    private void handleDamage(Player attacker, Player target, ServerWrapper server) {
        int damage = attacker.getEquippedWeapon().getDamage();
        target.setHealth(target.getHealth() - damage);

        damageDealtMap.put(attacker, true);

        PlayerState newState = new PlayerState(target.getId(), target.getHealth());
        PlayerStatePacket packet = new PlayerStatePacket(newState);
        server.sendToUDP(target.getId(), packet);

        if (target.getHealth() <= 0) {
            handlePlayerDeath(attacker, target, server);
        }
    }

    private void handleBulletDamage(Bullet bullet, Player target, ServerWrapper server) {
        int damage = bullet.getDamage();
        target.setHealth(target.getHealth() - damage);

        PlayerState newState = new PlayerState(target.getId(), target.getHealth());
        PlayerStatePacket packet = new PlayerStatePacket(newState);
        server.sendToUDP(target.getId(), packet);

        if (target.getHealth() <= 0) {
            handlePlayerDeath(entityManager.getPlayerEntity(bullet.getPlayerOwnerID()), target, server);
        }
    }

    private void handlePlayerDeath(Player attacker, Player target, ServerWrapper server) {
        entityManager.removePlayerEntity(target.getId());

        String deathMessage = String.format("%s was killed by %s using %s", target.getUsername(),
                attacker.getUsername(), attacker.getEquippedWeapon().getName());
        BroadcastMessagePacket messagePacket = new BroadcastMessagePacket(deathMessage);
        server.sendToAllTCP(messagePacket);

        PlayerDeathPacket deathPacket = new PlayerDeathPacket(target.getId(), attacker.getId());
        server.sendToAllTCP(deathPacket);
    }
}
