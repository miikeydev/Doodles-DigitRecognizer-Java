import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.swing.*;
import java.awt.*;

public class DrawingFrame extends JFrame {
    DrawingPanel drawingPanel;
    static INDArray data_created;

    public DrawingFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(new BorderLayout());

        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> drawingPanel.clear());
        buttonPanel.add(clearButton);

        JButton retrieveDataButton = new JButton("Retrieve Data");
        retrieveDataButton.addActionListener(e -> {
            data_created = getDrawingData();
            System.out.println("The Data: " + data_created);

            NeuralNetwork nn = new NeuralNetwork();
            nn.testPredict(data_created); // Call test method directly here
        });
        buttonPanel.add(retrieveDataButton);

        pack();

        pack();
    }

    public INDArray getDrawingData() {
        int[][] pixels = drawingPanel.getPixels();
        float[] normalizedPixels = new float[784];
        int idx = 0;

        for (int y = 0; y < 28; y++) {
            for (int x = 0; x < 28; x++) {
                normalizedPixels[idx++] = pixels[y][x] / 255.0f;
            }
        }

        // Creating a 2D INDArray of shape [784, 1] by reshaping
        INDArray array2D = Nd4j.createFromArray(normalizedPixels).reshape(784, 1);

        // Displaying the shape of the array
        long[] shape = array2D.shape();
        System.out.println("Shape of INDArray: [" + shape[0] + ", " + shape[1] + "]");

        return array2D;
    }






    public static void main(String[] args) {
        DrawingFrame frame = new DrawingFrame();

        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
        });

        // Example: Using data_created
        while (data_created == null) {
            try {
                Thread.sleep(100); // Wait for 100ms before checking again
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Data in main method. INDArray length: " + data_created.length());
        System.out.println("The Data: " + data_created);
    }
}
