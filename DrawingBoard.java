import java.awt.*;
import java.awt.event.*;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import javax.swing.*;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

public class DrawingBoard extends JPanel implements MouseListener, MouseMotionListener {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private Image image;
    private Graphics2D graphics;
    private int prevX, prevY;
    private int paintBrushSize = 30;
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
        predictButton.addActionListener(e -> {
            try {
                predict();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
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

    public void predict() throws IOException {
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

        int[][] normalizedData = normalisation(grayscaleData);


        // Afficher l'image normalisée
        new ImageWindow(normalizedData);

        int totalSize = normalizedData.length * normalizedData[0].length;
        int[] oneDArray = new int[totalSize];
        int index = 0;
        for(int i = 0; i < normalizedData.length; i++) {
            for(int j = 0; j < normalizedData[0].length; j++) {
                oneDArray[index++] = normalizedData[i][j];
            }
        }

        // Convert the 1D array to an INDArray and reshape it to (1, 784)
        INDArray input = Nd4j.createFromArray(oneDArray).reshape(1, 784);
        input.divi(255.0);

        NeuralNetworkBoosted boostedNetwork = new NeuralNetworkBoosted(784, 10, 0.001);
        boostedNetwork.model = NeuralNetworkBoosted.loadModel("savedmodel/savedmodel1.model");
        String predictions = boostedNetwork.predictWithLabels(input);
        System.out.println(predictions);
    }

    public int[][] normalisation(int[][] grayscaleData) {
        // Convertissez les données en niveaux de gris en une Matrice OpenCV
        Mat mat = new Mat(grayscaleData.length, grayscaleData[0].length, CvType.CV_8U);
        for (int row = 0; row < grayscaleData.length; row++) {
            for (int col = 0; col < grayscaleData[0].length; col++) {
                mat.put(row, col, grayscaleData[row][col]);
            }
        }

        // Redimensionnez la matrice à 28x28 avec anti-aliasing
        Mat resizedMat = new Mat();
        Imgproc.resize(mat, resizedMat, new Size(28, 28), 0, 0, Imgproc.INTER_AREA);

        // Convertissez la matrice redimensionnée en un tableau 2D
        int[][] resizedData = new int[28][28];
        for (int row = 0; row < 28; row++) {
            for (int col = 0; col < 28; col++) {
                resizedData[row][col] = (int) resizedMat.get(row, col)[0];
            }
        }

        return resizedData;
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

class ImageWindow extends JFrame {
    private int[][] normalizedData;

    public ImageWindow(int[][] normalizedData) {
        this.normalizedData = normalizedData;
        setTitle("Normalized Image");
        setSize(280, 280);  // Chaque pixel sera affiché comme un carré de 10x10 pixels
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int row = 0; row < 28; row++) {
            for (int col = 0; col < 28; col++) {
                int value = normalizedData[row][col];
                g.setColor(new Color(value, value, value));
                g.fillRect(col * 10 + 10, row * 10 + 30, 10, 10);  // Ajustement pour les bordures de la fenêtre
            }
        }
    }
}
