package pewpew.smash.engine.entities;

public abstract class UpdatableEntity extends StaticEntity {
    public abstract void updateClient();

    public abstract void updateServer(double deltaTime);
}
