package pewpew.smash.game.hud;

import java.awt.Color;
import java.awt.image.BufferedImage;

import lombok.Setter;
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
    private AlivePlayerDisplayer alivePlayerDisplayer;
    private ScopeElementDisplayer scopeElementDisplayer;
    private Minimap minimap;

    @Setter
    private int amountOfPlayerAlive;

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
        if (this.local.getInventory().getPrimaryWeapon().isPresent()
                && this.local.getEquippedWeapon().equals(this.local.getInventory().getPrimaryWeapon().get())) {
            this.ammoBar.setMaxValue(((RangedWeapon) this.local.getEquippedWeapon()).getAmmoCapacity());
        }
        this.minimap.setLocal(player);
    }

    public void setWorldImage(BufferedImage image) {
        this.minimap.setWorldImage(image);
    }

    public void update() {
        this.healthBar.setValue(this.local.getHealth());
        this.alivePlayerDisplayer.setAmountOfPlayerAlive(this.amountOfPlayerAlive);
        this.scopeElementDisplayer.setScope(local.getScope().getPreview());
        if (this.local.getEquippedWeapon() instanceof RangedWeapon) {
            this.ammoBar.setValue(((RangedWeapon) this.local.getEquippedWeapon()).getCurrentAmmo());
        }
    }

    public void render(Canvas canvas) {
        this.healthBar.render(canvas);
        this.ammoBar.render(canvas);
        this.weaponDisplayer.render(canvas);
        this.alivePlayerDisplayer.render(canvas);
        this.scopeElementDisplayer.render(canvas);
        this.minimap.render(canvas);
    }

    private HudManager() {
        this.healthBar = new BarDisplayer(10, 10, 200, 25, Color.RED);
        this.ammoBar = new BarDisplayer(10, 40, 100, 25, Color.ORANGE);
        this.weaponDisplayer = new WeaponDisplayer(800 - 120 - 10, 600 - 100 - 10, 120, 100);
        this.alivePlayerDisplayer = new AlivePlayerDisplayer(690, 10, 100, 100);
        this.scopeElementDisplayer = new ScopeElementDisplayer(375, 25, 50, 50);
        this.minimap = new Minimap(20, 580, 100, 100);
    }
}
