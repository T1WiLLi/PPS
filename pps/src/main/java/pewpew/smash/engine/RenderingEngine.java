package pewpew.smash.engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import lombok.Getter;

public class RenderingEngine {

    private static RenderingEngine instance;
    @Getter
    private Screen screen;
    private JPanel panel;
    private BufferedImage buffer;

    @Getter
    private final double[] scale = new double[2];

    private Object antiAliasingHint = RenderingHints.VALUE_ANTIALIAS_ON;
    private Object textAliasingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
    private Object renderingQualityHint = RenderingHints.VALUE_RENDER_QUALITY;

    public static RenderingEngine getInstance() {
        if (instance == null) {
            synchronized (RenderingEngine.class) {
                if (instance == null) {
                    instance = new RenderingEngine();
                }
            }
        }
        return instance;
    }

    public Canvas getCanvas() {
        Graphics2D graphics = this.buffer.createGraphics();
        graphics.setRenderingHints(getHints());
        return new Canvas(graphics);
    }

    public void renderCanvasOnScreen() {
        Graphics2D graphics = null;
        while (graphics == null) {
            graphics = (Graphics2D) this.panel.getGraphics();
        }
        graphics.scale(scale[0], scale[1]);
        graphics.drawImage(this.buffer, 0, 0, null);
        Toolkit.getDefaultToolkit().sync();
        graphics.dispose();
    }

    public void start() {
        screen.start();
    }

    public void stop() {
        screen.end();
    }

    public void addKeyListener(KeyListener listener) {
        panel.addKeyListener(listener);
    }

    public void addMouseListener(MouseListener listener) {
        panel.addMouseListener(listener);
        panel.addMouseMotionListener((MouseMotionListener) listener);
    }

    private RenderingEngine() {
        initScreen();
        initPanel();
    }

    private void initScreen() {
        screen = new Screen();
        screen.setTitle("PewPewSmash");
        screen.setSize(800, 600);
        buffer = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
    }

    private void initPanel() {
        panel = new JPanel();
        panel.setBackground(Color.BLUE);
        panel.setFocusable(true);
        panel.setDoubleBuffered(true);
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                updateScale();
            }
        });

        screen.setPanel(panel);
        updateScale();
    }

    private void updateScale() {
        scale[0] = (double) panel.getWidth() / 800;
        scale[1] = (double) panel.getHeight() / 600;
    }

    private RenderingHints getHints() {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, antiAliasingHint);
        hints.put(RenderingHints.KEY_RENDERING, renderingQualityHint);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, textAliasingHint);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return hints;
    }

    // Methods to dynamically change rendering settings
    public void setAntiAliasing(boolean enabled) {
        this.antiAliasingHint = enabled ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
    }

    public void setTextAliasing(boolean enabled) {
        this.textAliasingHint = enabled ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
    }

    public void setRenderingQuality(boolean highQuality) {
        this.renderingQualityHint = highQuality ? RenderingHints.VALUE_RENDER_QUALITY
                : RenderingHints.VALUE_RENDER_SPEED;
    }
}
