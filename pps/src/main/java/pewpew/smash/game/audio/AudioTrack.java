package pewpew.smash.game.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class AudioTrack {

    private static final int FADE_OUT_DURATION = 3000;

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
            if (soundType == AudioPlayer.SoundType.MUSIC) {
                fadeOut();
            } else {
                clip.stop();
            }
        }
    }

    private void fadeOut() {
        new Thread(() -> {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float minVolume = volumeControl.getMinimum();
                float currentVolume = (float) Math.pow(10.0, volumeControl.getValue() / 20.0);

                int steps = FADE_OUT_DURATION / 150;
                float stepDecrease = currentVolume / steps;

                for (int i = 0; i < steps; i++) {
                    currentVolume -= stepDecrease;
                    if (currentVolume <= 0) {
                        currentVolume = 0;
                        break;
                    }
                    float dB = (float) (20.0 * Math.log10(currentVolume));
                    volumeControl.setValue(Math.max(dB, minVolume));
                    Thread.sleep(400);
                }

                volumeControl.setValue(minVolume);
                clip.stop();
            } catch (Exception e) {
                System.err.println("Error during fade-out: " + e.getMessage());
            }
        }).start();
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
