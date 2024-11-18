package pewpew.smash.game.entities;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.game.Camera;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.objects.Fist;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.MeleeWeapon;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.Weapon;
import pewpew.smash.game.objects.WeaponType;
import pewpew.smash.game.objects.special.Scope;
import pewpew.smash.game.utils.FontFactory;

@ToString(callSuper = true)
@Getter
@Setter
public class Player extends MovableEntity {

    private Weapon equippedWeapon;
    private Fist fists;
    private MouseInput mouseInput = MouseInput.NONE;
    private Inventory inventory;

    private float rotation;

    private String username;
    private int health;

    private boolean canDoAction = true;

    public Player(int id) {
        setDimensions(40, 40);
        teleport(100, 100);
        setSpeed(2f);
        this.rotation = 0f;
        this.id = id;
        this.health = 50;

        this.inventory = new Inventory();
        this.fists = (Fist) ItemFactory.createItem(WeaponType.FIST);
        this.fists.pickup(this);
        this.equippedWeapon = this.fists;

        // Apply player scope to camera
        Camera.getInstance().setZoom(this.inventory.getScope().getZoomValue());
    }

    public Player(int id, String username) {
        this(id);
        this.username = username;
    }

    @Override
    public void updateClient() {
        if (equippedWeapon instanceof MeleeWeapon) {
            this.equippedWeapon.updateClient();
        }

        if (Camera.getZoom() != this.inventory.getScope().getZoomValue()) {
            Camera.getInstance().setZoom(this.inventory.getScope().getZoomValue());
        }
    }

    @Override
    public void updateServer() {
        move(1);
        if (this.inventory.hasPrimaryWeapon() && equippedWeapon instanceof RangedWeapon && canDoAction) {
            this.inventory.getPrimaryWeapon().get().updateServer();
        }
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderCircle(x, y, width, new Color(168, 168, 168));
        canvas.renderCircle(x + 2, y + 2, width - 4, new Color(229, 194, 152));

        if (this.equippedWeapon != null) {
            this.equippedWeapon.render(canvas);
        }

        FontFactory.DEFAULT_FONT.applyFont(canvas);
        String displayUsername = (id == User.getInstance().getLocalID().get()) ? "You" : "";
        canvas.renderString(displayUsername, x - width / 2, y - height / 2, Color.WHITE);
        FontFactory.resetFont(canvas);
    }

    @Override
    public Shape getHitbox() {
        return new Ellipse2D.Float(getX(), getY(), getWidth(), getHeight());
    }

    public void applyState(PlayerState newState) {
        this.health = Math.clamp(newState.getHealth(), 0, 100);
    }

    public void changeWeapon(RangedWeapon newWeapon) {
        inventory.changeWeapon(newWeapon);
        this.equippedWeapon = newWeapon;
        newWeapon.pickup(this);
    }

    public boolean hasAmmo() {
        return inventory.isAmmoEmpty();
    }

    public void setScope(Scope scope) {
        this.inventory.setScope(scope);
        Camera.getInstance().setZoom(scope.getZoomValue());
    }

    public Scope getScope() {
        return this.inventory.getScope();
    }

    public void preventAction() {
        this.canDoAction = false;
        setSpeed(1.25f);
    }

    public void allowAction() {
        this.canDoAction = true;
        setSpeed(2f);
    }
}
