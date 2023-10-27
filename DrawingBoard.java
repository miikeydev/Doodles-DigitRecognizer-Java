/* import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.CvType;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.PixelGrabber;
import javax.swing.*;

public class DrawingBoard extends JPanel implements MouseListener, MouseMotionListener {

    private Image image;
    private Graphics2D graphics;
    private int prevX, prevY;
    private boolean eraseMode = false;
    private int paintBrushSize = 16;
    private JButton clearButton;
    private JButton predictButton;
    private JFrame frame;

    public DrawingBoard() {
        addMouseListener(this);
        addMouseMotionListener(this);
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
        initializeFrame();
    }

    private void initializeFrame() {
        frame = new JFrame("Drawing Board");
        frame.add(this, BorderLayout.CENTER);


        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clear());


        predictButton = new JButton("Predict");
        predictButton.addActionListener(e -> predict());


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        buttonPanel.add(predictButton);


        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setVisible(true);
    }

    protected void paintComponent(Graphics g) {
        if (image == null) {
            image = createImage(getWidth(), getHeight());
            graphics = (Graphics2D) image.getGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clear();
        }
        g.drawImage(image, 0, 0, null);
    }

    public void clear() {
        graphics.setPaint(Color.WHITE);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.setPaint(Color.BLACK);
        repaint();
    }

    public void predict() {
        int width = getWidth();
        int height = getHeight();
        int[] pixelData = new int[width * height];
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, width, height, pixelData, 0, width);
        try {
            pixelGrabber.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("Interrupted waiting for pixels!");
            return;
        }

        int[][] grayscaleData = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int pixelValue = pixelData[row * width + col];
                int red = (pixelValue >> 16) & 0xff;
                int green = (pixelValue >> 8) & 0xff;
                int blue = pixelValue & 0xff;
                int grayscaleValue = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                grayscaleData[row][col] = 255 - grayscaleValue; // Inverse car 0 est noir et 255 est blanc
            }
        }



        // Imprimez les données en niveaux de gris sur la console
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                System.out.print(grayscaleData[row][col] + " ");
            }
            System.out.println();
        }
    }

    public void mousePressed(MouseEvent e) {
        prevX = e.getX();
        prevY = e.getY();
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        graphics.setStroke(new BasicStroke(paintBrushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(prevX, prevY, x, y);

        repaint();
        prevX = x;
        prevY = y;
    }


    // Unused methods

    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

    public static void main(String[] args) {
        DrawingBoard board = new DrawingBoard();
    }

}

 */

