package pewpew.smash.engine.entities;

import lombok.ToString;

@ToString(callSuper = true)
public abstract class UpdatableEntity extends StaticEntity {
    public abstract void updateClient();

    public abstract void updateServer();
}
