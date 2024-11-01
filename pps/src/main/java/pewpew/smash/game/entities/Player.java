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
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.MeleeWeapon;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.Weapon;
import pewpew.smash.game.objects.WeaponType;
import pewpew.smash.game.objects.weapon.AK47;
import pewpew.smash.game.objects.weapon.Fist;

@ToString(callSuper = true)
@Getter
@Setter
public class Player extends MovableEntity {

    private Weapon equippedWeapon;
    private Fist fists;
    private MouseInput mouseInput = MouseInput.NONE;

    private float rotation;

    private String username;
    private int health;

    public Player(int id) {
        setDimensions(40, 40);
        teleport(100, 100);
        setSpeed(2);
        this.rotation = 0f;
        this.id = id;
        this.health = 100;

        this.fists = ItemFactory.createItem(WeaponType.FIST);
        this.fists.pickup(this);
        AK47 ak47 = ItemFactory.createItem(WeaponType.AK47);
        ak47.pickup(this);
        this.equippedWeapon = ak47;
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
    }

    @Override
    public void updateServer() {
        move(1);
        if (equippedWeapon instanceof RangedWeapon) {
            this.equippedWeapon.updateServer();
        }
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderCircle(x, y, width, new Color(168, 168, 168));
        canvas.renderCircle(x + 2, y + 2, width - 4, new Color(229, 194, 152));

        this.equippedWeapon.render(canvas);

        canvas.renderString(User.getInstance().getUsername() + "-" + id, x - width / 2, y - height / 2, Color.WHITE);
    }

    @Override
    public Shape getHitbox() {
        return new Ellipse2D.Float(getX(), getY(), width, width);
    }

    public void applyState(PlayerState newState) {
        this.health = newState.getHealth();
    }
}
