package pewpew.smash.game.hud;

import java.awt.Color;
import java.awt.image.BufferedImage;

import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.Camera;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.utils.FontFactory;

@Setter
public class Minimap extends HudElement {

    private Player local;
    private BufferedImage worldImage;
    private Camera camera;

    public Minimap(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.camera = Camera.getInstance();
    }

    @Override
    protected void render(Canvas canvas) {
        if (worldImage == null || local == null)
            return;

        int visibleWidth = (int) (camera.getViewportWidth() * 2.5);
        int visibleHeight = (int) (camera.getViewportHeight() * 2.5);

        int startX = Math.max(0, (int) local.getX() - visibleWidth / 2);
        int startY = Math.max(0, (int) local.getY() - visibleHeight / 2);

        startX = Math.min(startX, worldImage.getWidth() - visibleWidth);
        startY = Math.min(startY, worldImage.getHeight() - visibleHeight);

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

        renderDirectionalLabels(canvas, minimapX, minimapY, width, height);
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