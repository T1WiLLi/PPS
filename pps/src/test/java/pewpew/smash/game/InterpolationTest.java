package pewpew.smash.game;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class InterpolationTest extends JPanel {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private BufferedImage[] testImages;
    private Object currentInterpolation = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
    private long lastTime = System.nanoTime();
    private int frameCount = 0;
    private double fps = 0;
    private boolean showSideBySide = false;
    private double zoomFactor = 1.0;
    private double rotation = 0.0;

    public InterpolationTest() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        createTestImages();
        setupKeyBindings();

        // Start animation timer
        new Timer(16, e -> repaint()).start();
    }

    private void createTestImages() {
        testImages = new BufferedImage[4];

        // Test Image 1: Checkerboard pattern
        testImages[0] = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = testImages[0].createGraphics();
        for (int x = 0; x < 32; x += 8) {
            for (int y = 0; y < 32; y += 8) {
                g2d.setColor(((x + y) / 8 % 2 == 0) ? Color.WHITE : Color.BLACK);
                g2d.fillRect(x, y, 8, 8);
            }
        }
        g2d.dispose();

        // Test Image 2: Diagonal lines
        testImages[1] = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        g2d = testImages[1].createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 32, 32);
        g2d.setColor(Color.BLACK);
        for (int i = -32; i < 32; i += 4) {
            g2d.drawLine(i, 0, i + 32, 32);
        }
        g2d.dispose();

        // Test Image 3: Gradients
        testImages[2] = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        g2d = testImages[2].createGraphics();
        GradientPaint gradient = new GradientPaint(0, 0, Color.RED, 32, 32, Color.BLUE);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 32, 32);
        g2d.dispose();

        // Test Image 4: Text
        testImages[3] = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        g2d = testImages[3].createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 64, 64);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Test", 5, 35);
        g2d.dispose();
    }

    private void setupKeyBindings() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_1:
                        currentInterpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                        break;
                    case KeyEvent.VK_2:
                        currentInterpolation = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
                        break;
                    case KeyEvent.VK_3:
                        currentInterpolation = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
                        break;
                    case KeyEvent.VK_SPACE:
                        showSideBySide = !showSideBySide;
                        break;
                    case KeyEvent.VK_UP:
                        zoomFactor = Math.min(8.0, zoomFactor * 1.1);
                        break;
                    case KeyEvent.VK_DOWN:
                        zoomFactor = Math.max(0.1, zoomFactor / 1.1);
                        break;
                    case KeyEvent.VK_LEFT:
                        rotation -= 5;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rotation += 5;
                        break;
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Calculate FPS
        frameCount++;
        long currentTime = System.nanoTime();
        long delta = currentTime - lastTime;
        if (delta > 1_000_000_000) {
            fps = frameCount / (delta / 1_000_000_000.0);
            frameCount = 0;
            lastTime = currentTime;
        }

        // Fill background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        if (showSideBySide) {
            drawSideBySideComparison(g2d);
        } else {
            drawSingleInterpolation(g2d);
        }

        // Draw controls info
        drawControlsInfo(g2d);
    }

    private void drawSideBySideComparison(Graphics2D g2d) {
        Object[] methods = {
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        };

        String[] labels = { "Nearest Neighbor", "Bilinear", "Bicubic" };

        int spacing = 10;
        int methodWidth = (WIDTH - (methods.length + 1) * spacing) / methods.length;

        for (int m = 0; m < methods.length; m++) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, methods[m]);

            int x = spacing + m * (methodWidth + spacing);

            // Draw label
            g2d.setColor(Color.WHITE);
            g2d.drawString(labels[m], x, 20);

            // Draw each test image
            for (int i = 0; i < testImages.length; i++) {
                int y = 40 + i * (methodWidth + spacing);

                AffineTransform originalTransform = g2d.getTransform();
                g2d.translate(x + methodWidth / 2, y + methodWidth / 2);
                g2d.rotate(Math.toRadians(rotation));
                g2d.scale(zoomFactor, zoomFactor);
                g2d.drawImage(testImages[i],
                        -testImages[i].getWidth() / 2,
                        -testImages[i].getHeight() / 2,
                        null);
                g2d.setTransform(originalTransform);
            }
        }
    }

    private void drawSingleInterpolation(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, currentInterpolation);

        int spacing = 10;
        int imageSize = (WIDTH - 5 * spacing) / 4;

        for (int i = 0; i < testImages.length; i++) {
            int x = spacing + i * (imageSize + spacing);
            int y = HEIGHT / 2 - imageSize / 2;

            AffineTransform originalTransform = g2d.getTransform();
            g2d.translate(x + imageSize / 2, y + imageSize / 2);
            g2d.rotate(Math.toRadians(rotation));
            g2d.scale(zoomFactor, zoomFactor);
            g2d.drawImage(testImages[i],
                    -testImages[i].getWidth() / 2,
                    -testImages[i].getHeight() / 2,
                    null);
            g2d.setTransform(originalTransform);
        }
    }

    private void drawControlsInfo(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(10, HEIGHT - 140, 300, 130);
        g2d.setColor(Color.WHITE);
        int y = HEIGHT - 120;
        g2d.drawString(String.format("FPS: %.1f", fps), 20, y);
        g2d.drawString("Current: " + getCurrentInterpolationName(), 20, y + 20);
        g2d.drawString("Controls:", 20, y + 40);
        g2d.drawString("1-3: Change interpolation method", 20, y + 60);
        g2d.drawString("Space: Toggle side-by-side comparison", 20, y + 80);
        g2d.drawString("Arrow keys: Rotate (←→) and Zoom (↑↓)", 20, y + 100);
    }

    private String getCurrentInterpolationName() {
        if (currentInterpolation == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
            return "Nearest Neighbor";
        else if (currentInterpolation == RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            return "Bilinear";
        else
            return "Bicubic";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Interpolation Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new InterpolationTest());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}