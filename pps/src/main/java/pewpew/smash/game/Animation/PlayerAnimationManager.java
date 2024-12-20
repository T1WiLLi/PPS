package pewpew.smash.game.Animation;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import pewpew.smash.game.utils.ResourcesLoader;

public class PlayerAnimationManager {
    private static final Map<String, BufferedImage[]> animations = new HashMap<>();
    private BufferedImage[] currentAnimation;
    private int currentIndex;
    private int frameTimer;
    private int animationSpeed;

    private PlayerAnimationState lastState;
    private PlayerAnimation lastAnimation;

    public PlayerAnimationManager(int defaultSpeed) {
        this.currentIndex = 0;
        this.frameTimer = 0;
        this.animationSpeed = defaultSpeed;
    }

    public static void preloadAllAnimations() {
        for (PlayerAnimationState state : PlayerAnimationState.values()) {
            String statePath = ResourcesLoader.PLAYER_SPRITE + "/" + state.name().toLowerCase();

            for (PlayerAnimation animation : PlayerAnimation.values()) {
                String animationPath = statePath + "/" + animation.name().toLowerCase();

                if (state == PlayerAnimationState.KNIFE && animation == PlayerAnimation.SHOOT) {
                    animationPath = statePath + "/meleeattack";
                }

                try {
                    loadAnimationFrames(animationPath, state, animation);
                } catch (Exception e) {
                    System.err.println("Failed to load animation: " + animationPath);
                }
            }
        }
    }

    public void updateAnimation(PlayerAnimationState state, PlayerAnimation animation) {
        if (state != lastState || animation != lastAnimation) {
            setCurrentAnimation(state, animation);
            lastState = state;
            lastAnimation = animation;
        }

        update();
    }

    public BufferedImage getFrame() {
        if (currentAnimation == null || currentAnimation.length == 0) {
            return null;
        }
        return currentAnimation[currentIndex];
    }

    private static void loadAnimationFrames(String animationPath, PlayerAnimationState state,
            PlayerAnimation animation) {
        String animationKey = state.name().toLowerCase() + "_" + animation.name().toLowerCase();
        animations.putIfAbsent(animationKey, new BufferedImage[0]);

        int index = 0;
        while (true) {
            String frameName = "survivor-" + animation.name().toLowerCase() + "_" + state.name().toLowerCase() + "_"
                    + index;
            BufferedImage frame = ResourcesLoader.getImage(animationPath, frameName);

            if (frame == null) {
                break;
            }

            BufferedImage[] frames = animations.get(animationKey);
            BufferedImage[] newFrames = new BufferedImage[frames.length + 1];
            System.arraycopy(frames, 0, newFrames, 0, frames.length);
            newFrames[frames.length] = frame;
            animations.put(animationKey, newFrames);

            index++;
        }
    }

    private void setCurrentAnimation(PlayerAnimationState state, PlayerAnimation animation) {
        String key = state.name().toLowerCase() + "_" + animation.name().toLowerCase();

        if (state == PlayerAnimationState.KNIFE && animation == PlayerAnimation.SHOOT) {
            key = state.name().toLowerCase() + "_meleeattack";
        }

        currentAnimation = animations.getOrDefault(key, new BufferedImage[0]);
        currentIndex = 0;
        frameTimer = 0;
    }

    private void update() {
        if (currentAnimation == null || currentAnimation.length == 0) {
            return;
        }

        frameTimer++;

        if (frameTimer >= animationSpeed) {
            frameTimer = 0;
            currentIndex = (currentIndex + 1) % currentAnimation.length;
        }
    }
}
