package pewpew.smash.game.ui;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.audio.AudioClip;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.audio.AudioPlayer.SoundType;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.utils.HelpMethods;

@ToString
public class Button extends UiElement {

    private int index;
    @Setter
    @Getter
    protected boolean mouseOver;
    @Setter
    protected boolean mousePressed;
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

    @Override
    public void update() {
        super.update();

        index = 0;
        updateState();
        if (mouseOver && MouseController.isMousePressed()) {
            mousePressed = true;
        }
        if (mouseOver && mousePressed) {
            playButtonPressedSound();
            onClick.run();
            MouseController.consumeEvent();
            resetState();
        }
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(sprites[index], xPos, yPos, width, height);
    }

    @Override
    protected void handleMouseInput() {
        if (HelpMethods.isIn(bounds)) {
            setMousePressed(MouseController.isMousePressed());
        } else {
            setMousePressed(false);
        }
    }

    @Override
    protected void handleMouseMove() {
        setMouseOver(HelpMethods.isIn(bounds));
    }

    private void updateState() {
        if (mouseOver) {
            index = 1;
            if (!hasSFXPlayed) {
                playButtonHoveredSound();
                hasSFXPlayed = true;
            }
        } else {
            hasSFXPlayed = false;
        }
        if (mousePressed) {
            index = 2;
        }
    }

    private void playButtonHoveredSound() {
        AudioPlayer.getInstance().play(AudioClip.BUTTON_HOVERED, 0.80f, false, SoundType.UI);
    }

    private void playButtonPressedSound() {
        AudioPlayer.getInstance().play(AudioClip.BUTTON_PRESSED, 0.80f, false, SoundType.UI);
    }

    protected void resetState() {
        this.mouseOver = false;
        this.mousePressed = false;
    }

    @Override
    protected void loadSprites(BufferedImage spriteSheet) {
        final int spriteCount = 3;
        sprites = new BufferedImage[spriteCount];
        for (int i = 0; i < spriteCount; i++) {
            sprites[i] = extractSprite(spriteSheet, i);
        }
    }

    private BufferedImage extractSprite(BufferedImage spriteSheet, int index) {
        int startX = Constants.X_OFFSET + index * (Constants.SPRITE_WIDTH + Constants.SPRITE_SPACER);
        int startY = Constants.Y_OFFSET;
        return spriteSheet.getSubimage(startX, startY, Constants.SPRITE_WIDTH, Constants.SPRITE_HEIGHT);
    }
}
