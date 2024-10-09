package pewpew.smash.game.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import pewpew.smash.database.models.Player;
import pewpew.smash.database.services.AuthService;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.GameTime;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.network.User;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.ui.TextField;
import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.utils.ResourcesLoader;

public class ConnectionOverlay extends Overlay {

    // REFACTOR THIS

    private Button backButton;
    private Button loginButton;
    private TextField usernameField;
    private TextField passwordField;
    private String errorMessage = "";
    private long errorMessageTimestamp = 0;

    private BufferedImage loginPanel;

    public ConnectionOverlay(OverlayManager overlayManager, int x, int y, int width, int height) {
        super(overlayManager, x, y, width, height);
        loadBackground();
        loadButtons();
        loadTextFields();
    }

    @Override
    public void update() {
        backButton.update();
        loginButton.update();
        usernameField.update();
        passwordField.update();

        if (!errorMessage.isEmpty() && (GameTime.getElapsedTime() - errorMessageTimestamp) >= 1500) {
            errorMessage = "";
        }
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(background, x, y, width, height);
        canvas.renderImage(loginPanel, width / 2 - 150, height / 2 - 200, 300, 400);
        backButton.render(canvas);
        loginButton.render(canvas);
        usernameField.render(canvas);
        passwordField.render(canvas);

        if (!errorMessage.isEmpty()) {
            canvas.renderRectangle(270, 500, 265, 50, Color.WHITE);
            canvas.renderRectangleBorder(270, 500, 265, 50, 2, Color.BLACK);
            canvas.setFont(new Font("Impact", Font.TRUETYPE_FONT, 18));
            canvas.renderString(errorMessage, 280, 533, Color.RED);
            canvas.resetFont();
        }
    }

    @Override
    public void handleMousePress(MouseEvent e) {
        handleMouseInput(true);
    }

    @Override
    public void handleMouseRelease(MouseEvent e) {
        handleMouseInput(false);
    }

    private void handleMouseInput(boolean isPressed) {
        if (isPressed) {
            handleButtonPress(backButton, isPressed);
            handleButtonPress(loginButton, isPressed);
            handleTextFieldFocus();
        }
    }

    private void handleButtonPress(Button button, boolean isPressed) {
        if (HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(), button.getBounds())) {
            button.setMousePressed(true);
        } else {
            button.setMousePressed(false);
        }
    }

    private void handleTextFieldFocus() {
        boolean usernameFocused = HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(),
                usernameField.getBounds());
        boolean passwordFocused = HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(),
                passwordField.getBounds());

        usernameField.setFocused(usernameFocused);
        passwordField.setFocused(passwordFocused);
    }

    @Override
    public void handleMouseMove(MouseEvent e) {
        backButton.setMouseOver(
                HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(), backButton.getBounds()));
        loginButton.setMouseOver(
                HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(), loginButton.getBounds()));
    }

    @Override
    public void handleMouseDrag(MouseEvent e) {

    }

    @Override
    public void handleKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            if (usernameField.isFocused()) {
                usernameField.setFocused(false);
                passwordField.setFocused(true);
            } else if (passwordField.isFocused()) {
                passwordField.setFocused(false);
                usernameField.setFocused(true);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (passwordField.isFocused()) {
                loginButton.getOnClick().run();
            }
        } else {
            if (usernameField.isFocused()) {
                usernameField.keyPressed(e);
            } else if (passwordField.isFocused()) {
                passwordField.keyPressed(e);
            }
        }
    }

    @Override
    public void handleKeyRelease(KeyEvent e) {

    }

    private void loadButtons() {
        this.backButton = new Button(
                Constants.LEFT_PADDING,
                Constants.TOP_PADDING,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"),
                () -> close());

        this.loginButton = new Button(
                300,
                425,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/loginButton"),
                this::login);
    }

    private void loadBackground() {
        this.background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "login");
        this.loginPanel = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "login-panel");
    }

    private void loadTextFields() {
        this.usernameField = new TextField(297, 290, 210, 48);
        this.passwordField = new TextField(297, 360, 210, 48);
    }

    private void login() {
        if (!usernameField.getText().trim().isEmpty() && !passwordField.getText().trim().isEmpty()) {
            errorMessage = "";

            Player player = new AuthService().authenticate(usernameField.getText().trim(),
                    passwordField.getText().trim());
            if (player != null) {
                User.getInstance().build(player.getUsername(), player.getRank(), player.getAchievements());
                User.getInstance().setConnected(true);
                close();
            } else {
                errorMessage = "Invalid username or password.";
                errorMessageTimestamp = GameTime.getElapsedTime();
            }
        } else {
            errorMessage = "Both fields must be filled to log in.";
            errorMessageTimestamp = GameTime.getElapsedTime();
        }
    }
}