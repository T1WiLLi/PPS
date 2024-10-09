package pewpew.smash.engine;

import javax.swing.*;

import pewpew.smash.game.utils.ResourcesLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Screen {

    private GraphicsDevice device;
    private JFrame frame;
    private DisplayMode windowedDisplayMode;
    private boolean isFullscreenMode;
    private Cursor rifleScopeCursor;

    public Screen() {
        initializeFrame();
        initializeRifleScopeCursor();
        initializeDevice();
        setIcon();
    }

    public synchronized void showRifleScope() {
        frame.setCursor(rifleScopeCursor);
    }

    public synchronized void showDefaultCursor() {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public synchronized void showPressedCursor() {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void fullscreen() {
        if (!isFullscreenMode && device.isFullScreenSupported()) {
            frame.dispose();
            frame.setUndecorated(true);
            frame.setResizable(false);
            device.setFullScreenWindow(frame);
            setBestDisplayMode();
            isFullscreenMode = true;
            frame.setVisible(true);
        }
    }

    public void windowed() {
        if (isFullscreenMode && device.isFullScreenSupported()) {
            device.setFullScreenWindow(null);
            frame.dispose();
            frame.setUndecorated(false);
            frame.setResizable(true);
            frame.setSize(windowedDisplayMode.getWidth(), windowedDisplayMode.getHeight());
            frame.setLocationRelativeTo(null);
            isFullscreenMode = false;
            frame.setVisible(true);
        }
    }

    public void toggleFullscreen() {
        if (isFullscreenMode) {
            windowed();
        } else {
            fullscreen();
        }
    }

    protected void setPanel(JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);
        frame.revalidate();
    }

    protected void setTitle(String title) {
        frame.setTitle(title);
    }

    protected void setSize(int width, int height) {
        boolean frameIsVisible = frame.isVisible();
        if (frameIsVisible) {
            frame.setVisible(false);
        }
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        if (frameIsVisible) {
            frame.setVisible(true);
        }
    }

    protected void start() {
        frame.setVisible(true);
    }

    protected void end() {
        frame.dispose();
    }

    private void setIcon() {
        Image icon = ResourcesLoader.getImage(ResourcesLoader.MISC_PATH, "icon");

        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            try {
                taskbar.setIconImage(icon);
            } catch (UnsupportedOperationException e) {
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        frame.setIconImage(icon);
    }

    private void initializeFrame() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIgnoreRepaint(true);
        frame.setUndecorated(true);
    }

    private void initializeRifleScopeCursor() {
        BufferedImage scopeImage = ResourcesLoader.getImage(ResourcesLoader.MISC_PATH, "RifleScopeCursor");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Point hotSpot = new Point(0, 0);
        rifleScopeCursor = toolkit.createCustomCursor(scopeImage, hotSpot, "RifleScopeCursor");
    }

    private void initializeDevice() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = environment.getDefaultScreenDevice();
        windowedDisplayMode = device.getDisplayMode();
    }

    private void setBestDisplayMode() {
        DisplayMode bestMode = getBestDisplayMode();
        if (bestMode != null) {
            device.setDisplayMode(bestMode);
        } else {
            System.err.println("No suitable fullscreen display mode found.");
        }
    }

    private DisplayMode getBestDisplayMode() {
        DisplayMode[] displayModes = device.getDisplayModes();
        DisplayMode bestMode = null;
        for (DisplayMode mode : displayModes) {
            if (mode.getWidth() >= 800 && mode.getHeight() >= 600
                    && mode.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI) {
                bestMode = mode;
            }
        }
        return bestMode;
    }
}