package pewpew.smash.game.utils;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class ResourcesLoader {

    private static final String BASE_PATH = "/pewpew/smash/";
    // game\src\main\resources\pewpew\smash\misc\RifleScopeCursor.png

    // Image
    public static final String SPRITE_PATH = BASE_PATH + "assets/sprite";
    public static final String BACKGROUND_PATH = BASE_PATH + "assets/background";
    public static final String RANK_PATH = BASE_PATH + "assets/rank";
    public static final String UI_PATH = BASE_PATH + "assets/ui";

    // Audio
    public static final String AUDIO_PATH = BASE_PATH + "audio";

    // JSON
    public static final String CONFIG_PATH = BASE_PATH + "config";

    // IDK YET (It can be FONT, SHADER, etc)
    public static final String MISC_PATH = BASE_PATH + "misc";

    public static BufferedImage getImage(String folderPath, String filename) {
        String path = folderPath + "/" + filename + ".png";
        InputStream is = ResourcesLoader.class.getResourceAsStream(path);
        if (is != null) {
            try {
                return ImageIO.read(is);
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

    public static Clip getAudio(String basePath, String filename) {
        String path = basePath + "/" + filename + ".wav";
        try {
            InputStream is = ResourcesLoader.class.getResourceAsStream(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream getMiscFile(String basePath, String filename) throws FileNotFoundException {
        String path = basePath + "/" + filename;
        InputStream inputStream = ResourcesLoader.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: " + path);
        }
        return inputStream;
    }
}