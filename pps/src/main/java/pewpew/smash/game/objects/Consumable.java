package pewpew.smash.game.objects;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public abstract class Consumable extends Item {

    protected int healingAmount;
    protected double timeToConsume;

    public abstract void consume();

    public Consumable(String name, String description, BufferedImage preview) {
        super(name, description, preview);
    }

    protected void buildConsumable(int healingAmount, double timeToConsume) {
        this.healingAmount = healingAmount;
        this.timeToConsume = timeToConsume;
    }

}
