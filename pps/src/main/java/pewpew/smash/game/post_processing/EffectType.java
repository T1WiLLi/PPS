package pewpew.smash.game.post_processing;

public enum EffectType {
    NONE, // No special trigger
    ALWAYS, // Always trigger
    ON_DAMAGE, // Trigger on damage
    ON_KILL, // Trigger on kill
    ON_DEATH; // Trigger on death
}
