package pewpew.smash.game.post_processing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pewpew.smash.engine.Canvas;

public class PostProcessingManager {

    private static PostProcessingManager instance;
    private final List<PostEffect> effects;

    public static PostProcessingManager getInstance() {
        if (instance == null) {
            synchronized (PostProcessingManager.class) {
                if (instance == null) {
                    instance = new PostProcessingManager();
                }
            }
        }
        return instance;
    }

    public void addEffect(PostEffect effect) {
        effects.add(effect);
    }

    public List<PostEffect> getEffects(EffectType type) {
        return effects.stream()
                .filter(effect -> effect.getType() == type)
                .collect(Collectors.toList());
    }

    public void triggerEffect(EffectType type) {
        for (PostEffect effect : effects) {
            if (effect.getType() == type) {
                effect.trigger();
            }
        }
    }

    public void removeEffect(PostEffect effect) {
        effects.remove(effect);
    }

    public void render(Canvas canvas) {
        for (PostEffect effect : effects) {
            effect.render(canvas);
        }
    }

    private PostProcessingManager() {
        effects = new ArrayList<>();
        addEffect(new DamageEffect());
    }
}
