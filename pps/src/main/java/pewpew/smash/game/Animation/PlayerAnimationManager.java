package pewpew.smash.game.Animation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pewpew.smash.game.utils.ResourcesLoader;

public class PlayerAnimationManager {
    // Cache for animations (unbounded, but optimized for reuse)
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
            for (PlayerAnimation animation : PlayerAnimation.values()) {
                String animationPath = ResourcesLoader.PLAYER_SPRITE + "/" + state.name().toLowerCase() + "/"
                        + animation.name().toLowerCase();

                if (state == PlayerAnimationState.KNIFE && animation == PlayerAnimation.SHOOT) {
                    animationPath = ResourcesLoader.PLAYER_SPRITE + "/" + state.name().toLowerCase() + "/meleeattack";
                }

                loadAnimationFrames(animationPath, state, animation);
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
        String animationKey = getAnimationKey(state, animation);

        if (animations.containsKey(animationKey)) {
            return;
        }

        List<BufferedImage> frameList = new ArrayList<>();
        int index = 0;

        while (true) {
            String frameName = "survivor-" + animation.name().toLowerCase() + "_" + state.name().toLowerCase() + "_"
                    + index;
            BufferedImage frame = ResourcesLoader.getImage(animationPath, frameName);

            if (frame == null) {
                break; // No more frames to load
            }

            frameList.add(frame);
            index++;
        }

        animations.put(animationKey, frameList.toArray(new BufferedImage[0]));
    }

    private void setCurrentAnimation(PlayerAnimationState state, PlayerAnimation animation) {
        String key = getAnimationKey(state, animation);

        if (state == PlayerAnimationState.KNIFE && animation == PlayerAnimation.SHOOT) {
            key = state.name().toLowerCase() + "_meleeattack";
        }

        currentAnimation = animations.computeIfAbsent(key, k -> loadAnimationFramesLazy(state, animation));
        currentIndex = 0;
        frameTimer = 0;
    }

    private static String getAnimationKey(PlayerAnimationState state, PlayerAnimation animation) {
        return state.name().toLowerCase() + "_" + animation.name().toLowerCase();
    }

    private static BufferedImage[] loadAnimationFramesLazy(PlayerAnimationState state, PlayerAnimation animation) {
        String animationPath = ResourcesLoader.PLAYER_SPRITE + "/" + state.name().toLowerCase() + "/"
                + animation.name().toLowerCase();
        List<BufferedImage> frameList = new ArrayList<>();
        int index = 0;

        while (true) {
            String frameName = "survivor-" + animation.name().toLowerCase() + "_" + state.name().toLowerCase() + "_"
                    + index;
            BufferedImage frame = ResourcesLoader.getImage(animationPath, frameName);

            if (frame == null) {
                break; // No more frames to load
            }

            frameList.add(frame);
            index++;
        }

        return frameList.toArray(new BufferedImage[0]);
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