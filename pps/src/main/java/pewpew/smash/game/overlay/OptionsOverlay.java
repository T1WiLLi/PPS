package pewpew.smash.game.overlay;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.audio.AudioPlayer.SoundType;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.settings.SettingsManager;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.ui.Checkbox;
import pewpew.smash.game.ui.Cycler;
import pewpew.smash.game.ui.Slider;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OptionsOverlay extends Overlay {

    private Button backButton, saveButton;
    private List<KeyBindButton> keyBindButtons;
    private Checkbox antiAliasingCheckbox, textAliasingCheckbox, musicCheckbox, sfxCheckbox, uiCheckbox;
    private Cycler fpsCycler, renderQualityCycler;
    private String awaitingKeyBind = null;

    private Slider generalVolumeSlider, sfxVolumeSlider;
    private boolean isDraggingGeneralVolumeSlider;
    private boolean isDraggingSfxVolumeSlider;

    public OptionsOverlay(OverlayManager overlayManager, int x, int y, int width, int height) {
        super(overlayManager, x, y, width, height);
        this.keyBindButtons = new ArrayList<>();
        loadButtons();
        loadCheckboxes();
        loadCyclers();
        background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "overlay");
    }

    @Override
    public void update() {
        backButton.update();
        saveButton.update();
        keyBindButtons.forEach(Button::update);
        antiAliasingCheckbox.update();
        textAliasingCheckbox.update();
        musicCheckbox.update();
        sfxCheckbox.update();
        uiCheckbox.update();
        fpsCycler.update();
        renderQualityCycler.update();
        generalVolumeSlider.update();
        sfxVolumeSlider.update();

        if (generalVolumeSlider.isMouseOver() && generalVolumeSlider.isMousePressed()) {
            SettingsManager.getInstance().getSettings().getAudio().setGeneralVolume(generalVolumeSlider.getValue());
        }

        if (sfxVolumeSlider.isMouseOver() && sfxVolumeSlider.isMousePressed()) {
            SettingsManager.getInstance().getSettings().getAudio().setSfxVolume(sfxVolumeSlider.getValue());
        }
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(background, x, y, width, height);
        canvas.setColor(Color.WHITE);

        renderSectionTitles(canvas);
        canvas.renderLine(0, 120, width, 120, 2, Color.WHITE);
        backButton.render(canvas);
        saveButton.render(canvas);

        canvas.renderLine(width / 2, 120, width / 2, height, 2, Color.WHITE);

        renderSectionHeadline(canvas, "Key Bindings", width / 2 - 110 - width / 4);
        renderSectionHeadline(canvas, "Game Options", width / 2 - 95 + width / 4);

        canvas.renderLine(0, 210, width, 210, 2, Color.WHITE);

        FontFactory.IMPACT_MEDIUM.applyFont(canvas);
        renderKeyBindings(canvas, Constants.LEFT_PADDING - 15, 240);
        renderVideoSettings(canvas, width / 2 + 25, 240);
        renderAudioSettings(canvas, width / 2 + 25, 420);
        FontFactory.resetFont(canvas);
    }

    @Override
    public void handleMousePress(MouseEvent e) {
        if (awaitingKeyBind != null)
            return;

        handleButtonPress();
        handleCheckboxPress(e);
        handleCyclerPress(e);
        handleSliderPress();
    }

    private void handleSliderPress() {
        if (isMouseInside(generalVolumeSlider.getBounds())) {
            generalVolumeSlider.setMousePressed(true);
            generalVolumeSlider.setMouseOver(true);
            isDraggingGeneralVolumeSlider = true;
        }

        if (isMouseInside(sfxVolumeSlider.getBounds())) {
            sfxVolumeSlider.setMousePressed(true);
            sfxVolumeSlider.setMouseOver(true);
            isDraggingSfxVolumeSlider = true;
        }
    }

    @Override
    public void handleMouseRelease(MouseEvent e) {
        backButton.setMousePressed(false);
        saveButton.setMousePressed(false);
        keyBindButtons.forEach(button -> button.setMousePressed(false));

        if (isDraggingGeneralVolumeSlider) {
            generalVolumeSlider.setMousePressed(false);
            isDraggingGeneralVolumeSlider = false;
        }

        if (isDraggingSfxVolumeSlider) {
            sfxVolumeSlider.setMousePressed(false);
            isDraggingSfxVolumeSlider = false;
        }
    }

    @Override
    public void handleMouseMove(MouseEvent e) {
        handleButtonHover();

        if (!isDraggingGeneralVolumeSlider) {
            generalVolumeSlider.setMouseOver(isMouseInside(generalVolumeSlider.getBounds()));
        }

        if (!isDraggingSfxVolumeSlider) {
            sfxVolumeSlider.setMouseOver(isMouseInside(sfxVolumeSlider.getBounds()));
        }
    }

    @Override
    public void handleKeyPress(KeyEvent e) {
        if (awaitingKeyBind != null) {
            String keyName = KeyEvent.getKeyText(e.getKeyCode());
            for (KeyBindButton button : keyBindButtons) {
                if (button.getAction().equals(awaitingKeyBind)) {
                    button.setCurrentKey(keyName);
                    updateKeyBinding(awaitingKeyBind, keyName);
                    break;
                }
            }
            awaitingKeyBind = null;
        }
    }

    @Override
    public void handleKeyRelease(KeyEvent e) {
    }

    @Override
    public void handleMouseDrag(MouseEvent e) {
    }

    private void renderKeyBindings(Canvas canvas, int x, int currentY) {
        for (KeyBindButton button : keyBindButtons) {
            canvas.renderString(button.getAction(), x, currentY);
            canvas.renderString(button.getCurrentKey(), x + 120, currentY);
            button.render(canvas);

            canvas.renderLine(0, currentY + button.getHeight() / 2, width / 2,
                    currentY + button.getHeight() / 2, 1, Color.WHITE);
            currentY += 43;
        }

        canvas.renderLine(x + 235, 210, x + 235, height, 1, Color.WHITE);
        canvas.renderLine(x + 110, 210, x + 110, height, 1, Color.WHITE);
    }

    private void renderVideoSettings(Canvas canvas, int x, int currentY) {
        canvas.renderString("Video Settings :", x, currentY);

        canvas.renderString("FPS:", x, currentY + 30);
        canvas.renderString(fpsCycler.getCurrentCycle(), x + 60, currentY + 30);
        fpsCycler.render(canvas);

        canvas.renderString("Anti-Aliasing:", x, currentY + 60);
        antiAliasingCheckbox.render(canvas);

        canvas.renderString("Text-Aliasing:", x, currentY + 100);
        textAliasingCheckbox.render(canvas);

        canvas.renderString("Render Quality:", x, currentY + 130);
        canvas.renderString(renderQualityCycler.getCurrentCycle(), x + 155, currentY + 130);
        renderQualityCycler.render(canvas);

        canvas.renderLine(width / 2, currentY + 144, width, currentY + 144, 1, Color.WHITE);
    }

    private void renderAudioSettings(Canvas canvas, int x, int currentY) {
        canvas.renderString("Audio Settings", x, currentY);

        canvas.renderString("General Volume: ", x, currentY + 30);
        generalVolumeSlider.render(canvas);

        canvas.renderString("SFX Volume: ", x, currentY + 73, Color.WHITE);
        sfxVolumeSlider.render(canvas);

        canvas.renderString("Music :", x, currentY + 105, Color.WHITE);
        musicCheckbox.render(canvas);

        canvas.renderString("SFX: ", x, currentY + 140);
        sfxCheckbox.render(canvas);

        canvas.renderString("UI: ", x, currentY + 175);
        uiCheckbox.render(canvas);
    }

    private void renderSectionTitles(Canvas canvas) {
        FontFactory.IMPACT_X_LARGE.applyFont(canvas);
        canvas.renderString("Options", width / 2 - 75, 100);
        FontFactory.resetFont(canvas);
    }

    private void renderSectionHeadline(Canvas canvas, String label, int xPos) {
        FontFactory.IMPACT_LARGE.applyFont(canvas);
        canvas.renderString(label, xPos, 180);
        FontFactory.resetFont(canvas);
    }

    private void handleButtonPress() {
        if (isMouseInside(backButton.getBounds())) {
            backButton.setMousePressed(true);
        }
        if (isMouseInside(saveButton.getBounds())) {
            saveButton.setMousePressed(true);
        }
        for (KeyBindButton button : keyBindButtons) {
            if (isMouseInside(button.getBounds())) {
                button.setMousePressed(true);
            }
        }
    }

    private void handleCheckboxPress(MouseEvent e) {
        if (isMouseInside(antiAliasingCheckbox.getBounds())) {
            handleCheckboxClick(antiAliasingCheckbox,
                    isChecked -> SettingsManager.getInstance().getSettings().getVideo().setAntiAliasing(isChecked));
        }
        if (isMouseInside(textAliasingCheckbox.getBounds())) {
            handleCheckboxClick(textAliasingCheckbox,
                    isChecked -> SettingsManager.getInstance().getSettings().getVideo().setTextAliasing(isChecked));
        }

        if (isMouseInside(musicCheckbox.getBounds())) {
            handleCheckboxClick(musicCheckbox,
                    isChecked -> SettingsManager.getInstance().getSettings().getAudio().setMusic(isChecked));
        }

        if (isMouseInside(sfxCheckbox.getBounds())) {
            handleCheckboxClick(sfxCheckbox,
                    isChecked -> SettingsManager.getInstance().getSettings().getAudio().setSfx(isChecked));
        }

        if (isMouseInside(uiCheckbox.getBounds())) {
            handleCheckboxClick(uiCheckbox,
                    isChecked -> SettingsManager.getInstance().getSettings().getAudio().setUi(isChecked));
        }
    }

    private void handleCheckboxClick(Checkbox checkbox, Consumer<Boolean> setter) {
        checkbox.setChecked(!checkbox.isChecked());
        setter.accept(checkbox.isChecked());
        AudioPlayer.getInstance().play(ResourcesLoader.getAudio(ResourcesLoader.AUDIO_PATH, "checked"), 0.95f,
                false, SoundType.UI);
    }

    private void handleCyclerPress(MouseEvent e) {
        if (isMouseInside(fpsCycler.getBounds())) {
            fpsCycler.nextCycle();
            SettingsManager.getInstance().getSettings().getVideo()
                    .setFps(Integer.parseInt(fpsCycler.getCurrentCycle()));
            AudioPlayer.getInstance().play(ResourcesLoader.getAudio(ResourcesLoader.AUDIO_PATH, "swapped"), 0.95f,
                    false, SoundType.UI);

        }
        if (isMouseInside(renderQualityCycler.getBounds())) {
            renderQualityCycler.nextCycle();
            SettingsManager.getInstance().getSettings().getVideo()
                    .setRenderQuality(renderQualityCycler.getCurrentCycle().toLowerCase());
            AudioPlayer.getInstance().play(ResourcesLoader.getAudio(ResourcesLoader.AUDIO_PATH, "swapped"), 0.95f,
                    false, SoundType.UI);

        }
    }

    private void startKeyBindingProcess(String action, String currentKey) {
        awaitingKeyBind = action;
    }

    private void handleButtonHover() {
        backButton.setMouseOver(isMouseInside(backButton.getBounds()));
        saveButton.setMouseOver(isMouseInside(saveButton.getBounds()));

        for (KeyBindButton button : keyBindButtons) {
            button.setMouseOver(isMouseInside(button.getBounds()));
        }
    }

    private void updateKeyBinding(String action, String newKey) {
        if (SettingsManager.getInstance().getSettings().getKey().getMovement().containsKey(action)) {
            SettingsManager.getInstance().getSettings().getKey().getMovement().put(action, newKey);
        } else if (SettingsManager.getInstance().getSettings().getKey().getMisc().containsKey(action)) {
            SettingsManager.getInstance().getSettings().getKey().getMisc().put(action, newKey);
        }
    }

    private void loadButtons() {
        sfxVolumeSlider = new Slider(width / 2 + 200, 465, 180, 40,
                (float) SettingsManager.getInstance().getSettings().getAudio().getSfxVolume());
        generalVolumeSlider = new Slider(width / 2 + 200, 422, 180, 40,
                (float) SettingsManager.getInstance().getSettings().getAudio().getGeneralVolume());
        backButton = new Button(Constants.LEFT_PADDING, Constants.TOP_PADDING,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"), this::close);
        saveButton = new Button(Constants.RIGHT_PADDING, Constants.TOP_PADDING,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/saveButton"),
                () -> SettingsManager.getInstance().saveSettings());

        loadKeyBindButtons();
    }

    private void loadKeyBindButtons() {
        BufferedImage setButtonSprite = ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/setButton");
        int startY = 240;
        int spacingY = 43;
        int currentY = startY;
        int buttonWidth = 125;
        int buttonHeight = 30;
        int x = Constants.LEFT_PADDING - 15 + 245;

        Map<String, String> allKeyBindings = new LinkedHashMap<>();
        allKeyBindings.putAll(SettingsManager.getInstance().getSettings().getKey().getMovement());
        allKeyBindings.putAll(SettingsManager.getInstance().getSettings().getKey().getMisc());

        for (Map.Entry<String, String> entry : allKeyBindings.entrySet()) {
            KeyBindButton keyBindButton = new KeyBindButton(
                    x, currentY - buttonHeight / 2 - 5, buttonWidth, buttonHeight,
                    setButtonSprite, entry.getKey(), entry.getValue(), this::startKeyBindingProcess);
            keyBindButtons.add(keyBindButton);
            currentY += spacingY;
        }
    }

    private void loadCheckboxes() {
        antiAliasingCheckbox = new Checkbox(width / 2 + 175, 280);
        antiAliasingCheckbox.setChecked(SettingsManager.getInstance().getSettings().getVideo().isAntiAliasing());

        textAliasingCheckbox = new Checkbox(width / 2 + 175, 320);
        textAliasingCheckbox.setChecked(SettingsManager.getInstance().getSettings().getVideo().isTextAliasing());

        musicCheckbox = new Checkbox(width / 2 + 120, 500);
        musicCheckbox.setChecked(SettingsManager.getInstance().getSettings().getAudio().isMusic());

        sfxCheckbox = new Checkbox(width / 2 + 120, 535);
        sfxCheckbox.setChecked(SettingsManager.getInstance().getSettings().getAudio().isSfx());

        uiCheckbox = new Checkbox(width / 2 + 120, 570);
        uiCheckbox.setChecked(SettingsManager.getInstance().getSettings().getAudio().isUi());
    }

    private void loadCyclers() {
        fpsCycler = new Cycler(width / 2 + 125, 248, 25, 25, new String[] { "30", "60", "144" },
                String.valueOf(SettingsManager.getInstance().getSettings().getVideo().getFps()).trim());
        renderQualityCycler = new Cycler(width / 2 + 255, 350, 25, 25, new String[] { "Quality", "Fast" },
                SettingsManager.getInstance().getSettings().getVideo().getRenderQuality().trim());
    }

    private static class KeyBindButton extends Button {
        private final String action;
        private String currentKey;

        public KeyBindButton(int x, int y, int width, int height, BufferedImage spriteSheet,
                String action, String currentKey, BiConsumer<String, String> onClick) {
            super(x, y, width, height, spriteSheet, () -> onClick.accept(action, currentKey));
            this.action = action;
            this.currentKey = currentKey;
        }

        public String getAction() {
            return action;
        }

        public String getCurrentKey() {
            return currentKey;
        }

        public void setCurrentKey(String key) {
            this.currentKey = key;
        }
    }
}
