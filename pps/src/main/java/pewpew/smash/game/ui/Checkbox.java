package pewpew.smash.game.ui;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.audio.AudioClip;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.audio.AudioPlayer.SoundType;
import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.utils.ResourcesLoader;

import java.awt.image.BufferedImage;
import java.awt.Color;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Checkbox extends UiElement {
    @Getter
    @Setter
    private boolean checked;

    private Runnable onCheck;

    private BufferedImage checkSprite;

    public Checkbox(int x, int y, Runnable onCheck) {
        super(x, y, 25, 25);
        this.checked = false;
        this.onCheck = onCheck;
        loadSprites(ResourcesLoader.getImage(ResourcesLoader.MISC_PATH, "check"));
    }

    @Override
    public void update() {
        updateScaledBounds();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderRectangleBorder(xPos, yPos, width, height, 3, Color.WHITE);
        if (checked) {
            canvas.renderImage(checkSprite, xPos, yPos - 10, this.width + 10, this.height + 10);
        }
    }

    @Override
    public void handleMouseInput() {
        if (HelpMethods.isIn(bounds)) {
            checked = !checked;
            onCheck.run();
            AudioPlayer.getInstance().play(AudioClip.SWAPPED, 0.95f, false, SoundType.UI);
        }
    }

    @Override
    public void handleMouseMove() {

    }

    @Override
    protected void loadSprites(BufferedImage sprite) {
        this.checkSprite = sprite;
    }
}
