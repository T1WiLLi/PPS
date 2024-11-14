package pewpew.smash.engine;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.VolatileImage;
import javax.swing.JPanel;

import lombok.Getter;

public class RenderingEngine {

    private static RenderingEngine instance;
    @Getter
    private DisplayManager displayManager;
    private JPanel panel;
    private VolatileImage buffer;
    private Graphics2D bufferGraphics;

    private final AffineTransform defaultTransform = new AffineTransform();

    @Getter
    private final double[] scale = new double[2];
    @Getter
    private double systemScaleFactor;

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
        defaultTransform.setToIdentity();
        bufferGraphics.setTransform(defaultTransform);
        bufferGraphics.setRenderingHints(getHints());
        bufferGraphics.scale(scale[0], scale[1]);

        return new Canvas(bufferGraphics);
    }

    public void renderCanvasOnScreen() {
        do {
            int returnCode = buffer.validate(panel.getGraphicsConfiguration());
            if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                updateBuffer();
            }
            Graphics panelGraphics = panel.getGraphics();
            if (panelGraphics != null) {
                try {
                    int panelWidth = panel.getWidth();
                    int panelHeight = panel.getHeight();
                    panelGraphics.drawImage(buffer, 0, 0, panelWidth, panelHeight, null);
                    Toolkit.getDefaultToolkit().sync();
                } finally {
                    panelGraphics.dispose();
                }
            }
        } while (buffer.contentsLost());
    }

    public void start() {
        displayManager.showWindow();
        panel.requestFocusInWindow();
        initBuffer();
    }

    public void stop() {
        displayManager.hideWindow();
    }

    public void addKeyListener(KeyListener listener) {
        panel.addKeyListener(listener);
    }

    public void addMouseListener(MouseListener listener) {
        panel.addMouseListener(listener);
        panel.addMouseMotionListener((MouseMotionListener) listener);
    }

    public void requestFocusOnPanel() {
        panel.requestFocusInWindow();
    }

    private RenderingEngine() {
        initDisplayManager();
        initPanel();
    }

    private void initDisplayManager() {
        displayManager = new DisplayManager("PewPewSmash", 800, 600);
        panel = new JPanel(); // Use standard JPanel
        displayManager.getFrame().getContentPane().add(panel);
    }

    private void initPanel() {
        panel.setBackground(Color.BLUE);
        panel.setFocusable(true);
        panel.setDoubleBuffered(true);
        panel.requestFocusInWindow();
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                updateScale();
                updateBuffer();
            }
        });
        updateScale();
    }

    private void initBuffer() {
        createVolatileImage();
    }

    private void createVolatileImage() {
        GraphicsConfiguration gc = panel.getGraphicsConfiguration();
        int width = panel.getWidth();
        int height = panel.getHeight();
        if (width <= 0 || height <= 0) {
            width = 800;
            height = 600;
        }
        buffer = gc.createCompatibleVolatileImage(width, height);
        bufferGraphics = buffer.createGraphics();
    }

    void updateScale() {
        int width = panel.getWidth();
        int height = panel.getHeight();
        if (width == 0 || height == 0) {
            width = 800;
            height = 600;
        }

        systemScaleFactor = displayManager.getSystemScalingFactor();

        scale[0] = (double) width / 800 * systemScaleFactor;
        scale[1] = (double) height / 600 * systemScaleFactor;
    }

    void updateBuffer() {
        createVolatileImage();
    }

    private RenderingHints getHints() {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, antiAliasingHint);
        hints.put(RenderingHints.KEY_RENDERING, renderingQualityHint);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, textAliasingHint);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        return hints;
    }

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
