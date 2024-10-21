package pewpew.smash.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import pewpew.smash.engine.entities.StaticEntity;

public class CollidableRepository implements Iterable<StaticEntity> {
    private static CollidableRepository instance;

    private final List<StaticEntity> registeredEntities;

    public static CollidableRepository getInstance() {
        if (instance == null) {
            instance = new CollidableRepository();
        }
        return instance;
    }

    @Override
    public Iterator<StaticEntity> iterator() {
        return registeredEntities.iterator();
    }

    public int size() {
        return registeredEntities.size();
    }

    public void registerEntity(StaticEntity entity) {
        registeredEntities.add(entity);
    }

    public void registerEntities(Collection<StaticEntity> entities) {
        registeredEntities.addAll(entities);
    }

    public void unregisterEntity(StaticEntity entity) {
        registeredEntities.remove(entity);
    }

    public void unregisterEntities(Collection<StaticEntity> entities) {
        registeredEntities.removeAll(entities);
    }

    private CollidableRepository() {
        this.registeredEntities = new ArrayList<>();
    }
}
