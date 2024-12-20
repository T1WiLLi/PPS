package pewpew.smash.game.entities;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.game.Camera;
import pewpew.smash.game.Animation.PlayerAnimation;
import pewpew.smash.game.Animation.PlayerAnimationManager;
import pewpew.smash.game.Animation.PlayerAnimationState;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.objects.Fist;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.MeleeWeapon;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.Weapon;
import pewpew.smash.game.objects.WeaponType;
import pewpew.smash.game.objects.special.Scope;
import pewpew.smash.game.post_processing.EffectType;
import pewpew.smash.game.post_processing.PostProcessingManager;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.HelpMethods;

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

    private PlayerAnimationManager animationManager;
    private BufferedImage currentSprite;

    public Player(int id) {
        setDimensions(40, 40);
        teleport(100, 100);
        setSpeed(2f);
        this.rotation = 0f;
        this.id = id;
        this.health = 50;

        this.inventory = new Inventory(this);
        this.fists = (Fist) ItemFactory.createItem(WeaponType.FIST);
        this.fists.pickup(this);
        this.equippedWeapon = this.fists;

        // Apply player scope to camera
        Camera.getInstance().setZoom(this.inventory.getScope().getZoomValue());

        this.animationManager = new PlayerAnimationManager(10);
        this.animationManager.loadAnimations();
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

        if (this.id == User.getInstance().getLocalID().get()) {
            if (Camera.getZoom() != this.inventory.getScope().getZoomValue()) {
                Camera.getInstance().setZoom(this.inventory.getScope().getZoomValue());
            }
        }

        PlayerAnimationState currentState = HelpMethods.getCurrentState(this);
        PlayerAnimation currentAnimation = HelpMethods.getCurrentAnimation(this);

        animationManager.updateAnimation(currentState, currentAnimation);
    }

    @Override
    public void updateServer() {
        move();
        if (this.inventory.hasPrimaryWeapon() && equippedWeapon instanceof RangedWeapon && canDoAction) {
            this.inventory.getPrimaryWeapon().get().updateServer();
        }
    }

    @Override
    public void render(Canvas canvas) {
        FontFactory.DEFAULT_FONT.applyFont(canvas);
        String displayUsername = (id == User.getInstance().getLocalID().get()) ? "You" : "";
        canvas.renderString(displayUsername, x - width / 2, y - height / 2, Color.WHITE);
        FontFactory.resetFont(canvas);

        int widthOffset = 0;
        int heightOffset = 0;

        if (this.equippedWeapon instanceof MeleeWeapon meleeWeapon) {
            if (meleeWeapon.isAttacking()) {
                widthOffset = 25;
                heightOffset = 25;
            }
        }

        canvas.rotate(rotation, (x + getWidth() / 2), (y + getHeight() / 2));
        canvas.renderImage(animationManager.getFrame(), (x - getWidth() / 2) + 10, (y - getHeight() / 2),
                width * 2 + widthOffset,
                height * 2 + heightOffset);
        canvas.resetRotation();
    }

    @Override
    public Shape getHitbox() {
        return new Ellipse2D.Float(getX(), getY(), getWidth(), getHeight());
    }

    public void applyState(PlayerState newState) {
        if (User.getInstance().getLocalID().get() == this.id) {
            if (newState.getHealth() < this.health) {
                PostProcessingManager.getInstance().triggerEffect(EffectType.ON_DAMAGE);
            }
        }
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
