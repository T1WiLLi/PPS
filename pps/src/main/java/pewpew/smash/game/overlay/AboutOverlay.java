package pewpew.smash.game.overlay;

import java.awt.Color;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.config.AboutConfig;
import pewpew.smash.game.config.ConfigReader;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class AboutOverlay extends Overlay {

    private Button backButton;
    private AboutConfig aboutConfig;
    private float scrollPosition = 600f;
    private float scrollSpeed = 0.1f;

    public AboutOverlay(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadConfig();
        loadButtons();
        loadBackground();
    }

    @Override
    public void update() {
        updateScrollPosition();
        this.backButton.update();
    }

    @Override
    public void render(Canvas canvas) {
        renderBackground(canvas);
        renderCredits(canvas);
        this.backButton.render(canvas);
        FontFactory.resetFont(canvas);
    }

    @Override
    public void activate() {
        super.activate();
        if (isDisplaying()) {
            scrollPosition = this.height;
        }
    }

    private void updateScrollPosition() {
        scrollPosition -= scrollSpeed;
    }

    private void renderBackground(Canvas canvas) {
        canvas.renderImage(this.background, this.x, this.y, this.width, this.height);
    }

    private void renderCredits(Canvas canvas) {
        if (aboutConfig != null) {
            FontFactory.IMPACT_SMALL.applyFont(canvas);
            int centerX = this.width / 2;

            float currentY = scrollPosition;

            for (AboutConfig.CreditSection section : aboutConfig.getCredits()) {
                currentY = renderCreditSection(canvas, centerX, currentY, section);
            }
        }
    }

    private float renderCreditSection(Canvas canvas, int centerX, float startY, AboutConfig.CreditSection section) {
        float currentY = startY;

        renderSectionTitle(canvas, centerX, currentY, section);
        currentY += 40;

        FontFactory.IMPACT_SMALL.applyFont(canvas);

        for (AboutConfig.CreditContent content : section.getContent()) {
            currentY = renderCreditContent(canvas, centerX, currentY, content);
        }

        FontFactory.resetFont(canvas);
        return currentY;
    }

    private void renderSectionTitle(Canvas canvas, int centerX, float currentY, AboutConfig.CreditSection section) {
        FontFactory.IMPACT_MEDIUM.applyFont(canvas);
        int sectionWidth = canvas.getGraphics2D().getFontMetrics().stringWidth(section.getSection());
        canvas.renderString(section.getSection(), centerX - (sectionWidth / 2), (int) currentY, Color.WHITE);
    }

    private float renderCreditContent(Canvas canvas, int centerX, float currentY, AboutConfig.CreditContent content) {
        int contentWidth = canvas.getGraphics2D().getFontMetrics().stringWidth(content.getTitle());
        canvas.renderString(content.getTitle(), centerX - (contentWidth / 2), (int) currentY, Color.WHITE);
        currentY += 30;

        for (AboutConfig.Artist artist : content.getArtists()) {
            currentY = renderArtist(canvas, centerX, currentY, artist);
        }

        currentY += 20;
        return currentY;
    }

    private float renderArtist(Canvas canvas, int centerX, float currentY, AboutConfig.Artist artist) {
        int authorWidth = canvas.getGraphics2D().getFontMetrics().stringWidth(artist.getAuthor());
        canvas.renderString(artist.getAuthor(), centerX - (authorWidth / 2), (int) currentY, Color.WHITE);
        currentY += 20;

        if (artist.getWebsite() != null) {
            currentY = renderArtistWebsite(canvas, centerX, currentY, artist);
        }

        if (artist.getLicense() != null) {
            currentY = renderArtistLicense(canvas, centerX, currentY, artist);
        }

        for (String asset : artist.getAssets()) {
            currentY = renderAsset(canvas, centerX, currentY, asset);
        }

        currentY += 30;
        return currentY;
    }

    private float renderArtistWebsite(Canvas canvas, int centerX, float currentY, AboutConfig.Artist artist) {
        int websiteWidth = canvas.getGraphics2D().getFontMetrics().stringWidth("Website: " + artist.getWebsite());
        canvas.renderString("Website: " + artist.getWebsite(), centerX - (websiteWidth / 2), (int) currentY,
                Color.WHITE);
        return currentY + 20;
    }

    private float renderArtistLicense(Canvas canvas, int centerX, float currentY, AboutConfig.Artist artist) {
        int licenseWidth = canvas.getGraphics2D().getFontMetrics().stringWidth("License: " + artist.getLicense());
        canvas.renderString("License: " + artist.getLicense(), centerX - (licenseWidth / 2), (int) currentY,
                Color.WHITE);
        return currentY + 20;
    }

    private float renderAsset(Canvas canvas, int centerX, float currentY, String asset) {
        int assetWidth = canvas.getGraphics2D().getFontMetrics().stringWidth("- " + asset);
        canvas.renderString("- " + asset, centerX - (assetWidth / 2), (int) currentY, Color.WHITE);
        return currentY + 20;
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
