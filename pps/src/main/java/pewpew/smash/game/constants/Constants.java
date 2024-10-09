package pewpew.smash.game.constants;

public class Constants {

    public static final int DEFAULT_SCREEN_WIDTH = 800;
    public static final int DEFAULT_SCREEN_HEIGHT = 600;

    public static final int X_OFFSET = 4; // pixels
    public static final int Y_OFFSET = 10; // pixels
    public static final int SPRITE_WIDTH = 136; // pixels
    public static final int SPRITE_HEIGHT = 55; // pixels
    public static final int SPRITE_SPACER = 8; // pixels

    public static final int BUTTON_WIDTH = 210;
    public static final int BUTTON_HEIGHT = 50;
    public static final int TOP_PADDING = 60;
    public static final int BOTTOM_PADDING = 60;
    public static final int LEFT_PADDING = 30;
    public static final int RIGHT_PADDING = DEFAULT_SCREEN_WIDTH - LEFT_PADDING - BUTTON_WIDTH;

    public static final int PLAY_BUTTON_Y = DEFAULT_SCREEN_HEIGHT - BOTTOM_PADDING - 4 * BUTTON_HEIGHT - 40;
    public static final int CONNECT_BUTTON_Y = DEFAULT_SCREEN_HEIGHT - BOTTOM_PADDING - 3 * BUTTON_HEIGHT - 30;
    public static final int SETTINGS_BUTTON_Y = DEFAULT_SCREEN_HEIGHT - BOTTOM_PADDING - 2 * BUTTON_HEIGHT - 20;
    public static final int CREDITS_BUTTON_Y = DEFAULT_SCREEN_HEIGHT - BOTTOM_PADDING - BUTTON_HEIGHT - 10;
    public static final int QUIT_BUTTON_Y = DEFAULT_SCREEN_HEIGHT - BOTTOM_PADDING;

}
