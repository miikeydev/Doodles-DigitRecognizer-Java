import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DrawingPanel extends JPanel {
    private final int GRID_SIZE = 28;
    private final int PIXEL_SIZE = 20;
    private int[][] pixels = new int[GRID_SIZE][GRID_SIZE];
    private int oldX = -1;
    private int oldY = -1;
    private float brushRadius = 4f;
    private float coreRadius = 0.1f;

    public DrawingPanel() {
        setPreferredSize(new Dimension(GRID_SIZE * PIXEL_SIZE, GRID_SIZE * PIXEL_SIZE));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                oldX = e.getX() / PIXEL_SIZE;
                oldY = e.getY() / PIXEL_SIZE;
                drawPixel(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                oldX = -1;
                oldY = -1;
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drawPixel(e);
            }
        });
    }

    private void drawPixel(MouseEvent e) {
        int x = e.getX() / PIXEL_SIZE;
        int y = e.getY() / PIXEL_SIZE;
        if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
            if (oldX != -1 && oldY != -1) {
                drawLine(oldX, oldY, x, y);
            }
            oldX = x;
            oldY = y;
            repaint();
        }
    }

    private float distanceToLineSegment(int x1, int y1, int x2, int y2, int px, int py) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int d2 = dx*dx + dy*dy;
        float t = ((px - x1) * dx + (py - y1) * dy) / (float) d2;
        t = Math.max(0, Math.min(1, t));
        float lx = x1 + t * dx;
        float ly = y1 + t * dy;
        float dxp = px - lx;
        float dyp = py - ly;
        return (float) Math.sqrt(dxp*dxp + dyp*dyp);
    }

    private float smoothstep(float minVal, float maxVal, float t) {
        t = Math.min(1, Math.max(0, (t-minVal) / (maxVal - minVal)));
        return t * t * (3 - 2 * t);
    }

    private void drawLine(int x1, int y1, int x2, int y2) {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                float distance = distanceToLineSegment(x1, y1, x2, y2, x, y);
                if (distance < brushRadius) {
                    float intensity;
                    if (distance < coreRadius) {
                        intensity = 1;  // Maximum intensity within the core radius
                    } else {
                        // Adjusting the smoothstep function for a more gradual fade
                        intensity = 1 - smoothstep(coreRadius, brushRadius, distance);
                        // Applying a square function to further soften the intensity
                        intensity = (float) Math.pow(intensity, 2);
                    }
                    // Ensuring that the pixel intensity does not exceed 255
                    pixels[y][x] = (int) Math.min(255, 255 * Math.max(intensity, pixels[y][x] / 255.0));
                }
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                g.setColor(new Color(pixels[y][x], pixels[y][x], pixels[y][x]));
                g.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
        }
    }

    public int[][] getPixels() {
        return pixels;
    }


    public void clear() {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                pixels[y][x] = 0;
            }
        }
        repaint();
    }
}
