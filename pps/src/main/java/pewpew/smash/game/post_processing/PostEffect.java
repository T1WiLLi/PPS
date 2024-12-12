package pewpew.smash.game.post_processing;

import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.Canvas;

public abstract class PostEffect {

    @Getter
    @Setter
    private EffectType type;

    public abstract void render(Canvas canvas);

    public abstract void trigger();
}
