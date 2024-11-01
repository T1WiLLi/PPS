package pewpew.smash.game.hud;

import java.awt.Color;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.objects.RangedWeapon;

// Only 1 per client
public class HudManager {
    private static HudManager instance;

    private Player local;
    private BarDisplayer healthBar;
    private BarDisplayer ammoBar;
    private WeaponDisplayer weaponDisplayer;

    public static HudManager getInstance() {
        if (instance == null) {
            instance = new HudManager();
        }
        return instance;
    }

    public void setPlayer(Player player) {
        this.local = player;
        this.weaponDisplayer.setPlayer(player);
        this.healthBar.setMaxValue(100);
    }

    public void update() {
        this.healthBar.setValue(this.local.getHealth());
        if (this.local.getEquippedWeapon() instanceof RangedWeapon) {
            this.ammoBar.setMaxValue(((RangedWeapon) this.local.getEquippedWeapon()).getAmmoCapacity());
            this.ammoBar.setValue(((RangedWeapon) this.local.getEquippedWeapon()).getCurrentAmmo());
        }
    }

    public void render(Canvas canvas) {
        this.healthBar.render(canvas);
        this.ammoBar.render(canvas);
        this.weaponDisplayer.render(canvas);
    }

    private HudManager() {
        this.healthBar = new BarDisplayer(10, 10, 200, 25, Color.RED);
        this.ammoBar = new BarDisplayer(10, 40, 100, 25, Color.ORANGE);
        this.weaponDisplayer = new WeaponDisplayer(800 - 120 - 10, 600 - 100 - 10, 120, 100);
    }
}
