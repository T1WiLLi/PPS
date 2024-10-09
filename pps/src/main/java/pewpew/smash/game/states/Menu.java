package pewpew.smash.game.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.PewPewSmash;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.audio.AudioPlayer.SoundType;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.network.User;
import pewpew.smash.game.overlay.AboutOverlay;
import pewpew.smash.game.overlay.AccountOverlay;
import pewpew.smash.game.overlay.ConnectionOverlay;
import pewpew.smash.game.overlay.OptionsOverlay;
import pewpew.smash.game.overlay.OverlayManager;
import pewpew.smash.game.overlay.PlayOverlay;
import pewpew.smash.game.ui.Button;

import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.utils.ResourcesLoader;

public class Menu extends GameState {

    private OverlayManager overlayManager;
    private Button[] buttons = new Button[6];
    private BufferedImage background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "menu");
    private Button connectButton;
    private Button accountButton;

    private boolean isUserConnected = false;

    public Menu(PewPewSmash pewPewSmash) {
        super(pewPewSmash);
        init();
    }

    @Override
    public void update() {
        if (overlayManager.hasActiveOverlays()) {
            overlayManager.update();
            return;
        }
        updateConnectOrAccountButton();
        updateButtons();
    }

    @Override
    public void render(Canvas canvas) {
        if (overlayManager.hasActiveOverlays()) {
            overlayManager.render(canvas);
            return;
        }
        renderBackground(canvas);
        renderButtons(canvas);
        renderBanner(canvas);
    }

    @Override
    public void handleMousePress(MouseEvent e) {
        if (!overlayManager.handleMousePress(e)) {
            handleMouseInput(true);
        }
    }

    @Override
    public void handleMouseRelease(MouseEvent e) {
        if (!overlayManager.handleMouseRelease(e)) {
            handleMouseInput(false);
        }
    }

    @Override
    public void handleMouseMove(MouseEvent e) {
        if (!overlayManager.handleMouseMove(e)) {
            updateMouseOverButtons();
        }
    }

    @Override
    public void handleMouseDrag(MouseEvent e) {
        overlayManager.handleMouseDrag(e);
    }

    @Override
    public void handleKeyPress(KeyEvent e) {
        if (!overlayManager.handleKeyPress(e)) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                pewPewSmash.stop();
            }
        }
    }

    @Override
    public void handleKeyRelease(KeyEvent e) {
        if (!overlayManager.handleKeyRelease(e)) {
            // Do somethings in the menu
        }
    }

    private void init() {
        this.overlayManager = new OverlayManager();
        AudioPlayer.getInstance().play(ResourcesLoader.getAudio(ResourcesLoader.AUDIO_PATH, "MainTheme"), 0.75f,
                true, SoundType.MUSIC);
        loadButtons();
    }

    private void loadButtons() {
        AboutOverlay aboutOverlay = new AboutOverlay(overlayManager, 0, 0, 800, 600);
        OptionsOverlay optionsOverlay = new OptionsOverlay(overlayManager, 0, 0, 800, 600);
        ConnectionOverlay connectionOverlay = new ConnectionOverlay(overlayManager, 0, 0, 800, 600);
        AccountOverlay accountOverlay = new AccountOverlay(overlayManager, 0, 0, 800, 600);
        PlayOverlay playOverlay = new PlayOverlay(overlayManager, 0, 0, 800, 600);

        this.buttons[0] = new Button(
                Constants.LEFT_PADDING,
                Constants.PLAY_BUTTON_Y,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/playButton"),
                () -> overlayManager.push(playOverlay));

        this.connectButton = new Button(
                Constants.LEFT_PADDING,
                Constants.CONNECT_BUTTON_Y,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/connectButton"),
                () -> overlayManager.push(connectionOverlay));
        this.buttons[1] = this.connectButton;

        this.accountButton = new Button(
                Constants.LEFT_PADDING,
                Constants.CONNECT_BUTTON_Y,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/accountButton"),
                () -> overlayManager.push(accountOverlay));

        this.buttons[2] = new Button(
                Constants.LEFT_PADDING,
                Constants.SETTINGS_BUTTON_Y,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/optionsButton"),
                () -> overlayManager.push(optionsOverlay));

        this.buttons[3] = new Button(
                Constants.LEFT_PADDING,
                Constants.CREDITS_BUTTON_Y,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/aboutButton"),
                () -> overlayManager.push(aboutOverlay));

        this.buttons[4] = new Button(
                Constants.LEFT_PADDING,
                Constants.QUIT_BUTTON_Y,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/quitButton"),
                () -> pewPewSmash.conclude());
    }

    private void updateButtons() {
        for (Button button : buttons) {
            if (button != null) {
                button.update();
            }
        }
    }

    private void renderButtons(Canvas canvas) {
        for (Button button : buttons) {
            if (button != null) {
                button.render(canvas);
            }
        }
    }

    private void updateMouseOverButtons() {
        for (Button button : buttons) {
            if (button != null) {
                button.setMouseOver(false);
                if (HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(), button.getBounds())) {
                    button.setMouseOver(true);
                }
            }
        }
    }

    private void renderBackground(Canvas canvas) {
        canvas.renderImage(background, 0, 0, 800, 600);
    }

    private void renderBanner(Canvas canvas) {
        int bannerWidth = 200;
        int bannerHeight = 50;
        int triangleWidth = 40;

        int x = 800 - bannerWidth - 20;
        int y = 20;

        renderBannerBackground(canvas, x, y, bannerWidth, bannerHeight);
        renderBannerTriangles(canvas, x, y, bannerWidth, bannerHeight, triangleWidth);
        renderBannerContent(canvas, x, y, bannerWidth, bannerHeight);
    }

    private void renderBannerBackground(Canvas canvas, int x, int y, int bannerWidth, int bannerHeight) {
        canvas.renderRectangle(x - 3, y - 3, bannerWidth + 6, bannerHeight + 6, Color.WHITE);
        canvas.renderRectangle(x, y, bannerWidth, bannerHeight, new Color(128, 128, 0));
    }

    private void renderBannerTriangles(Canvas canvas, int x, int y, int bannerWidth, int bannerHeight,
            int triangleWidth) {
        Polygon triangle = new Polygon();
        triangle.addPoint(x, y);
        triangle.addPoint(x, y + bannerHeight);
        triangle.addPoint(x - triangleWidth, y + bannerHeight / 2);

        Polygon triangle2 = new Polygon();
        triangle2.addPoint(x - 3, y - 3);
        triangle2.addPoint(x - 3, y + bannerHeight + 3);
        triangle2.addPoint(x - triangleWidth - 3, y + bannerHeight / 2);

        canvas.renderPolygon(triangle2, Color.WHITE);
        canvas.renderPolygon(triangle, new Color(128, 128, 0));
    }

    private void renderBannerContent(Canvas canvas, int x, int y, int bannerWidth, int bannerHeight) {
        canvas.setFont(new Font("Impact", Font.PLAIN, 22));
        canvas.renderImage(User.getInstance().getRank().getImage(), x,
                y + bannerHeight / 2 - 25, 50, 50);
        canvas.renderString(User.getInstance().getUsername(), x + 60, y + bannerHeight / 2 + 7, Color.WHITE);
        canvas.resetFont();
    }

    private void updateConnectOrAccountButton() {
        boolean isUserCurrentlyConnected = User.getInstance().isConnected();
        if (isUserCurrentlyConnected != isUserConnected) {
            isUserConnected = isUserCurrentlyConnected;
            if (isUserCurrentlyConnected) {
                buttons[1] = accountButton;
            } else {
                buttons[1] = connectButton;
            }
        }
    }

    private void handleMouseInput(boolean isPressed) {
        for (Button button : buttons) {
            if (button != null
                    && HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(), button.getBounds())) {
                button.setMousePressed(isPressed);
                break;
            }
        }
    }
}