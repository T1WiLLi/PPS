package pewpew.smash.game.hud;

import java.awt.Color;
import java.awt.image.BufferedImage;

import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.utils.FontFactory;

// Only 1 per client
public class HudManager {
    private static HudManager instance;

    private Player local;
    private BarDisplayer healthBar;
    private BarDisplayer ammoBar;
    private WeaponDisplayer weaponDisplayer;
    private AlivePlayerDisplayer alivePlayerDisplayer;
    private ScopeElementDisplayer scopeElementDisplayer;
    private ConsumableDisplayer consumableDisplayer;
    private AmmoDisplayer ammoDisplayer;
    private Minimap minimap;

    private CircleLoaderManager circleLoaderManager;

    @Setter
    private int amountOfPlayerAlive;

    private long waterWarningStartTime;
    private int warningDuration;
    private boolean isInWater;

    public static HudManager getInstance() {
        if (instance == null) {
            instance = new HudManager();
        }
        return instance;
    }

    public void setPlayer(Player player) {
        this.local = player;
        this.weaponDisplayer.setPlayer(player);
        this.consumableDisplayer.setInventory(player.getInventory());
        this.healthBar.setMaxValue(100);
        this.minimap.setLocal(player);
    }

    public void setWorldImage(BufferedImage image) {
        System.out.println("World Image!");
        this.minimap.setWorldImage(image);
    }

    public void update() {
        if (this.local != null) {
            this.healthBar.setValue(this.local.getHealth());
            this.ammoDisplayer.setAmmo(this.local.getInventory().getAmmoCount());
            this.alivePlayerDisplayer.setAmountOfPlayerAlive(this.amountOfPlayerAlive);
            this.scopeElementDisplayer.setScope(this.local.getScope().getPreview());
            this.circleLoaderManager.update(this.local);
            if (this.local.getEquippedWeapon() instanceof RangedWeapon) {
                this.ammoBar.setMaxValue(((RangedWeapon) this.local.getEquippedWeapon()).getAmmoCapacity());
                this.ammoBar.setValue(((RangedWeapon) this.local.getEquippedWeapon()).getCurrentAmmo());
            }
        }
    }

    public void render(Canvas canvas) {
        this.healthBar.render(canvas);
        this.ammoBar.render(canvas);
        this.weaponDisplayer.render(canvas);
        this.alivePlayerDisplayer.render(canvas);
        this.scopeElementDisplayer.render(canvas);
        this.consumableDisplayer.render(canvas);
        this.ammoDisplayer.render(canvas);
        this.minimap.render(canvas);
        this.circleLoaderManager.render(canvas);

        if (isInWater) {
            renderWaterWarning(canvas);
        }
    }

    public void reset() {
        instance = null;
    }

    private HudManager() {
        this.healthBar = new BarDisplayer(10, 10, 200, 25, Color.RED);
        this.ammoBar = new BarDisplayer(10, 40, 100, 25, Color.ORANGE);
        this.weaponDisplayer = new WeaponDisplayer(800 - 105 - 10, 600 - 100 - 10, 120, 100);
        this.alivePlayerDisplayer = new AlivePlayerDisplayer(690, 10, 100, 100);
        this.scopeElementDisplayer = new ScopeElementDisplayer(375, 25, 50, 50);
        this.consumableDisplayer = new ConsumableDisplayer(800 - 120 - 10, 225, 120, 150);
        this.ammoDisplayer = new AmmoDisplayer(550, 545, 110, 50);
        this.minimap = new Minimap(20, 580, 100, 100);

        this.circleLoaderManager = new CircleLoaderManager();
    }

    public void startWaterWarning(int durationInSeconds) {
        this.waterWarningStartTime = System.currentTimeMillis();
        this.warningDuration = durationInSeconds;
        this.isInWater = true;
    }

    public void stopWaterWarning() {
        this.isInWater = false;
    }

    public void startLoader(long secondDelay, Runnable action, Player player) {
        this.circleLoaderManager.startLoader(secondDelay, action, player);
    }

    private void renderWaterWarning(Canvas canvas) {
        long elapsedTime = (System.currentTimeMillis() - waterWarningStartTime) / 1000;
        int timeRemaining = warningDuration - (int) elapsedTime;

        FontFactory.IMPACT_X_LARGE.applyFont(canvas);

        String firstLine, secondLine;

        if (timeRemaining > 0) {
            firstLine = "You can't swim!";
            secondLine = String.format("You have %d seconds to leave the waters", timeRemaining);
        } else {
            firstLine = "Get out!";
            secondLine = "You are taking damage";
        }

        canvas.setColor(elapsedTime % 2 == 0 ? Color.RED : Color.DARK_GRAY);

        int firstLineWidth = FontFactory.IMPACT_X_LARGE.getFontWidth(firstLine, canvas);
        int secondLineWidth = FontFactory.IMPACT_X_LARGE.getFontWidth(secondLine, canvas);
        int textHeight = FontFactory.IMPACT_X_LARGE.getFontHeight(canvas);

        int centerXFirstLine = (800 - firstLineWidth) / 2;
        int centerXSecondLine = (800 - secondLineWidth) / 2;
        int centerY = (600 - (textHeight * 2)) / 2;

        canvas.renderString(firstLine, centerXFirstLine, centerY);
        canvas.renderString(secondLine, centerXSecondLine, centerY + textHeight);

        FontFactory.resetFont(canvas);
    }

}
