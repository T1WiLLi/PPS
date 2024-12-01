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
    private Ellipse2D.Float innerStorm;
    private int targetRadius;

    @Getter
    private StormStage currentStage;

    // Server only
    public StormEvent(float initalRadius, StormStage stage) {
        this.radius = initalRadius;
        this.centerX = WorldGenerator.getWorldWidth() / 2; // Later this will be random
        this.centerY = WorldGenerator.getWorldHeight() / 2; // Later this will be random
        this.currentStage = stage;
        this.stormSpeed = this.currentStage.getStormSpeed();
        this.hitDamage = this.currentStage.getHitDamage();
        this.targetRadius = this.currentStage.getTargetRadius();

        updateStormArea();
    }

    // Client only
    public StormEvent(StormState state) {
        applyState(state);
        updateStormArea();
    }

    public void update() {
        if (radius > targetRadius) {
            radius -= stormSpeed;
            updateStormArea();
        }
    }

    public void render(Canvas canvas) {
        canvas.renderArea(stormArea, new Color(1, 0, 0, 0.5f));
    }

    public void setCenter(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public boolean isInside(StaticEntity entity) {
        return getBounds().intersects(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
    }

    // Server
    public void transitionToNextStage() {
        this.currentStage = currentStage.next();
        this.stormSpeed = this.currentStage.getStormSpeed();
        this.hitDamage = this.currentStage.getHitDamage();
        this.targetRadius = this.currentStage.getTargetRadius();
    }

    // Client
    public void applyState(StormState state) {
        currentStage = state.getStage();
        this.centerX = state.getCenterX();
        this.centerY = state.getCenterY();
        this.radius = state.getRadius();
        this.stormSpeed = this.currentStage.getStormSpeed();
        this.hitDamage = this.currentStage.getHitDamage();
        this.targetRadius = this.currentStage.getTargetRadius();
    }

    public long getStageDuration(StormStage stage) {
        return switch (stage) {
            case STAGE_1 -> 60 * 1000;
            case STAGE_2 -> 2 * 60 * 1000;
            case STAGE_3 -> 2 * 60 * 1000;
            case STAGE_4 -> 60 * 1000;
        };
    }

    private Ellipse2D.Float getBounds() {
        return new Ellipse2D.Float(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    private void updateStormArea() {
        this.stormArea = new Area(new Rectangle(0, 0, WorldGenerator.getWorldWidth(), WorldGenerator.getWorldHeight()));
        this.innerStorm = getBounds();
        this.stormArea.subtract(new Area(innerStorm));
    }
}