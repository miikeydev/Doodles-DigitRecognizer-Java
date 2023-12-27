package DrawingApp;

import NeuralNetwork.LabelList;

import javax.swing.*;
import java.awt.*;


public class PredictionPanel extends JPanel {

    private static final int NUMBER_OF_PREDICTIONS = 10; // Assuming 10 classes for the prediction
    private java.util.List<JLabel> predictionLabels;
    String[] LABEL_NAMES = LabelList.LabelForDoodles;

    public PredictionPanel() {
        setLayout(new GridLayout(NUMBER_OF_PREDICTIONS, 1, 5, 10)); // Arrange labels in a column with vertical spacing of 10 pixels
        predictionLabels = new java.util.ArrayList<>(NUMBER_OF_PREDICTIONS);

        for (int i = 0; i < NUMBER_OF_PREDICTIONS; i++) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setPreferredSize(new Dimension(200, 50)); // Adjust the width and height as needed
            label.setFont(new Font("Arial", Font.PLAIN, 16));
            label.setForeground(Color.WHITE);

            predictionLabels.add(label);
            add(label);
        }

        // Add vertical spacing at the top and bottom of the panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adjust the values as needed
    }

    public void setPredictions(double[] predictions) {
        // Find the index of the highest prediction
        int maxIndex = 0;
        double maxPrediction = predictions[0];

        for (int i = 1; i < predictions.length; i++) {
            if (predictions[i] > maxPrediction) {
                maxPrediction = predictions[i];
                maxIndex = i;
            }
        }

        for (int i = 0; i < predictions.length; i++) {
            double prediction = predictions[i];
            JLabel label = predictionLabels.get(i);
            label.setText(String.format("%s: %.2f%%", LABEL_NAMES[i], prediction));
            // Set the color based on whether it's the highest prediction or not
            if (i == maxIndex) {
                label.setBackground(new Color(0x147A03)); // Green for the highest
            } else {
                label.setBackground(new Color(0x3F3C3C)); // Dark color for the rest
            }
        }
    }
}
