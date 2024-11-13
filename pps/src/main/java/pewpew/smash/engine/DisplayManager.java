package pewpew.smash.engine;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Taskbar;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lombok.Getter;
import pewpew.smash.game.utils.ResourcesLoader;

public class DisplayManager {

    @Getter
    private JFrame frame;
    @Getter
    private JPanel panel;
    @Getter
    private boolean isFullscreen = false;
    private GraphicsDevice device;
    private int windowedWidth;
    private int windowedHeight;

    public DisplayManager(String title, int width, int height) {
        this.windowedWidth = width;
        this.windowedHeight = height;
        initPanel();
        initFrame(title, width, height);
        initGraphicsDevice();
    }

    private void initPanel() {
        this.panel = new JPanel();
        this.panel.setBackground(Color.BLUE);
        this.panel.setFocusable(true);
        this.panel.setDoubleBuffered(true);
    }

    private void initFrame(String title, int width, int height) {
        this.frame = new JFrame(title);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setIgnoreRepaint(true);
        this.frame.getContentPane().add(this.panel);
        this.frame.setSize(width, height);
        this.frame.setLocationRelativeTo(null);
        this.frame.setFocusable(false);

        setIcon();
    }

    private void initGraphicsDevice() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.device = env.getDefaultScreenDevice();
    }

    public void setFullscreen(boolean fullscreen) {
        if (isFullscreen == fullscreen) {
            return;
        }

        this.frame.dispose();

        if (fullscreen) {
            this.frame.setUndecorated(true);
            this.frame.setResizable(false);
            this.device.setFullScreenWindow(frame);
            this.isFullscreen = true;
        } else {
            this.device.setFullScreenWindow(null);
            this.frame.setUndecorated(false);
            this.frame.setResizable(true);
            this.frame.setSize(windowedWidth, windowedHeight);
            this.frame.setLocationRelativeTo(null);
            this.isFullscreen = false;
        }

        setIcon();
        this.frame.setVisible(true);
        RenderingEngine.getInstance().requestFocusOnPanel();
    }

    public void showWindow() {
        this.frame.setVisible(true);
    }

    public void hideWindow() {
        this.frame.setVisible(false);
    }

    public void setTitle(String title) {
        this.frame.setTitle(title);
    }

    public void setSize(int width, int height) {
        this.windowedWidth = width;
        this.windowedHeight = height;
        this.frame.setSize(width, height);
        this.frame.setLocationRelativeTo(null);
    }

    private void setIcon() {
        BufferedImage icon = ResourcesLoader.getImage(ResourcesLoader.MISC_PATH, "icon");
        frame.setIconImage(icon);

        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                try {
                    taskbar.setIconImage(icon);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Taskbar icon image feature is not supported on this platform.");
            }
        } else {
            System.err.println("Taskbar is not supported on this platform.");
        }
    }

}
