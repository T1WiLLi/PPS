package pewpew.smash.game.post_processing;

import java.awt.Color;

import pewpew.smash.engine.Canvas;

public class DamageEffect extends PostEffect {

    private float alpha;
    private final float fadeSpeed;

    public DamageEffect() {
        this.alpha = 0.0f;
        this.fadeSpeed = 0.05f;
        setType(EffectType.ON_DAMAGE);
    }

    @Override
    public void trigger() {
        alpha = 1.0f;
    }

    @Override
    public void render(Canvas canvas) {
        if (alpha > 0) {
            canvas.setTransparency(alpha);
            canvas.renderRectangle(0, 0, (int) canvas.getGraphics2D().getDeviceConfiguration().getBounds().getWidth(),
                    (int) canvas.getGraphics2D().getDeviceConfiguration().getBounds().getHeight(),
                    new Color(255, 0, 0, 150));
            canvas.resetTransparency();

            alpha -= fadeSpeed;
            if (alpha < 0) {
                alpha = 0; // Ensure alpha is never negative
            }
        }
    }
}
