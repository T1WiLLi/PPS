package pewpew.smash.game.network.client;

import java.awt.Color;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.Camera;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.objects.special.AmmoStack;
import pewpew.smash.game.settings.SettingsManager;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ViewUtils;

public class ClientItemRenderer {
    private static final int PICKUP_RADIUS = 100;

    public void render(Canvas canvas, Camera camera, Player localPlayer) {
        ItemManager.getInstance(false).getItems().forEach(item -> {
            if (ViewUtils.isInView(item.getX(), item.getY())) {
                canvas.translate(-camera.getX(), -camera.getY());
                item.preview(canvas);
                if (isPlayerNearItem(localPlayer, item)) {
                    renderPickupPrompt(canvas, item, camera);
                }
                canvas.translate(camera.getX(), camera.getY());
            }
        });
    }

    private boolean isPlayerNearItem(Player player, Item item) {
        double distance = calculateDistance(player.getX(), player.getY(), item.getX(), item.getY());
        return distance <= PICKUP_RADIUS;
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private void renderPickupPrompt(Canvas canvas, Item item, Camera camera) {
        FontFactory.SMALL_FONT.applyFont(canvas);
        canvas.renderString(
                item.getName() + ((item instanceof AmmoStack) ? "(" + ((AmmoStack) item).getAmmo() + ")" : ""),
                item.getX(), item.getY() - 5, Color.WHITE);

        FontFactory.MEDIUM_FONT.applyFont(canvas);
        canvas.renderString(
                SettingsManager.getInstance().getSettings().getKey().getMisc().get("use").toLowerCase(),
                item.getX(), item.getY() + item.getWidth() + 10, Color.YELLOW);
        FontFactory.resetFont(canvas);
    }
}
