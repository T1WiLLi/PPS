package pewpew.smash.game.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.geom.AffineTransform;

import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.utils.ResourcesLoader;

@ToString
public class Cycler extends UiElement {

    private BufferedImage swapSprite;
    private String[] cycles;
    private int currentIndex;

    private boolean isAnimating;
    private long animationStartTime;
    private static final long ANIMATION_DURATION = 300;
    private static final int TOTAL_ROTATION = -180;

    public Cycler(int x, int y, int w, int h, String[] cycles, String initialValue) {
        super(x, y, w, h);
        this.cycles = cycles;
        this.currentIndex = findInitialIndex(initialValue);
        this.isAnimating = false;
        loadSprites(ResourcesLoader.getImage(ResourcesLoader.MISC_PATH, "swap"));
    }

    @Override
    protected void loadSprites(BufferedImage spriteSheet) {
        BufferedImage coloredSprite = new BufferedImage(
                spriteSheet.getWidth(),
                spriteSheet.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = coloredSprite.createGraphics();
        g2d.drawImage(spriteSheet, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, spriteSheet.getWidth(), spriteSheet.getHeight());
        g2d.dispose();

        this.swapSprite = coloredSprite;
    }

    @Override
    public void update() {
        updateScaledBounds();
    }

    @Override
    public void render(Canvas canvas) {
        Graphics2D g2d = canvas.getGraphics2D();
        AffineTransform originalTransform = g2d.getTransform();

        try {
            int centerX = xPos + width / 2;
            int centerY = yPos + height / 2;

            if (isAnimating) {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - animationStartTime;

                if (elapsedTime >= ANIMATION_DURATION) {
                    isAnimating = false;
                } else {
                    float progress = (float) elapsedTime / ANIMATION_DURATION;
                    float currentRotation = progress * TOTAL_ROTATION;

                    g2d.rotate(Math.toRadians(currentRotation), centerX, centerY);
                }
            }

            canvas.renderImage(swapSprite, xPos, yPos, width, height);

        } finally {
            g2d.setTransform(originalTransform);
        }
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
            int newIndex = findInitialIndex(value);
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

    private int findInitialIndex(String initialValue) {
        for (int i = 0; i < cycles.length; i++) {
            if (cycles[i].equalsIgnoreCase(initialValue)) {
                return i;
            }
        }
        return 0;
    }
}