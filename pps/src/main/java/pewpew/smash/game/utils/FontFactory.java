package pewpew.smash.game.utils;

import pewpew.smash.engine.Canvas;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public enum FontFactory {
    DEFAULT_FONT(new Font("Segoe UI", Font.BOLD, 14)),
    SMALL_FONT(new Font("Verdana", Font.PLAIN, 12)),
    LARGE_FONT(new Font("Georgia", Font.BOLD, 24)),
    IMPACT_SMALL(new Font("Impact", Font.PLAIN, 18)),
    IMPACT_MEDIUM(new Font("Impact", Font.BOLD, 24)),
    IMPACT_LARGE(new Font("Impact", Font.BOLD, 36)),
    IMPACT_X_LARGE(new Font("Impact", Font.BOLD, 48));

    private Font font;

    private FontFactory(Font font) {
        this.font = font;
    }

    public void applyFont(Canvas canvas) {
        canvas.setFont(this.font);
    }

    public static void resetFont(Canvas canvas) {
        canvas.setFont(DEFAULT_FONT.getFont());
    }

    public int getFontWidth(String text, Canvas canvas) {
        Graphics2D g = canvas.getGraphics2D();
        if (g == null) {
            throw new IllegalStateException("Graphics context is not avaibable");
        }
        FontMetrics metrics = g.getFontMetrics(this.font);
        return metrics.stringWidth(text);
    }

    public Font getFont() {
        return this.font;
    }
}
