package pewpew.smash.game.audio;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.sound.sampled.Clip;

import pewpew.smash.game.config.SettingsConfig.AudioSettings;

public class AudioPlayer {

    private static AudioPlayer instance;

    private final Map<Integer, AudioTrack> activeTracks;
    private int currentID = 0;

    private Map<SoundType, Boolean> soundTypeStatus;
    private Map<SoundType, Float> soundTypeVolumes;

    private float generalVolume = 1.0f; // Default value
    private float sfxVolume = 1.0f; // Default value

    private AudioTrack currentMusicTrack;

    private AudioPlayer() {
        this.activeTracks = new ConcurrentHashMap<>();

        soundTypeStatus = new HashMap<>();
        soundTypeStatus.put(SoundType.MUSIC, true);
        soundTypeStatus.put(SoundType.SFX, true);
        soundTypeStatus.put(SoundType.UI, true);

        soundTypeVolumes = new HashMap<>();
        soundTypeVolumes.put(SoundType.MUSIC, generalVolume);
        soundTypeVolumes.put(SoundType.SFX, sfxVolume);
        soundTypeVolumes.put(SoundType.UI, generalVolume);
    }

    public static AudioPlayer getInstance() {
        if (instance == null) {
            synchronized (AudioPlayer.class) {
                if (instance == null) {
                    instance = new AudioPlayer();
                }
            }
        }
        return instance;
    }

    public void initAudioSettings(AudioSettings settings) {
        this.generalVolume = (float) settings.getGeneralVolume();
        this.sfxVolume = (float) settings.getSfxVolume();

        soundTypeStatus.put(SoundType.MUSIC, settings.isMusic());
        soundTypeStatus.put(SoundType.SFX, settings.isSfx());
        soundTypeStatus.put(SoundType.UI, settings.isUi());

        soundTypeVolumes.put(SoundType.MUSIC, generalVolume);
        soundTypeVolumes.put(SoundType.SFX, sfxVolume);
        soundTypeVolumes.put(SoundType.UI, generalVolume);

        updateAllTrackVolumes();
    }

    public int play(Clip clip, float volume, boolean loop, SoundType type) {
        if (!soundTypeStatus.getOrDefault(type, true)) {
            return -1;
        }

        int id = generateId();
        float adjustedVolume = adjustVolume(volume, type);
        AudioTrack track = new AudioTrack(id, clip, adjustedVolume, loop, type);

        if (type == SoundType.MUSIC) {
            if (currentMusicTrack != null) {
                currentMusicTrack.stop();
            }
            currentMusicTrack = track;
        }

        activeTracks.put(id, track);
        track.play();
        return id;
    }

    public void stop(int trackId) {
        AudioTrack track = activeTracks.get(trackId);
        if (track != null) {
            track.stop();
            activeTracks.remove(trackId);

            if (track.getSoundType() == SoundType.MUSIC) {
                currentMusicTrack = null;
            }
        }
    }

    public void stopAll() {
        for (AudioTrack track : activeTracks.values()) {
            track.stop();
        }
        activeTracks.clear();
        currentMusicTrack = null;
    }

    public void shutdown() {
        stopAll();
    }

    public void setMusicOn(boolean musicOn) {
        soundTypeStatus.put(SoundType.MUSIC, musicOn);

        if (musicOn && currentMusicTrack != null) {
            currentMusicTrack.play();
        } else if (!musicOn && currentMusicTrack != null) {
            currentMusicTrack.stop();
        }
    }

    public void setSfxOn(boolean sfxOn) {
        soundTypeStatus.put(SoundType.SFX, sfxOn);
    }

    public void setUiOn(boolean uiOn) {
        soundTypeStatus.put(SoundType.UI, uiOn);
    }

    public void setGeneralVolume(float volume) {
        this.generalVolume = volume;
        soundTypeVolumes.put(SoundType.MUSIC, generalVolume);
        soundTypeVolumes.put(SoundType.UI, generalVolume);

        updateTracksVolume(SoundType.MUSIC);
        updateTracksVolume(SoundType.UI);
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = volume;
        soundTypeVolumes.put(SoundType.SFX, sfxVolume);

        updateTracksVolume(SoundType.SFX);
    }

    private synchronized int generateId() {
        return currentID++;
    }

    private float adjustVolume(float volume, SoundType type) {
        return volume * soundTypeVolumes.getOrDefault(type, 1.0f);
    }

    private void updateAllTrackVolumes() {
        for (AudioTrack track : activeTracks.values()) {
            float adjustedVolume = adjustVolume(track.getBaseVolume(), track.getSoundType());
            track.setVolume(adjustedVolume);
        }
    }

    private void updateTracksVolume(SoundType type) {
        for (AudioTrack track : activeTracks.values()) {
            if (track.getSoundType() == type) {
                float adjustedVolume = adjustVolume(track.getBaseVolume(), type);
                track.setVolume(adjustedVolume);
            }
        }
    }

    public enum SoundType {
        MUSIC,
        SFX,
        UI
    }
}
