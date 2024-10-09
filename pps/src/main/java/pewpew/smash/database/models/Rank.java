package pewpew.smash.database.models;

import java.awt.image.BufferedImage;

import lombok.Data;
import lombok.ToString;
import pewpew.smash.game.utils.ResourcesLoader;

@Data
@ToString
public class Rank {
    private int id;
    private String name;
    private String description;
    private String imageURL;
    private BufferedImage image;
    private int currentXp;
    private int maxXp;
    private int minXp;

    public Rank(int id, String name, String description, String imageURL, int currentXp, int maxXp, int minXp) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
        this.currentXp = currentXp;
        this.maxXp = maxXp;
        this.minXp = minXp;
        loadImage();
    }

    private void loadImage() {
        this.image = ResourcesLoader.getImage(ResourcesLoader.RANK_PATH, this.imageURL);
    }
}
