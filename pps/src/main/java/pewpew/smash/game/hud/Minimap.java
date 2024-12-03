package pewpew.smash.game.hud;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.Camera;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.event.StormEvent;
import pewpew.smash.game.utils.FontFactory;

@Setter
public class Minimap extends HudElement {

    private Player local;
    private BufferedImage worldImage;
    private Camera camera;

    // Battle Royale only !
    @Setter
    private StormEvent storm;

    public Minimap(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.camera = Camera.getInstance();
    }

    @Override
    protected void render(Canvas canvas) {
        if (worldImage == null || local == null) {
            return;
        }
        int visibleWidth = (int) (camera.getViewportWidth() * 2.5);
        int visibleHeight = (int) (camera.getViewportHeight() * 2.5);

        int startX = (int) local.getX() - visibleWidth / 2;
        int startY = (int) local.getY() - visibleHeight / 2;

        startX = Math.max(0, Math.min(startX, worldImage.getWidth() - visibleWidth));
        startY = Math.max(0, Math.min(startY, worldImage.getHeight() - visibleHeight));

        visibleWidth = Math.min(visibleWidth, worldImage.getWidth() - startX);
        visibleHeight = Math.min(visibleHeight, worldImage.getHeight() - startY);

        int minimapX = x;
        int minimapY = y - height;
        canvas.renderRectangle(minimapX, minimapY, width, height, Color.DARK_GRAY);
        canvas.renderRectangleBorder(minimapX, minimapY, width, height, 2, Color.WHITE);

        BufferedImage scaledMiniMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        scaledMiniMap.getGraphics().drawImage(
                worldImage.getSubimage(startX, startY, visibleWidth, visibleHeight),
                0, 0, width, height, null);
        canvas.renderImage(scaledMiniMap, minimapX, minimapY);

        int playerXOnMinimap = (int) ((local.getX() - startX) / (double) visibleWidth * width);
        int playerYOnMinimap = (int) ((local.getY() - startY) / (double) visibleHeight * height);
        int playerSizeOnMinimap = 5;

        canvas.renderCircle(
                minimapX + playerXOnMinimap - playerSizeOnMinimap / 2,
                minimapY + playerYOnMinimap - playerSizeOnMinimap / 2,
                playerSizeOnMinimap,
                Color.YELLOW);

        if (storm != null) {
            renderStormOnMinimap(canvas, minimapX, minimapY, startX, startY, visibleWidth, visibleHeight);
        }

        renderDirectionalLabels(canvas, minimapX, minimapY, width, height);
    }

    private void renderStormOnMinimap(Canvas canvas, int minimapX, int minimapY, int startX, int startY,
            int visibleWidth, int visibleHeight) {
        Area stormArea = storm.getStormArea();

        double scaleX = width / (double) visibleWidth;
        double scaleY = height / (double) visibleHeight;

        AffineTransform transform = new AffineTransform();
        transform.translate(minimapX - (startX * scaleX), minimapY - (startY * scaleY));
        transform.scale(scaleX, scaleY);

        Area scaledStormArea = stormArea.createTransformedArea(transform);
        Rectangle minimapBounds = new Rectangle(minimapX, minimapY, width, height);
        Area clippedStormArea = new Area(scaledStormArea);
        clippedStormArea.intersect(new Area(minimapBounds));

        canvas.renderArea(clippedStormArea, new Color(1f, 0, 0, 0.5f));
    }

    private void renderDirectionalLabels(Canvas canvas, int minimapX, int minimapY, int width, int height) {
        FontFactory.IMPACT_SMALL.applyFont(canvas);

        int centerX = minimapX + width / 2;
        int centerY = minimapY + height / 2;

        int northTextWidth = FontFactory.IMPACT_SMALL.getFontWidth("N", canvas);
        int northTextHeight = FontFactory.IMPACT_SMALL.getFontHeight(canvas);
        int southTextWidth = FontFactory.IMPACT_SMALL.getFontWidth("S", canvas);
        int eastTextWidth = FontFactory.IMPACT_SMALL.getFontWidth("E", canvas);

        canvas.renderString("N", centerX - northTextWidth / 2, minimapY + northTextHeight, Color.WHITE);
        canvas.renderString("S", centerX - southTextWidth / 2, minimapY + height - 5, Color.WHITE);
        canvas.renderString("W", minimapX + 5, centerY + northTextHeight / 2, Color.WHITE);
        canvas.renderString("E", minimapX + width - eastTextWidth - 5, centerY + northTextHeight / 2, Color.WHITE);
        FontFactory.resetFont(canvas);
    }

}
