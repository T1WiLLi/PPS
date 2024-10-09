package pewpew.smash.game.config;

import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsConfig {
    private VideoSettings video;
    private KeySettings key;
    private MouseSettings mouse;
    private AudioSettings audio;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SettingsConfig that = (SettingsConfig) o;
        return Objects.equals(video, that.video) &&
                Objects.equals(key, that.key) &&
                Objects.equals(mouse, that.mouse) &&
                Objects.equals(audio, that.audio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(video, key, mouse, audio);
    }

    @Getter
    @Setter
    public static class VideoSettings {
        private int fps;
        private boolean antiAliasing;
        private boolean textAliasing;
        private String renderQuality;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            VideoSettings that = (VideoSettings) o;
            return fps == that.fps &&
                    antiAliasing == that.antiAliasing &&
                    textAliasing == that.textAliasing &&
                    Objects.equals(renderQuality, that.renderQuality);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fps, antiAliasing, textAliasing, renderQuality);
        }
    }

    @Getter
    @Setter
    public static class KeySettings {
        private Map<String, String> movement;
        private Map<String, String> misc;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            KeySettings that = (KeySettings) o;
            return Objects.equals(movement, that.movement) &&
                    Objects.equals(misc, that.misc);
        }

        @Override
        public int hashCode() {
            return Objects.hash(movement, misc);
        }
    }

    @Getter
    @Setter
    public static class MouseSettings {
        private Map<String, String> buttons;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            MouseSettings that = (MouseSettings) o;
            return Objects.equals(buttons, that.buttons);
        }

        @Override
        public int hashCode() {
            return Objects.hash(buttons);
        }
    }

    @Getter
    @Setter
    public static class AudioSettings {
        private double generalVolume;
        private double sfxVolume;
        private boolean music;
        private boolean sfx;
        private boolean ui;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            AudioSettings that = (AudioSettings) o;
            return Double.compare(that.generalVolume, generalVolume) == 0 &&
                    Double.compare(that.sfxVolume, sfxVolume) == 0 &&
                    music == that.music &&
                    sfx == that.sfx &&
                    ui == that.ui;
        }

        @Override
        public int hashCode() {
            return Objects.hash(generalVolume, sfxVolume, music, sfx, ui);
        }
    }
}