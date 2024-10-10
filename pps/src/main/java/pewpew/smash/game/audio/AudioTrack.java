package pewpew.smash.game.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class AudioTrack {

    private int id;
    private Clip clip;
    private boolean isLooping;
    private float baseVolume;
    private AudioPlayer.SoundType soundType;

    public AudioTrack(int id, Clip clip, float volume, boolean isLooping, AudioPlayer.SoundType soundType) {
        this.id = id;
        this.clip = clip;
        this.isLooping = isLooping;
        this.baseVolume = volume;
        this.soundType = soundType;
        setVolume(volume);
    }

    public int getID() {
        return this.id;
    }

    public float getBaseVolume() {
        return baseVolume;
    }

    public AudioPlayer.SoundType getSoundType() {
        return soundType;
    }

    public void play() {
        if (this.clip != null) {
            this.clip.setFramePosition(0);
            if (this.isLooping) {
                this.clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                this.clip.start();
            }
        }
    }

    public void stop() {
        if (this.clip != null) {
            this.clip.stop();
        }
    }

    public void setVolume(float volume) {
        if (this.clip != null) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float minVolume = volumeControl.getMinimum();

                float dB;
                if (volume == 0f) {
                    dB = minVolume; // Mute
                } else {
                    dB = (float) (20.0 * Math.log10(volume));
                    dB = Math.max(dB, minVolume);
                }

                volumeControl.setValue(dB);
            } catch (IllegalArgumentException e) {
                System.err.println("Volume control not supported for this clip.");
            }
        }
    }

    public void close() {
        if (this.clip != null) {
            this.clip.close();
            this.clip = null;
        }
    }
}
