package pewpew.smash.game.overlay;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Color;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.network.User;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class AccountOverlay extends Overlay {

    private static final int XP_BAR_HEIGHT = 40;
    private static final int XP_BAR_WIDTH = 350;
    private static final Color XP_BAR_COLOR = new Color(0, 120, 180);
    private static final Color XP_BAR_BACKGROUND = Color.GRAY;

    private Button[] buttons = new Button[2];
    private int xpBarX = 350;
    private int xpBarY = 240;

    public AccountOverlay(int x, int y, int width, int height) {
        super(x, y, width, height);
        init();
    }

    @Override
    public void update() {
        for (Button button : buttons) {
            button.update();
        }
    }

    @Override
    public void render(Canvas canvas) {
        renderBackground(canvas);
        renderButtons(canvas);
        renderUserDetails(canvas);
        renderXPBar(canvas);
        FontFactory.resetFont(canvas);
    }

    @Override
    public void handleMousePress(MouseEvent e) {
    }

    @Override
    public void handleMouseRelease(MouseEvent e) {
    }

    @Override
    public void handleMouseMove(MouseEvent e) {
    }

    @Override
    public void handleMouseDrag(MouseEvent e) {
    }

    @Override
    public void handleKeyPress(KeyEvent e) {
    }

    @Override
    public void handleKeyRelease(KeyEvent e) {
    }

    private void renderXPBar(Canvas canvas) {
        canvas.renderRectangle(xpBarX, xpBarY, XP_BAR_WIDTH, XP_BAR_HEIGHT, XP_BAR_BACKGROUND);

        int currentXP = User.getInstance().getRank().getCurrentXp();
        int maxXP = User.getInstance().getRank().getMaxXp();
        int minXp = User.getInstance().getRank().getMinXp();

        int adjustedXP = currentXP - minXp;
        int adjustedMaxXP = maxXP - minXp;
        int progressWidth = (int) ((float) adjustedXP / adjustedMaxXP * XP_BAR_WIDTH);

        canvas.renderRectangle(xpBarX, xpBarY, progressWidth, XP_BAR_HEIGHT, XP_BAR_COLOR);
    }

    private void init() {
        loadBackground();
        loadButtons();
    }

    private void loadBackground() {
        this.background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "account");
    }

    private void loadButtons() {
        this.buttons[0] = new Button(
                Constants.LEFT_PADDING + 30,
                500,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"),
                () -> close());
        this.buttons[1] = new Button(
                Constants.RIGHT_PADDING - 30,
                500,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"),
                () -> System.out.println("Disconnected"));
    }

    private void renderBackground(Canvas canvas) {
        canvas.renderImage(background, x, y, width, height);
    }

    private void renderButtons(Canvas canvas) {
        for (Button button : buttons) {
            button.render(canvas);
        }
    }

    private void renderUserDetails(Canvas canvas) {
        canvas.renderImage(User.getInstance().getRank().getImage(), Constants.LEFT_PADDING, 75, 300, 350);

        FontFactory.IMPACT_LARGE.applyFont(canvas);
        canvas.renderString(User.getInstance().getUsername(), 350, 200);
        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.renderString(User.getInstance().getRank().getCurrentXp() + "XP", 350, 235);
        FontFactory.IMPACT_MEDIUM.applyFont(canvas);
        canvas.renderString(User.getInstance().getRank().getName(), 190 - 50, 450, Color.WHITE);
        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.renderString(User.getInstance().getRank().getDescription(), 190 - 100, 470);
    }
}
