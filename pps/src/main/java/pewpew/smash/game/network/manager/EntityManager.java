package pewpew.smash.game.network.manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.world.entities.WorldBreakableStaticEntity;
import pewpew.smash.game.world.entities.WorldStaticEntity;

public class EntityManager {
    private final Map<Integer, MovableEntity> movableEntitiesMap;
    private final Map<Integer, StaticEntity> staticEntitiesMap;
    private final Map<Integer, Player> playerEntitiesMap;
    private final Map<Integer, Bullet> bulletEntitiesMap;
    private final Map<Integer, Player> deadPlayersMap;

    public EntityManager() {
        this.movableEntitiesMap = new ConcurrentHashMap<>();
        this.staticEntitiesMap = new ConcurrentHashMap<>();
        this.playerEntitiesMap = new ConcurrentHashMap<>();
        this.bulletEntitiesMap = new ConcurrentHashMap<>();
        this.deadPlayersMap = new ConcurrentHashMap<>();
    }

    public synchronized void addMovableEntity(int id, MovableEntity entity) {
        movableEntitiesMap.put(id, entity);
    }

    public synchronized void addStaticEntity(int id, StaticEntity entity) {
        staticEntitiesMap.put(id, entity);
    }

    public synchronized void addWorldStaticEntity(List<WorldStaticEntity> entities) {
        entities.forEach(entity -> staticEntitiesMap.put(entity.getId(), entity));
    }

    public synchronized void addPlayerEntity(int id, Player entity) {
        playerEntitiesMap.put(id, entity);
    }

    public synchronized void addBulletEntity(int id, Bullet bullet) {
        bulletEntitiesMap.put(id, bullet);
    }

    public synchronized MovableEntity removeMovableEntity(int id) {
        return movableEntitiesMap.remove(id);
    }

    public synchronized StaticEntity removeStaticEntity(int id) {
        return staticEntitiesMap.remove(id);
    }

    public synchronized Player removePlayerEntity(int id) {
        Player removedPlayer = playerEntitiesMap.remove(id);
        if (removedPlayer != null) {
            deadPlayersMap.put(id, removedPlayer);
        }
        return removedPlayer;
    }

    public synchronized Bullet removeBulletEntity(int id) {
        return bulletEntitiesMap.remove(id);
    }

    public synchronized MovableEntity getMovableEntity(int id) {
        return movableEntitiesMap.get(id);
    }

    public synchronized StaticEntity getStaticEntity(int id) {
        return staticEntitiesMap.get(id);
    }

    public synchronized Player getPlayerEntity(int id) {
        Player player = playerEntitiesMap.get(id);
        if (player == null) {
            player = deadPlayersMap.get(id);
        }
        return player;
    }

    public synchronized int getRandomAlivePlayerID() {
        Random r = new Random();
        List<Integer> alivePlayerIDs = new ArrayList<>(playerEntitiesMap.keySet());
        return alivePlayerIDs.get(r.nextInt(alivePlayerIDs.size()));
    }

    public synchronized Bullet getBulletEntity(int id) {
        return bulletEntitiesMap.get(id);
    }

    public boolean containsMovableEntity(int id) {
        return movableEntitiesMap.containsKey(id);
    }

    public boolean containsStaticEntity(int id) {
        return staticEntitiesMap.containsKey(id);
    }

    public boolean containsPlayerEntity(int id) {
        return playerEntitiesMap.containsKey(id);
    }

    public boolean containsBulletEntity(int id) {
        return bulletEntitiesMap.containsKey(id);
    }

    public List<StaticEntity> getAllEntities() {
        List<StaticEntity> allEntities = new ArrayList<>();
        allEntities.addAll(staticEntitiesMap.values());
        allEntities.addAll(movableEntitiesMap.values());
        allEntities.addAll(playerEntitiesMap.values());
        return allEntities;
    }

    public void clearAllEntities() {
        staticEntitiesMap.clear();
        movableEntitiesMap.clear();
        playerEntitiesMap.clear();
        deadPlayersMap.clear();
    }

    public List<MovableEntity> getMovableEntities() {
        return new ArrayList<>(movableEntitiesMap.values());
    }

    public List<StaticEntity> getStaticEntities() {
        return new ArrayList<>(staticEntitiesMap.values());
    }

    public List<Player> getPlayerEntities() {
        return new ArrayList<>(playerEntitiesMap.values());
    }

    public List<Bullet> getBulletEntities() {
        return new ArrayList<>(bulletEntitiesMap.values());
    }

    public List<WorldStaticEntity> getWorldStaticEntities() {
        return staticEntitiesMap.values().stream()
                .sequential()
                .filter(entity -> entity instanceof WorldStaticEntity)
                .map(entity -> (WorldStaticEntity) entity)
                .collect(Collectors.toList());
    }

    public List<WorldBreakableStaticEntity> gettWorldBreakableStaticEntities() {
        return staticEntitiesMap.values().stream()
                .sequential()
                .filter(entity -> entity instanceof WorldBreakableStaticEntity)
                .map(entity -> (WorldBreakableStaticEntity) entity)
                .collect(Collectors.toList());
    }

    public List<Player> getAllPlayers() {
        List<Player> allPlayers = new ArrayList<>(playerEntitiesMap.values());
        allPlayers.addAll(deadPlayersMap.values());
        return allPlayers;
    }

    public int getNextID(Class<?> type) {
        return switch (type.getSimpleName()) {
            case "Player" -> playerEntitiesMap.values().stream().sequential()
                    .max(Comparator.comparingInt(Player::getId))
                    .map(player -> player.getId() + 1)
                    .orElse(0);
            case "MovableEntity" -> movableEntitiesMap.values().stream().sequential()
                    .max(Comparator.comparingInt(MovableEntity::getId))
                    .map(entity -> entity.getId() + 1)
                    .orElse(0);
            case "StaticEntity" -> staticEntitiesMap.values().stream().sequential()
                    .max(Comparator.comparingInt(StaticEntity::getId))
                    .map(entity -> entity.getId() + 1)
                    .orElse(0);
            case "Bullet" -> bulletEntitiesMap.values().stream().sequential()
                    .max(Comparator.comparingInt(Bullet::getId))
                    .map(bullet -> bullet.getId() + 1)
                    .orElse(0);
            default -> throw new IllegalArgumentException("Unsupported entity type: " + type.getSimpleName());
        };
    }

    public String sizeToString() {
        return "Player[" + playerEntitiesMap.size() + "] & MovableEntity[" + movableEntitiesMap.size()
                + "] & StaticEntity[" + staticEntitiesMap.size() + "] & Bullet[" + bulletEntitiesMap.size() + "]";
    }
}
