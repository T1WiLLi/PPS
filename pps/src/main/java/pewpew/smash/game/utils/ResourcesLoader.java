package pewpew.smash.game.utils;

import java.util.concurrent.ConcurrentHashMap;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import pewpew.smash.game.audio.AudioData;

public class ResourcesLoader {

    private static final ConcurrentHashMap<String, BufferedImage> imageCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AudioData> audioCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, byte[]> miscCache = new ConcurrentHashMap<>();

    private static final String BASE_PATH = "/pewpew/smash/";
    // Image
    public static final String SPRITE_PATH = BASE_PATH + "assets/sprite";
    public static final String BACKGROUND_PATH = BASE_PATH + "assets/background";
    public static final String RANK_PATH = BASE_PATH + "assets/rank";
    public static final String UI_PATH = BASE_PATH + "assets/ui";
    public static final String PREVIEW_PATH = SPRITE_PATH + "/previews";
    public static final String HUD_PATH = SPRITE_PATH + "/hud";
    public static final String ENTITY_SPRITE = SPRITE_PATH + "/entities";

    // Audio
    public static final String AUDIO_PATH = BASE_PATH + "audio";
    public static final String SFX_PATH = AUDIO_PATH + "/sfx";

    // JSON
    public static final String CONFIG_PATH = BASE_PATH + "config";

    // IDK YET (It can be FONT, SHADER, etc)
    public static final String MISC_PATH = BASE_PATH + "misc";

    public static BufferedImage getImage(String folderPath, String filename) {
        String path = folderPath + "/" + filename + ".png";

        BufferedImage cachedImage = imageCache.get(path);
        if (cachedImage != null) {
            return cachedImage;
        }

        InputStream is = ResourcesLoader.class.getResourceAsStream(path);
        if (is != null) {
            try {
                BufferedImage image = ImageIO.read(is);
                imageCache.put(path, image);
                return image;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Image not found : " + path);
        }
        return null;
    }

    public static AudioData getAudioData(String basePath, String filename) {
        String path = basePath + "/" + filename + ".wav";

        AudioData cachedAudio = audioCache.get(path);
        if (cachedAudio != null) {
            return cachedAudio;
        }

        try (InputStream is = ResourcesLoader.class.getResourceAsStream(path)) {
            if (is != null) {
                try (BufferedInputStream bis = new BufferedInputStream(is)) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis);
                    AudioFormat format = audioStream.getFormat();
                    byte[] audioData = audioStream.readAllBytes();
                    audioStream.close();

                    AudioData newAudioData = new AudioData(format, audioData);
                    audioCache.put(path, newAudioData);
                    return newAudioData;
                }
            } else {
                System.err.println("Audio file not found: " + path);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InputStream getMiscFile(String basePath, String filename) throws FileNotFoundException {
        String path = basePath + "/" + filename;

        byte[] cachedData = miscCache.get(path);
        if (cachedData != null) {
            return new java.io.ByteArrayInputStream(cachedData);
        }

        try (InputStream is = ResourcesLoader.class.getResourceAsStream(path)) {
            if (is != null) {
                byte[] data = is.readAllBytes();
                miscCache.put(path, data);
                return new ByteArrayInputStream(data);
            } else {
                throw new FileNotFoundException("Resource not found: " + path);
            }
        } catch (IOException e) {
            throw new FileNotFoundException("Failed to read resource: " + path);
        }
    }
}