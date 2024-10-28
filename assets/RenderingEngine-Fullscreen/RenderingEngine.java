package doctrina;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class RenderingEngine {

    private static RenderingEngine instance;
    private JPanel panel;
    private BufferedImage bufferedImage;
    private Screen screen;

    public static RenderingEngine getInstance() {
        if (instance == null) {
            instance = new RenderingEngine();
        }
        return instance;
    }

    public Screen getScreen() {
        return screen;
    }

    public void start() {
        screen.start();
    }

    public void stop() {
        screen.stop();
    }

    public void addKeyListener(KeyListener keyListener) {
        panel.addKeyListener(keyListener);
    }

    public Canvas buildCanvas() {

        Graphics2D buffer = bufferedImage.createGraphics();
        buffer.setRenderingHints(buildRenderingHints());
        return new Canvas(buffer);
    }

    public void drawOnScreen() {
        Graphics2D graphics = (Graphics2D) panel.getGraphics();
        graphics.drawImage(bufferedImage, 0, 0,
                screen.getWidth(), screen.getHeight(),
                0, 0,
                bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        Toolkit.getDefaultToolkit().sync();
        graphics.dispose();
    }

    private void initializePanel() {
        panel = new JPanel();
        panel.setBackground(Color.BLUE);
        panel.setFocusable(true);
        panel.setDoubleBuffered(true);
        screen.setPanel(panel);
    }

    private RenderingHints buildRenderingHints() {
        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        return hints;
    }

    private RenderingEngine() {
        initializeScreen();
        initializePanel();
    }

    private void initializeScreen() {
        screen = new Screen();
        screen.setSize(800, 600);
        bufferedImage = new BufferedImage(800, 600,
                BufferedImage.TYPE_INT_RGB);
    }
}
