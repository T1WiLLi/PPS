package pewpew.smash.game.overlay;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.config.AboutConfig;
import pewpew.smash.game.config.ConfigReader;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.utils.ResourcesLoader;

public class AboutOverlay extends Overlay {

    private Button backButton;
    private AboutConfig aboutConfig;
    private float scrollPosition = 600f;
    private float scrollSpeed = 0.3f;

    public AboutOverlay(OverlayManager overlayManager, int x, int y, int width, int height) {
        super(overlayManager, x, y, width, height);
        loadConfig();
        loadButtons();
        loadBackground();
    }

    @Override
    public void update() {
        this.backButton.update();
        scrollPosition -= scrollSpeed;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(this.background, this.x, this.y, this.width, this.height);

        if (aboutConfig != null) {
            FontFactory.IMPACT_SMALL.applyFont(canvas);
            int centerX = this.width / 2;

            float currentY = scrollPosition; // Start the scrolling from the current position

            for (AboutConfig.CreditSection section : aboutConfig.getCredits()) {
                currentY = renderCreditSection(canvas, centerX, currentY, section);
            }
        }

        this.backButton.render(canvas);
        FontFactory.resetFont(canvas);
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
        if (HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(), this.backButton.getBounds())) {
            this.backButton.setMousePressed(isPressed);
        }
    }

    @Override
    public void handleMouseMove(MouseEvent e) {
        this.backButton.setMouseOver(false);

        if (HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(), this.backButton.getBounds())) {
            this.backButton.setMouseOver(true);
        }
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

    private float renderCreditSection(Canvas canvas, int centerX, float startY, AboutConfig.CreditSection section) {
        float currentY = startY;

        FontFactory.IMPACT_MEDIUM.applyFont(canvas);
        int sectionWidth = canvas.getGraphics2D().getFontMetrics().stringWidth(section.getSection());
        canvas.renderString(section.getSection(), centerX - (sectionWidth / 2), (int) currentY,
                Color.WHITE);
        currentY += 40;

        FontFactory.IMPACT_SMALL.applyFont(canvas);

        for (AboutConfig.CreditContent content : section.getContent()) {
            int contentWidth = canvas.getGraphics2D().getFontMetrics().stringWidth(content.getTitle());
            canvas.renderString(content.getTitle(), centerX - (contentWidth / 2), (int) currentY,
                    Color.WHITE);
            currentY += 30;

            for (AboutConfig.Artist artist : content.getArtists()) {
                int authorWidth = canvas.getGraphics2D().getFontMetrics().stringWidth(artist.getAuthor());
                canvas.renderString(artist.getAuthor(), centerX - (authorWidth / 2), (int) currentY,
                        Color.WHITE);
                currentY += 20;

                if (artist.getWebsite() != null) {
                    int websiteWidth = canvas.getGraphics2D().getFontMetrics()
                            .stringWidth("Website: " + artist.getWebsite());
                    canvas.renderString("Website: " + artist.getWebsite(), centerX - (websiteWidth / 2), (int) currentY,
                            Color.WHITE);
                    currentY += 20;
                }

                if (artist.getLicense() != null) {
                    int licenseWidth = canvas.getGraphics2D().getFontMetrics()
                            .stringWidth("License: " + artist.getLicense());
                    canvas.renderString("License: " + artist.getLicense(), centerX - (licenseWidth / 2), (int) currentY,
                            Color.WHITE);
                    currentY += 20;
                }

                for (String asset : artist.getAssets()) {
                    int assetWidth = canvas.getGraphics2D().getFontMetrics().stringWidth("- " + asset);
                    canvas.renderString("- " + asset, centerX - (assetWidth / 2), (int) currentY,
                            Color.WHITE);
                    currentY += 20;
                }

                currentY += 30;
            }

            currentY += 20;
        }

        FontFactory.resetFont(canvas);
        return currentY;
    }

    @Override
    public void toggleDisplay() {
        super.toggleDisplay();
        if (isDisplaying()) {
            scrollPosition = this.height;
        }
    }

    private void loadConfig() {
        try {
            aboutConfig = ConfigReader.readConfig(
                    ResourcesLoader.getMiscFile(
                            ResourcesLoader.CONFIG_PATH, "about.json"),
                    AboutConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadButtons() {
        this.backButton = new Button(
                Constants.LEFT_PADDING,
                Constants.TOP_PADDING,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"),
                () -> close());
    }

    private void loadBackground() {
        this.background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "overlay");
    }
}