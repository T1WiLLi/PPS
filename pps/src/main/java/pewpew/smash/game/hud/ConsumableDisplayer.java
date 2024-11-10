package pewpew.smash.game.hud;

import java.awt.Color;
import java.awt.image.BufferedImage;

import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Inventory;
import pewpew.smash.game.objects.ConsumableType;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class ConsumableDisplayer extends HudElement {

    @Setter
    private Inventory inventory;
    private BufferedImage medikit, bandage, pill;

    public ConsumableDisplayer(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadImages();
    }

    @Override
    public void render(Canvas canvas) {
        int offsetY = 0;
        int slot = 3;

        renderConsumable(canvas, ConsumableType.MEDIKIT, medikit, offsetY, slot);
        offsetY += 45;
        renderConsumable(canvas, ConsumableType.BANDAGE, bandage, offsetY, ++slot);
        offsetY += 45;
        renderConsumable(canvas, ConsumableType.PILL, pill, offsetY, ++slot);
    }

    private void renderConsumable(Canvas canvas, ConsumableType type, BufferedImage image, int offsetY,
            int slotNumber) {
        int quantity = inventory.getConsumableQuantity(type).orElse(0);

        canvas.renderRectangle(x, y + offsetY, width, 45, new Color(34, 85, 24, 200));

        if (image != null) {
            canvas.renderImage(image, x + 5, y + offsetY + 5, 35, 35);
        }

        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.setColor(Color.ORANGE);
        canvas.renderString(String.valueOf(slotNumber), x + 5, y + offsetY + 40);

        FontFactory.DEFAULT_FONT.applyFont(canvas);
        canvas.setColor(quantity > 0 ? Color.WHITE : new Color(200, 200, 200));
        canvas.renderString(type.name(), x + 45, y + offsetY + 20);

        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.setColor(Color.YELLOW);
        canvas.renderString("x" + quantity, x + width - 20, y + offsetY + 40);

        FontFactory.resetFont(canvas);
    }

    private void loadImages() {
        this.medikit = ResourcesLoader.getImage(ResourcesLoader.PREVIEW_PATH, "medikit");
        this.bandage = ResourcesLoader.getImage(ResourcesLoader.PREVIEW_PATH, "bandage");
        this.pill = ResourcesLoader.getImage(ResourcesLoader.PREVIEW_PATH, "pill");
    }
}
