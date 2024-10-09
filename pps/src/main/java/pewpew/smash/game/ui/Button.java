package pewpew.smash.game.ui;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.audio.AudioPlayer.SoundType;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.utils.ResourcesLoader;

@ToString
public class Button extends UiElement {

    private int index;
    @Setter
    protected boolean mouseOver, mousePressed;
    protected boolean hasSFXPlayed;
    @Getter
    protected Runnable onClick;
    private BufferedImage[] sprites;

    public Button(int x, int y, BufferedImage spriteSheet, Runnable onClick) {
        super(x, y, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        this.onClick = onClick;
        loadSprites(spriteSheet);
    }

    public Button(int x, int y, int width, int height, BufferedImage spriteSheet, Runnable onClick) {
        super(x, y, width, height);
        this.onClick = onClick;
        loadSprites(spriteSheet);
    }

    public void update() {
        index = 0;
        statesUpdate();
        if (mouseOver && mousePressed) {
            AudioPlayer.getInstance().play(ResourcesLoader.getAudio(ResourcesLoader.AUDIO_PATH, "ButtonPressed"), 0.90f,
                    false, SoundType.UI);
            this.onClick.run();
            resetState();
        }
        updateScaledBounds();
    }

    public void render(Canvas canvas) {
        canvas.renderImage(sprites[index], xPos, yPos, width, height);
    }

    private void statesUpdate() {
        if (mouseOver) {
            index = 1;
            if (!hasSFXPlayed) {
                AudioPlayer.getInstance().play(
                        ResourcesLoader.getAudio(ResourcesLoader.AUDIO_PATH, "ButtonHovered"), 0.80f,
                        false, SoundType.UI);
                this.hasSFXPlayed = true;
            }
        } else {
            hasSFXPlayed = false;
        }
        if (mousePressed) {
            index = 2;
        }
    }

    protected void resetState() {
        this.mouseOver = false;
        this.mousePressed = false;
    }

    @Override
    protected void loadSprites(BufferedImage spriteSheet) {
        this.sprites = new BufferedImage[3];
        for (int i = 0; i < 3; i++) {
            int startX = Constants.X_OFFSET + i * (Constants.SPRITE_WIDTH + Constants.SPRITE_SPACER);
            int startY = Constants.Y_OFFSET;
            this.sprites[i] = spriteSheet.getSubimage(startX, startY, Constants.SPRITE_WIDTH,
                    Constants.SPRITE_HEIGHT);
        }
    }
}
