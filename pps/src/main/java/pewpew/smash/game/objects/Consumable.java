package pewpew.smash.game.objects;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public abstract class Consumable extends Item {

    protected int healingAmount;
    protected double timeToConsume;

    @Setter
    private ConsumableType type;

    public abstract void consume();

    public Consumable(int id, String name, String description, BufferedImage preview) {
        super(id, name, description, preview);
    }

    protected void buildConsumable(int healingAmount, double timeToConsume) {
        this.healingAmount = healingAmount;
        this.timeToConsume = timeToConsume;
    }

}
