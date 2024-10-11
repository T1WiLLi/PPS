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
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.network.User;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.ui.TextField;
import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.utils.ResourcesLoader;

public class ConnectionOverlay extends Overlay {

    private Button backButton;
    private Button loginButton;
    private TextField usernameField;
    private TextField passwordField;
    private String errorMessage = "";
    private long errorMessageTimestamp = 0;

    private BufferedImage loginPanel;

    public ConnectionOverlay(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadBackground();
        loadButtons();
        loadTextFields();
    }

    @Override
    public void update() {
        updateButtons();
        updateTextFields();
        clearErrorMessageIfExpired();
    }

    @Override
    public void render(Canvas canvas) {
        renderBackground(canvas);
        renderLoginPanel(canvas);
        renderButtons(canvas);
        renderTextFields(canvas);
        renderErrorMessage(canvas);
    }

    @Override
    public void handleMousePress(MouseEvent e) {
        handleMouseInput(true);
    }

    @Override
    public void handleMouseRelease(MouseEvent e) {
        handleMouseInput(false);
    }

    @Override
    public void handleMouseMove(MouseEvent e) {
        updateMouseOverState();
    }

    @Override
    public void handleMouseDrag(MouseEvent e) {
    }

    @Override
    public void handleKeyPress(KeyEvent e) {
        handleKeyInput(e);
    }

    @Override
    public void handleKeyRelease(KeyEvent e) {
    }

    private void updateButtons() {
        backButton.update();
        loginButton.update();
    }

    private void updateTextFields() {
        usernameField.update();
        passwordField.update();
    }

    private void clearErrorMessageIfExpired() {
        if (!errorMessage.isEmpty() && (GameTime.getElapsedTime() - errorMessageTimestamp) >= 1500) {
            errorMessage = "";
        }
    }

    private void renderBackground(Canvas canvas) {
        canvas.renderImage(background, x, y, width, height);
    }

    private void renderLoginPanel(Canvas canvas) {
        canvas.renderImage(loginPanel, width / 2 - 150, height / 2 - 200, 300, 400);
    }

    private void renderButtons(Canvas canvas) {
        backButton.render(canvas);
        loginButton.render(canvas);
    }

    private void renderTextFields(Canvas canvas) {
        usernameField.render(canvas);
        passwordField.render(canvas);
    }

    private void renderErrorMessage(Canvas canvas) {
        if (!errorMessage.isEmpty()) {
            canvas.renderRectangle(270, 500, 265, 50, Color.WHITE);
            canvas.renderRectangleBorder(270, 500, 265, 50, 2, Color.BLACK);
            canvas.setFont(new Font("Impact", Font.TRUETYPE_FONT, 18));
            canvas.renderString(errorMessage, 280, 533, Color.RED);
            canvas.resetFont();
        }
    }

    private void handleMouseInput(boolean isPressed) {
        if (isPressed) {
            pressButton(backButton, isPressed);
            pressButton(loginButton, isPressed);
            updateTextFieldFocus();
        }
    }

    private void pressButton(Button button, boolean isPressed) {
        if (HelpMethods.isIn(button.getBounds())) {
            button.setMousePressed(isPressed);
        } else {
            button.setMousePressed(false);
        }
    }

    private void updateTextFieldFocus() {
        boolean usernameFocused = HelpMethods.isIn(usernameField.getBounds());
        boolean passwordFocused = HelpMethods.isIn(passwordField.getBounds());

        usernameField.setFocused(usernameFocused);
        passwordField.setFocused(passwordFocused);
    }

    private void updateMouseOverState() {
        backButton.setMouseOver(
                HelpMethods.isIn(backButton.getBounds()));
        loginButton.setMouseOver(
                HelpMethods.isIn(loginButton.getBounds()));
    }

    private void handleKeyInput(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            toggleTextFieldFocus();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (passwordField.isFocused()) {
                loginButton.getOnClick().run();
            }
        } else {
            handleTextFieldKeyPress(e);
        }
    }

    private void toggleTextFieldFocus() {
        if (usernameField.isFocused()) {
            usernameField.setFocused(false);
            passwordField.setFocused(true);
        } else if (passwordField.isFocused()) {
            passwordField.setFocused(false);
            usernameField.setFocused(true);
        }
    }

    private void handleTextFieldKeyPress(KeyEvent e) {
        if (usernameField.isFocused()) {
            usernameField.keyPressed(e);
        } else if (passwordField.isFocused()) {
            passwordField.keyPressed(e);
        }
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
                this::attemptLogin);
    }

    private void loadBackground() {
        this.background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "login");
        this.loginPanel = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "login-panel");
    }

    private void loadTextFields() {
        this.usernameField = new TextField(297, 290, 210, 48);
        this.passwordField = new TextField(297, 360, 210, 48);
    }

    private void attemptLogin() {
        if (!usernameField.getText().trim().isEmpty() && !passwordField.getText().trim().isEmpty()) {
            errorMessage = "";

            Player player = new AuthService().authenticate(usernameField.getText().trim(),
                    passwordField.getText().trim());
            if (player != null) {
                User.getInstance().build(player.getUsername(), player.getRank(), player.getAchievements());
                User.getInstance().setConnected(true);
                close();
            } else {
                setErrorMessage("Invalid username or password.");
            }
        } else {
            setErrorMessage("Both fields must be filled to log in.");
        }
    }

    private void setErrorMessage(String message) {
        errorMessage = message;
        errorMessageTimestamp = GameTime.getElapsedTime();
    }
}
