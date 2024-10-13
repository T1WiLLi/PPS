package pewpew.smash.game.world;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WorldDisplayerHelper {
    public static void displayWorld(BufferedImage worldImage) {
        JFrame frame = new JFrame("World Viewer");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                double scaleX = (double) getWidth() / worldImage.getWidth();
                double scaleY = (double) getHeight() / worldImage.getHeight();
                double scaleFactor = Math.min(scaleX, scaleY);

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int newWidth = (int) (worldImage.getWidth() * scaleFactor);
                int newHeight = (int) (worldImage.getHeight() * scaleFactor);

                int x = (getWidth() - newWidth) / 2;
                int y = (getHeight() - newHeight) / 2;

                g2d.drawImage(worldImage, x, y, newWidth, newHeight, null);
            }
        };

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        WorldGenerator worldGenerator = new WorldGenerator();
        long startTime = System.currentTimeMillis();
        displayWorld(WorldGenerator.getWorldImage(worldGenerator.getWorldData()));
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to display world: " + (endTime - startTime) + " milliseconds.");

    }
}