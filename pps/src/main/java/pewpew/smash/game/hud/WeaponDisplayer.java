package pewpew.smash.game.hud;

import java.awt.Color;
import java.awt.image.BufferedImage;

import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.objects.weapon.Fist;

public class WeaponDisplayer extends HudElement {

    @Setter
    private Player player;

    public WeaponDisplayer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void render(Canvas canvas) {
        canvas.setColor(new Color(34, 85, 24, 200));

        boolean isFistSelected = player.getEquippedWeapon() instanceof Fist;

        renderSlot(canvas, x + 10, y + 10, 1, "Fists", player.getFists().getPreview(), isFistSelected);

        player.getInventory().getPrimaryWeapon().ifPresentOrElse(primaryWeapon -> {
            boolean isPrimarySelected = !isFistSelected;
            renderSlot(canvas, x + 10, y + 60, 2, primaryWeapon.getName(), primaryWeapon.getPreview(),
                    isPrimarySelected);
        }, () -> {
            renderSlot(canvas, x + 10, y + 60, 2, "Empty", null, !isFistSelected);
        });
    }

    private void renderSlot(Canvas canvas, int slotX, int slotY, int slotNumber, String weaponName,
            BufferedImage weaponImage, boolean isSelected) {
        canvas.renderRectangle(slotX - 5, slotY - 5, 100, 50, new Color(34, 85, 24, 180));

        if (isSelected) {
            canvas.renderRectangle(slotX - 5, slotY - 5, 100, 50, new Color(20, 20, 20, 150));
        }

        if (weaponImage != null) {
            canvas.renderImage(weaponImage, slotX, slotY, 32, 32);
        }

        canvas.setColor(Color.WHITE);
        canvas.renderString(String.valueOf(slotNumber), slotX + 40, slotY + 15, Color.WHITE);
        canvas.renderString(weaponName, slotX + 40, slotY + 35, Color.WHITE);
    }
}
