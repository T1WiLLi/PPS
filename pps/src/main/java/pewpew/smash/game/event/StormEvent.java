package pewpew.smash.game.event;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import lombok.Getter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.network.model.StormState;
import pewpew.smash.game.world.WorldGenerator;

public class StormEvent {

    @Getter
    private int centerX, centerY;
    @Getter
    private float radius;
    private float stormSpeed;
    @Getter
    private int hitDamage;
    private Area stormArea;
    private Area innerStorm;
    @Getter
    private int targetRadius;

    @Getter
    private StormStage currentStage;

    private float lastRadius;

    public StormEvent(float initialRadius, StormStage stage) {
        this.radius = initialRadius;
        this.centerX = WorldGenerator.getWorldWidth() / 2;
        this.centerY = WorldGenerator.getWorldHeight() / 2;
        this.currentStage = stage;
        this.stormSpeed = stage.getStormSpeed();
        this.hitDamage = stage.getHitDamage();
        this.targetRadius = stage.getTargetRadius();

        this.stormArea = new Area(new Rectangle(0, 0, WorldGenerator.getWorldWidth(), WorldGenerator.getWorldHeight()));
        this.innerStorm = new Area(getBounds());
        this.stormArea.subtract(this.innerStorm);
        lastRadius = radius;
    }

    public StormEvent(StormState state) {
        applyState(state);
        this.stormArea = new Area(new Rectangle(0, 0, WorldGenerator.getWorldWidth(), WorldGenerator.getWorldHeight()));
        this.innerStorm = new Area(getBounds());
        this.stormArea.subtract(this.innerStorm);
        lastRadius = radius;
    }

    public void update() {
        if (radius > targetRadius) {
            float newRadius = Math.max(radius - stormSpeed, targetRadius);
            if (newRadius != radius) {
                radius = newRadius;
                updateStormArea();
            }
        }
    }

    public void render(Canvas canvas) {
        canvas.renderArea(stormArea, new Color(1, 0, 0, 0.5f));
    }

    public void setCenter(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        updateStormArea();
    }

    public boolean isInside(StaticEntity entity) {
        return innerStorm.intersects(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
    }

    public void applyState(StormState state) {
        currentStage = state.getStage();
        this.centerX = state.getCenterX();
        this.centerY = state.getCenterY();
        this.radius = state.getRadius();
        this.stormSpeed = currentStage.getStormSpeed();
        this.hitDamage = currentStage.getHitDamage();
        this.targetRadius = currentStage.getTargetRadius();
        updateStormArea();
    }

    public StormState toStormState() {
        return new StormState(centerX, centerY, radius, currentStage);
    }

    public void transitionToStage(StormStage newStage) {
        this.currentStage = newStage;
        this.stormSpeed = newStage.getStormSpeed();
        this.hitDamage = newStage.getHitDamage();
        this.targetRadius = newStage.getTargetRadius();
        updateStormArea();
    }

    private Ellipse2D.Float getBounds() {
        return new Ellipse2D.Float(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    private void updateStormArea() {
        if (lastRadius == radius) {
            return;
        }

        this.stormArea = new Area(new Rectangle(0, 0, WorldGenerator.getWorldWidth(), WorldGenerator.getWorldHeight()));
        this.innerStorm = new Area(getBounds());
        this.stormArea.subtract(this.innerStorm);

        lastRadius = radius;
    }
}