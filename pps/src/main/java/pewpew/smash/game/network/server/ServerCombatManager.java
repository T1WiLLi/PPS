package pewpew.smash.game.network.server;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.network.packets.PlayerStatePacket;
import pewpew.smash.game.objects.MeleeWeapon;

import java.awt.Polygon;

import java.util.Collection;

// TODO : FIX POLYGON INTERSECT COLLISION
public class ServerCombatManager {
    private final EntityManager entityManager;

    public ServerCombatManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void checkMeleeCombat(ServerWrapper server) {
        Collection<Player> players = this.entityManager.getPlayerEntities();

        players.forEach(player -> {
            if (player.getEquippedWeapon() instanceof MeleeWeapon) {
                MeleeWeapon weapon = (MeleeWeapon) player.getEquippedWeapon();
                if (weapon.isAttacking()) {
                    Polygon damageZone = getDamageZone(player, weapon);
                    checkDamage(player, damageZone, server);
                }
            }
        });
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
            if (target == attacker) {
                continue;
            }

            if (damageZone.intersects(target.getX(), target.getY(), target.getWidth(), target.getHealth())) {
                System.out.println("In zone, would get damage!");
                handleDamage(attacker, target, server);
            } else {
                System.out.println("Not in zone, no damage!");
            }
        }
    }

    private void handleDamage(Player attacker, Player target, ServerWrapper server) {
        int damage = attacker.getEquippedWeapon().getDamage();

        target.setHealth(target.getHealth() - damage);

        // Prepare a packet to send to the client
        PlayerState newState = new PlayerState(target.getId(), target.getHealth());
        PlayerStatePacket packet = new PlayerStatePacket(newState);

        // We only send the newState to the client if the target is the player
        server.sendToUDP(target.getId(), packet);
    }

}
