package pewpew.smash.game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.util.HashSet;
import java.util.Set;

public class RayCastingGame extends JPanel {
    private int playerX = 200, playerY = 300, playerSize = 20;
    private int speed = 5;
    private Set<Integer> keysPressed = new HashSet<>();
    private Timer movementTimer;

    private Rectangle[] rectangles = {
            new Rectangle(250, 150, 50, 50),
            new Rectangle(400, 200, 70, 70)
    };
    private Polygon triangle = new Polygon(new int[] { 150, 200, 175 }, new int[] { 100, 150, 180 }, 3);
    private Ellipse2D.Double circle = new Ellipse2D.Double(500, 100, 50, 50);

    public RayCastingGame() {
        setPreferredSize(new Dimension(800, 600));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed.remove(e.getKeyCode());
            }
        });
        setFocusable(true);

        // Timer to continuously update the player's position
        movementTimer = new Timer(15, e -> {
            movePlayer();
            repaint();
        });
        movementTimer.start();
    }

    private void movePlayer() {
        int newX = playerX;
        int newY = playerY;

        // Try moving horizontally
        if (keysPressed.contains(KeyEvent.VK_LEFT))
            newX -= speed;
        if (keysPressed.contains(KeyEvent.VK_RIGHT))
            newX += speed;

        // Check horizontal movement collision
        if (!checkCollision(newX, playerY)) {
            playerX = newX;
        } else {
            newX = playerX; // Reset if collision occurred
        }

        // Try moving vertically
        if (keysPressed.contains(KeyEvent.VK_UP))
            newY -= speed;
        if (keysPressed.contains(KeyEvent.VK_DOWN))
            newY += speed;

        // Check vertical movement collision
        if (!checkCollision(playerX, newY)) {
            playerY = newY;
        }
    }

    private boolean checkCollision(int newX, int newY) {
        // Create a circle representing the player
        Ellipse2D.Double player = new Ellipse2D.Double(
                newX - playerSize / 2.0,
                newY - playerSize / 2.0,
                playerSize,
                playerSize);

        // Check rectangle collisions
        for (Rectangle rect : rectangles) {
            if (player.intersects(rect)) {
                return true;
            }
        }

        // Check triangle collision
        if (checkTriangleCollision(player, triangle)) {
            return true;
        }

        // Check circle collision
        if (checkCircleCollision(newX, newY, circle)) {
            return true;
        }

        return false;
    }

    private boolean checkTriangleCollision(Ellipse2D.Double player, Polygon triangle) {
        // Convert player to a point for simplified collision
        Point2D.Double playerCenter = new Point2D.Double(
                player.getCenterX(),
                player.getCenterY());

        // Check if player center is inside triangle
        if (triangle.contains(playerCenter)) {
            return true;
        }

        // Check distance from player to triangle edges
        for (int i = 0; i < triangle.npoints; i++) {
            int x1 = triangle.xpoints[i];
            int y1 = triangle.ypoints[i];
            int x2 = triangle.xpoints[(i + 1) % triangle.npoints];
            int y2 = triangle.ypoints[(i + 1) % triangle.npoints];

            // Calculate distance from player center to line segment
            double dist = pointToLineDistance(
                    playerCenter.x, playerCenter.y,
                    x1, y1, x2, y2);

            if (dist < playerSize / 2.0) {
                return true;
            }
        }

        return false;
    }

    private boolean checkCircleCollision(int newX, int newY, Ellipse2D.Double targetCircle) {
        // Calculate distance between centers
        double dx = newX - targetCircle.getCenterX();
        double dy = newY - targetCircle.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Check if circles overlap (sum of radii > distance)
        double sumRadii = (playerSize / 2.0) + (targetCircle.getWidth() / 2.0);
        return distance < sumRadii;
    }

    private double pointToLineDistance(double px, double py, double x1, double y1, double x2, double y2) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;

        if (len_sq != 0) {
            param = dot / len_sq;
        }

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw player as a circle
        g2d.setColor(Color.BLUE);
        g2d.fillOval(playerX - playerSize / 2, playerY - playerSize / 2, playerSize, playerSize);

        // Draw shadows for each shape
        drawShapeShadow(g2d, playerX, playerY, rectangles);
        drawShapeShadow(g2d, playerX, playerY, new Shape[] { triangle, circle });

        // Draw objects (hidden by shadows if occluded)
        g2d.setColor(Color.RED);
        for (Rectangle rect : rectangles) {
            g2d.fill(rect);
        }

        g2d.setColor(Color.GREEN);
        g2d.fillPolygon(triangle);

        g2d.setColor(Color.MAGENTA);
        g2d.fill(circle);
    }

    private void drawShapeShadow(Graphics2D g2d, int startX, int startY, Shape[] shapes) {
        int shadowLength = 1000;

        for (Shape shape : shapes) {
            if (shape instanceof Rectangle) {
                drawRectangleShadow(g2d, startX, startY, (Rectangle) shape, shadowLength);
            } else if (shape instanceof Polygon) {
                drawPolygonShadow(g2d, startX, startY, (Polygon) shape, shadowLength);
            } else if (shape instanceof Ellipse2D) {
                drawCircleShadow(g2d, startX, startY, (Ellipse2D) shape, shadowLength);
            }
        }
    }

    private void drawRectangleShadow(Graphics2D g2d, int startX, int startY, Rectangle rect, int shadowLength) {
        int[] xPoints = { rect.x, rect.x + rect.width, rect.x + rect.width, rect.x };
        int[] yPoints = { rect.y, rect.y, rect.y + rect.height, rect.y + rect.height };

        for (int i = 0; i < xPoints.length; i++) {
            int next = (i + 1) % xPoints.length;

            int dx1 = xPoints[i] - startX;
            int dy1 = yPoints[i] - startY;
            int dx2 = xPoints[next] - startX;
            int dy2 = yPoints[next] - startY;

            int[] shadowXPoints = {
                    xPoints[i],
                    xPoints[next],
                    xPoints[next] + dx2 * shadowLength,
                    xPoints[i] + dx1 * shadowLength
            };
            int[] shadowYPoints = {
                    yPoints[i],
                    yPoints[next],
                    yPoints[next] + dy2 * shadowLength,
                    yPoints[i] + dy1 * shadowLength
            };

            // Calculate center point of the shadow quad
            int centerX = (shadowXPoints[0] + shadowXPoints[1] + shadowXPoints[2] + shadowXPoints[3]) / 4;
            int centerY = (shadowYPoints[0] + shadowYPoints[1] + shadowYPoints[2] + shadowYPoints[3]) / 4;

            // Create custom paint for gradient shadow
            createAndFillGradientShadow(g2d, shadowXPoints, shadowYPoints, centerX, centerY, startX, startY);
        }
    }

    private void createAndFillGradientShadow(Graphics2D g2d, int[] xPoints, int[] yPoints,
            int centerX, int centerY, int startX, int startY) {
        // Create a path for the shadow quad
        Path2D shadowPath = new Path2D.Double();
        shadowPath.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < 4; i++) {
            shadowPath.lineTo(xPoints[i], yPoints[i]);
        }
        shadowPath.closePath();

        // Store original paint and composite
        Paint originalPaint = g2d.getPaint();
        Composite originalComposite = g2d.getComposite();

        // Create a radial gradient based on distance from player
        Point2D center = new Point2D.Float(startX, startY);
        float radius = 800.0f; // Adjust this for gradient spread
        float[] fractions = { 0.0f, 0.5f, 1.0f };
        Color[] colors = {
                new Color(0, 0, 0, 255), // Opaque black near player
                new Color(0, 0, 0, 200), // Semi-transparent black
                new Color(0, 0, 0, 128) // More transparent black far from player
        };

        RadialGradientPaint gradientPaint = new RadialGradientPaint(
                center,
                radius,
                fractions,
                colors);

        g2d.setPaint(gradientPaint);
        g2d.fill(shadowPath);

        // Restore original paint and composite
        g2d.setPaint(originalPaint);
        g2d.setComposite(originalComposite);
    }

    private void drawPolygonShadow(Graphics2D g2d, int startX, int startY, Polygon poly, int shadowLength) {
        int[] xPoints = poly.xpoints;
        int[] yPoints = poly.ypoints;
        int nPoints = poly.npoints;

        for (int i = 0; i < nPoints; i++) {
            int next = (i + 1) % nPoints;

            int dx1 = xPoints[i] - startX;
            int dy1 = yPoints[i] - startY;
            int dx2 = xPoints[next] - startX;
            int dy2 = yPoints[next] - startY;

            int[] shadowXPoints = {
                    xPoints[i],
                    xPoints[next],
                    xPoints[next] + dx2 * shadowLength,
                    xPoints[i] + dx1 * shadowLength
            };
            int[] shadowYPoints = {
                    yPoints[i],
                    yPoints[next],
                    yPoints[next] + dy2 * shadowLength,
                    yPoints[i] + dy1 * shadowLength
            };

            // Calculate center point of the shadow quad
            int centerX = (shadowXPoints[0] + shadowXPoints[1] + shadowXPoints[2] + shadowXPoints[3]) / 4;
            int centerY = (shadowYPoints[0] + shadowYPoints[1] + shadowYPoints[2] + shadowYPoints[3]) / 4;

            createAndFillGradientShadow(g2d, shadowXPoints, shadowYPoints, centerX, centerY, startX, startY);
        }
    }

    private void drawCircleShadow(Graphics2D g2d, int startX, int startY, Ellipse2D circle, int shadowLength) {
        int segments = 32;
        double centerX = circle.getCenterX();
        double centerY = circle.getCenterY();
        double radius = circle.getWidth() / 2;

        for (int i = 0; i < segments; i++) {
            double angle1 = 2 * Math.PI * i / segments;
            double angle2 = 2 * Math.PI * (i + 1) / segments;

            double x1 = centerX + radius * Math.cos(angle1);
            double y1 = centerY + radius * Math.sin(angle1);
            double x2 = centerX + radius * Math.cos(angle2);
            double y2 = centerY + radius * Math.sin(angle2);

            double dx1 = x1 - startX;
            double dy1 = y1 - startY;
            double dx2 = x2 - startX;
            double dy2 = y2 - startY;

            int[] shadowXPoints = {
                    (int) x1,
                    (int) x2,
                    (int) (x2 + dx2 * shadowLength),
                    (int) (x1 + dx1 * shadowLength)
            };

            int[] shadowYPoints = {
                    (int) y1,
                    (int) y2,
                    (int) (y2 + dy2 * shadowLength),
                    (int) (y1 + dy1 * shadowLength)
            };

            // Calculate center point of the shadow quad
            int quadCenterX = (shadowXPoints[0] + shadowXPoints[1] + shadowXPoints[2] + shadowXPoints[3]) / 4;
            int quadCenterY = (shadowYPoints[0] + shadowYPoints[1] + shadowYPoints[2] + shadowYPoints[3]) / 4;

            createAndFillGradientShadow(g2d, shadowXPoints, shadowYPoints, quadCenterX, quadCenterY, startX, startY);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Raycasting Game");
        RayCastingGame panel = new RayCastingGame();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
