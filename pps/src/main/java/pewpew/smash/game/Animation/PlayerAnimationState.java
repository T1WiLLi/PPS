package pewpew.smash.game.Animation;

public enum PlayerAnimationState {
    HANDGUN,
    KNIFE,
    RIFLE,
    SHOTGUN;

    private PlayerAnimationState current;

    public String getPathTo() {
        return current.name().toLowerCase();
    }
}
