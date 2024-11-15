package pewpew.smash.game.hud;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.Canvas;

public class CircleLoader extends HudElement {

    @Setter
    @Getter
    private float currentValue;

    public CircleLoader(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.currentValue = 0;
    }

    public void teleport(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(Canvas canvas) {
        int thickness = 2;
        int diameter = Math.min(width, height) - thickness;
        int centerX = x + thickness / 2;
        int centerY = y + thickness / 2;

        canvas.renderCircleBorder(new Ellipse2D.Float(centerX, centerY, diameter, diameter), thickness,
                new Color(200, 200, 200));
        int angle = (int) (currentValue * 3.6); // 360 degrees / 100%
        canvas.setColor(Color.BLACK);
        canvas.getGraphics2D().setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        canvas.getGraphics2D().drawArc(centerX, centerY, diameter, diameter, 90, -angle);
    }
}
