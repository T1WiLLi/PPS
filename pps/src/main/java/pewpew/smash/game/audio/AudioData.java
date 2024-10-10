package pewpew.smash.game.audio;

import javax.sound.sampled.AudioFormat;

public class AudioData {
    private final AudioFormat format;
    private final byte[] data;

    public AudioData(AudioFormat format, byte[] data) {
        this.format = format;
        this.data = data;
    }

    public AudioFormat getFormat() {
        return format;
    }

    public byte[] getData() {
        return data;
    }
}
