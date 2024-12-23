package pewpew.smash.game.audio;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.*;

import pewpew.smash.game.config.SettingsConfig.AudioSettings;
import pewpew.smash.game.utils.ResourcesLoader;
import pewpew.smash.game.settings.SettingsManager;

public class AudioPlayer {

    private static AudioPlayer instance;

    private final Map<Integer, AudioTrack> activeTracks;
    private int currentID = 0;

    private final Map<SoundType, Boolean> soundTypeStatus;
    private final Map<SoundType, Float> soundTypeVolumes;

    private float generalVolume = 1.0f; // Default value
    private float sfxVolume = 1.0f; // Default value

    private AudioTrack currentMusicTrack;

    private final Map<AudioClip, AudioData> preloadedAudioData;

    private AudioPlayer() {
        this.activeTracks = new ConcurrentHashMap<>();
        this.soundTypeStatus = new HashMap<>();
        this.soundTypeVolumes = new HashMap<>();
        this.preloadedAudioData = new HashMap<>();

        initAudioSettings(SettingsManager.getInstance().getSettings().getAudio());
        preloadAudioData();
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

    private void initAudioSettings(AudioSettings settings) {
        this.generalVolume = (float) settings.getGeneralVolume();
        this.sfxVolume = (float) settings.getSfxVolume();

        soundTypeStatus.put(SoundType.MUSIC, settings.isMusic());
        soundTypeStatus.put(SoundType.SFX, settings.isSfx());
        soundTypeStatus.put(SoundType.UI, settings.isUi());

        soundTypeVolumes.put(SoundType.MUSIC, generalVolume);
        soundTypeVolumes.put(SoundType.SFX, sfxVolume);
        soundTypeVolumes.put(SoundType.UI, generalVolume);
    }

    private void preloadAudioData() {
        for (AudioClip clipEnum : AudioClip.values()) {
            AudioData audioData = ResourcesLoader.getAudioData(ResourcesLoader.AUDIO_PATH, clipEnum.getFileName());
            if (audioData != null) {
                preloadedAudioData.put(clipEnum, audioData);
            } else {
                System.err.println("Failed to preload audio data: " + clipEnum.getFileName());
            }
        }
    }

    public int play(AudioClip audioClip, float volume, boolean loop, SoundType type) {
        if (!soundTypeStatus.getOrDefault(type, true)) {
            return -1;
        }

        AudioData audioData = preloadedAudioData.get(audioClip);
        if (audioData == null) {
            System.err.println("Audio data not found: " + audioClip.getFileName());
            return -1;
        }

        try {
            Clip clip = AudioSystem.getClip();
            clip.open(audioData.getFormat(), audioData.getData(), 0, audioData.getData().length);

            int id = generateId();
            float adjustedVolume = adjustVolume(volume, type);
            AudioTrack track = new AudioTrack(id, clip, adjustedVolume, loop, type);

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (!loop) {
                        clip.close();
                        activeTracks.remove(id);
                    }
                }
            });

            if (type == SoundType.MUSIC) {
                if (currentMusicTrack != null) {
                    currentMusicTrack.stop();
                }
                currentMusicTrack = track;
            }

            activeTracks.put(id, track);
            track.play();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int playWithSpatialProperties(AudioClip clip, double volume, double pan, boolean loop) {
        AudioData audioData = preloadedAudioData.get(clip);
        if (audioData == null) {
            System.err.println("Audio data not found: " + clip.getFileName());
            return -1;
        }

        try {
            Clip audioClip = AudioSystem.getClip();
            audioClip.open(audioData.getFormat(), audioData.getData(), 0, audioData.getData().length);

            FloatControl volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            FloatControl panControl = (FloatControl) audioClip.getControl(FloatControl.Type.PAN);

            volumeControl.setValue((float) (20.0 * Math.log10(volume)));
            panControl.setValue((float) pan);

            int id = generateId();
            AudioTrack track = new AudioTrack(id, audioClip, (float) volume, loop, SoundType.SFX);

            audioClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (!loop) {
                        audioClip.close();
                        activeTracks.remove(id);
                    }
                }
            });

            activeTracks.put(id, track);
            track.play();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
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

    public void setMusicOn(boolean musicOn) {
        soundTypeStatus.put(SoundType.MUSIC, musicOn);

        if (musicOn) {
            if (currentMusicTrack != null) {
                currentMusicTrack.play();
            } else {
                play(AudioClip.MAIN_THEME, 1.0f, true, SoundType.MUSIC);
            }
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

    public void shutdown() {
        stopAll();
        preloadedAudioData.clear();
    }

    private synchronized int generateId() {
        return currentID++;
    }

    private float adjustVolume(float volume, SoundType type) {
        return volume * soundTypeVolumes.getOrDefault(type, 1.0f);
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
