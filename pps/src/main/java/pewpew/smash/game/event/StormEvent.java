package pewpew.smash.game.event;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.world.WorldGenerator;

public class StormEvent {
    private int centerX, centerY;
    private int radius;
    private long stormStepDuration; // in milliseconds
    private int hitdamage = 2; // default is 2 damage per sec :)
    private Area stormArea;
    private Ellipse2D innerStorm;

    public StormEvent(int radius) {
        this.radius = radius;
        this.centerX = 1000;
        this.centerY = 1000;
        this.stormArea = new Area(new Rectangle(0, 0, WorldGenerator.getWorldWidth(),
                WorldGenerator.getWorldHeight()));
        this.innerStorm = getBounds();
        this.stormArea.subtract(new Area(innerStorm));
    }

    public void update() {

    }

    public void render(Canvas canvas) {
        Graphics2D g2d = canvas.getGraphics2D();

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.setColor(new Color(128, 0, 128));
        g2d.fill(stormArea);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        canvas.renderCircleBorder(getBounds(), 2, Color.RED);
    }

    public void setCenter(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.innerStorm = getBounds();
    }

    public Ellipse2D.Float getBounds() {
        return new Ellipse2D.Float(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }
}
