import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.swing.*;
import java.awt.*;




public class DrawingFrame extends JFrame {
    DrawingPanel drawingPanel;
    static INDArray data_created;

    public DrawingFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
            data_created = getDrawingData(false);
            System.out.println("The Data: " + data_created);

            long[] shape = data_created.shape();

            if (shape.length == 2){
                NeuralNetwork nn = new NeuralNetwork();
                nn.testPredict(data_created); // Call test method directly here
            } else {
                data_created = data_created.reshape(1, 784);
                NeuralNetworkBoosted boostedNetwork = new NeuralNetworkBoosted(784, 10, 0.7);
                INDArray predictions = boostedNetwork.predict(data_created);
                System.out.println("Predictions: " + predictions);
            }
        });
        buttonPanel.add(retrieveDataButton);

        pack();
    }

    public INDArray getDrawingData(boolean is2D) {
        int[][] pixels = drawingPanel.getPixels();
        float[] normalizedPixels = new float[784];
        int idx = 0;

        for (int y = 0; y < 28; y++) {
            for (int x = 0; x < 28; x++) {
                normalizedPixels[idx++] = pixels[y][x] / 255.0f;
            }
        }

        INDArray array;

        if (is2D) {
            // Creating a 2D INDArray of shape [1, 784] by reshaping
            array = Nd4j.createFromArray(normalizedPixels).reshape(1, 784);
            array = array.transpose();
        } else {
            // Keeping it as 1D with shape 784
            array = Nd4j.createFromArray(normalizedPixels);
        }

        // Displaying the shape of the array
        long[] shape = array.shape();
        System.out.println("Shape of INDArray: [" + shape[0] + (shape.length > 1 ? ", " + shape[1] : "") + "]");

        return array;
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
    }
}
