package pewpew.smash.game.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.geom.AffineTransform;

import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.audio.AudioClip;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.audio.AudioPlayer.SoundType;
import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.utils.ResourcesLoader;

@ToString
public class Cycler extends UiElement {

    private BufferedImage swapSprite;
    private String[] cycles;
    private int currentIndex;

    private Runnable onCycle;

    private boolean isAnimating;
    private long animationStartTime;
    private static final long ANIMATION_DURATION = 300;
    private static final int TOTAL_ROTATION = -180;

    public Cycler(int x, int y, int w, int h, String[] cycles, String initialValue, Runnable onCycle) {
        super(x, y, w, h);
        this.onCycle = onCycle;
        this.cycles = cycles;
        this.currentIndex = findInitialIndex(initialValue);
        this.isAnimating = false;
        loadSprites(ResourcesLoader.getImage(ResourcesLoader.MISC_PATH, "swap"));
    }

    @Override
    protected void loadSprites(BufferedImage spriteSheet) {
        this.swapSprite = createColoredSprite(spriteSheet, Color.WHITE);
    }

    @Override
    public void update() {
        super.update();
        if (isAnimating) {
            updateAnimation();
        }
    }

    @Override
    public void render(Canvas canvas) {
        Graphics2D g2d = canvas.getGraphics2D();
        AffineTransform originalTransform = g2d.getTransform();

        try {
            applyAnimationTransform(g2d);
            canvas.renderImage(swapSprite, xPos, yPos, width, height);
        } finally {
            g2d.setTransform(originalTransform);
        }
    }

    @Override
    protected void handleMouseInput() {
        if (HelpMethods.isIn(bounds) && MouseController.isMousePressed() && !isAnimating) {
            nextCycle();
            onCycle.run();
            MouseController.consumeEvent();
            AudioPlayer.getInstance().play(AudioClip.SWAPPED, 0.95f, false, SoundType.UI);
        }
    }

    @Override
    protected void handleMouseMove() {
    }

    public String getCurrentCycle() {
        return cycles[currentIndex];
    }

    public void nextCycle() {
        if (!isAnimating) {
            currentIndex = (currentIndex + 1) % cycles.length;
            startAnimation();
        }
    }

    public void setValue(String value) {
        if (!isAnimating) {
            int newIndex = findCycleIndex(value);
            if (newIndex >= 0 && newIndex != currentIndex) {
                currentIndex = newIndex;
                startAnimation();
            }
        }
    }

    private void startAnimation() {
        isAnimating = true;
        animationStartTime = System.currentTimeMillis();
    }

    private void updateAnimation() {
        long elapsedTime = System.currentTimeMillis() - animationStartTime;
        if (elapsedTime >= ANIMATION_DURATION) {
            isAnimating = false;
        }
    }

    private int findInitialIndex(String initialValue) {
        int index = findCycleIndex(initialValue);
        return index >= 0 ? index : 0;
    }

    private int findCycleIndex(String value) {
        for (int i = 0; i < cycles.length; i++) {
            if (cycles[i].equalsIgnoreCase(value)) {
                return i;
            }
        }
        return -1;
    }

    private BufferedImage createColoredSprite(BufferedImage spriteSheet, Color color) {
        BufferedImage coloredSprite = new BufferedImage(
                spriteSheet.getWidth(),
                spriteSheet.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = coloredSprite.createGraphics();
        try {
            g2d.drawImage(spriteSheet, 0, 0, null);
            g2d.setComposite(AlphaComposite.SrcAtop);
            g2d.setColor(color);
            g2d.fillRect(0, 0, spriteSheet.getWidth(), spriteSheet.getHeight());
        } finally {
            g2d.dispose();
        }
        return coloredSprite;
    }

    private void applyAnimationTransform(Graphics2D g2d) {
        int centerX = xPos + width / 2;
        int centerY = yPos + height / 2;

        if (isAnimating) {
            long elapsedTime = System.currentTimeMillis() - animationStartTime;
            float progress = Math.min(1.0f, (float) elapsedTime / ANIMATION_DURATION);
            float currentRotation = progress * TOTAL_ROTATION;
            g2d.rotate(Math.toRadians(currentRotation), centerX, centerY);
        }
    }
}
