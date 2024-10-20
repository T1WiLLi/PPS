package pewpew.smash.game.network.manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.engine.entities.UpdatableEntity;
import pewpew.smash.game.entities.Player;

public class EntityManager {
    private final Map<Integer, UpdatableEntity> updatableEntitiesMap;
    private final Map<Integer, MovableEntity> movableEntitiesMap;
    private final Map<Integer, Player> playerEntitiesMap;

    public EntityManager() {
        this.updatableEntitiesMap = new ConcurrentHashMap<>();
        this.movableEntitiesMap = new ConcurrentHashMap<>();
        this.playerEntitiesMap = new ConcurrentHashMap<>();
    }

    public void addUpdatableEntity(int id, UpdatableEntity entity) {
        updatableEntitiesMap.put(id, entity);
    }

    public void addMovableEntity(int id, MovableEntity entity) {
        movableEntitiesMap.put(id, entity);
    }

    public void addPlayerEntity(int id, Player entity) {
        playerEntitiesMap.put(id, entity);
    }

    public UpdatableEntity removeUpdatableEntity(int id) {
        return updatableEntitiesMap.remove(id);
    }

    public MovableEntity removeMovableEntity(int id) {
        return movableEntitiesMap.remove(id);
    }

    public Player removePlayerEntity(int id) {
        return playerEntitiesMap.remove(id);
    }

    public UpdatableEntity getUpdatableEntity(int id) {
        return updatableEntitiesMap.get(id);
    }

    public MovableEntity getMovableEntity(int id) {
        return movableEntitiesMap.get(id);
    }

    public Player getPlayerEntity(int id) {
        return playerEntitiesMap.get(id);
    }

    public boolean containsUpdatableEntity(int id) {
        return updatableEntitiesMap.containsKey(id);
    }

    public boolean containsMovableEntity(int id) {
        return movableEntitiesMap.containsKey(id);
    }

    public boolean containsPlayerEntity(int id) {
        return playerEntitiesMap.containsKey(id);
    }

    public synchronized void clearAllEntities() {
        updatableEntitiesMap.clear();
        movableEntitiesMap.clear();
        playerEntitiesMap.clear();
    }

    public synchronized Iterator<UpdatableEntity> updatableEntitiesIterator() {
        return updatableEntitiesMap.values().iterator();
    }

    public synchronized Iterator<MovableEntity> movableEntitiesIterator() {
        return movableEntitiesMap.values().iterator();
    }

    public synchronized Iterator<Player> playerEntitiesIterator() {
        return playerEntitiesMap.values().iterator();
    }
}
