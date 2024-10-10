package pewpew.smash.game.audio;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.sound.sampled.Clip;
import pewpew.smash.game.settings.SettingsManager;

public class AudioPlayer {

    private static AudioPlayer instance;

    private final Map<Integer, AudioTrack> activeTracks;
    private int currentID = 0;

    private final Map<SoundType, Boolean> soundTypeStatus;
    private final Map<SoundType, Float> soundTypeVolumes;

    private float generalVolume = (float) SettingsManager.getInstance().getSettings().getAudio().getGeneralVolume();
    private float sfxVolume = (float) SettingsManager.getInstance().getSettings().getAudio().getSfxVolume();

    private AudioTrack currentMusicTrack;

    private AudioPlayer() {
        this.activeTracks = new ConcurrentHashMap<>();

        soundTypeStatus = new HashMap<>();
        soundTypeStatus.put(SoundType.MUSIC, SettingsManager.getInstance().getSettings().getAudio().isMusic());
        soundTypeStatus.put(SoundType.SFX, SettingsManager.getInstance().getSettings().getAudio().isSfx());
        soundTypeStatus.put(SoundType.UI, SettingsManager.getInstance().getSettings().getAudio().isUi());

        soundTypeVolumes = new HashMap<>();
        soundTypeVolumes.put(SoundType.MUSIC, generalVolume);
        soundTypeVolumes.put(SoundType.SFX, sfxVolume);
        soundTypeVolumes.put(SoundType.UI, generalVolume);
    }

    public static AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    public int play(Clip clip, float volume, boolean loop, SoundType type) {
        if (!soundTypeStatus.getOrDefault(type, false)) {
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

        for (AudioTrack track : activeTracks.values()) {
            if (track.getSoundType() == SoundType.MUSIC || track.getSoundType() == SoundType.UI) {
                track.setVolume(adjustVolume(track.getBaseVolume(), track.getSoundType()));
            }
        }
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = volume;
        soundTypeVolumes.put(SoundType.SFX, sfxVolume);

        for (AudioTrack track : activeTracks.values()) {
            if (track.getSoundType() == SoundType.SFX) {
                track.setVolume(adjustVolume(track.getBaseVolume(), track.getSoundType()));
            }
        }
    }

    private synchronized int generateId() {
        return currentID++;
    }

    private float adjustVolume(float volume, SoundType type) {
        return soundTypeVolumes.getOrDefault(type, 1.0f);
    }

    public enum SoundType {
        MUSIC,
        SFX,
        UI
    }
}
