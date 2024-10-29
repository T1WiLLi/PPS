package pewpew.smash.game.settings;

import java.io.File;
import java.io.IOException;

import pewpew.smash.engine.GameTime;
import pewpew.smash.engine.RenderingEngine;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.config.ConfigReader;
import pewpew.smash.game.config.SettingsConfig;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.utils.ResourcesLoader;

public class SettingsManager {
    private static SettingsManager instance;
    private SettingsConfig settings;

    public static SettingsManager getInstance() {
        if (instance == null) {
            synchronized (SettingsManager.class) {
                if (instance == null) {
                    instance = new SettingsManager();
                }
            }
        }
        return instance;
    }

    public void saveSettings() {
        updateGameSettings();
        try {
            ConfigReader.writeConfig(new File("pps/src/main/resources/pewpew/smash/config/settings.json"),
                    settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SettingsConfig getSettings() {
        return this.settings;
    }

    public void setSettings(SettingsConfig settings) {
        this.settings = settings;
    }

    private SettingsManager() {
        this.settings = loadSettings();
    }

    private SettingsConfig loadSettings() {
        try {
            return ConfigReader.readConfig(
                    ResourcesLoader.getMiscFile(ResourcesLoader.CONFIG_PATH, "settings.json"),
                    SettingsConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateGameSettings() {
        // Video
        GameTime.getInstance().setFPS_TARGET(this.settings.getVideo().getFps());
        RenderingEngine.getInstance().setAntiAliasing(this.settings.getVideo().isAntiAliasing());
        RenderingEngine.getInstance().setTextAliasing(this.settings.getVideo().isTextAliasing());
        RenderingEngine.getInstance()
                .setRenderingQuality(this.settings.getVideo().getRenderQuality().equalsIgnoreCase("quality"));

        // Audio
        AudioPlayer.getInstance().setGeneralVolume((float) this.settings.getAudio().getGeneralVolume());
        AudioPlayer.getInstance().setSfxVolume((float) this.settings.getAudio().getSfxVolume());
        AudioPlayer.getInstance().setMusicOn(this.settings.getAudio().isMusic());
        AudioPlayer.getInstance().setSfxOn(this.settings.getAudio().isSfx());
        AudioPlayer.getInstance().setUiOn(this.settings.getAudio().isUi());

        // Keys
        GamePad.getInstance().updateBindings(this.settings.getKey().getMovement(), this.settings.getKey().getMisc());
    }
}
