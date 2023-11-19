package DrawingApp;

import javax.swing.*;
import java.awt.*;

public class PredictionPanel extends JPanel {

    private static final int NUMBER_OF_PREDICTIONS = 10; // Assuming 10 classes for the prediction
    private java.util.List<JLabel> predictionLabels;
    private static final String[] LABEL_NAMES = {
            "Angel", "Apple", "Axe", "Book", "Helicopter","Moon", "Mushroom", "Octopus", "Pants", "Pencil"
            //"Zero","One", "Two", "Three", "Four", "Five", "Six", "Seven", "Height", "Nine"
    };

    public PredictionPanel() {
        // Adjust the GridLayout to have more space horizontally for each label
        setLayout(new GridLayout(NUMBER_OF_PREDICTIONS, 1, 5, 5)); // Arrange labels in a column
        predictionLabels = new java.util.ArrayList<>(NUMBER_OF_PREDICTIONS);

        for (int i = 0; i < NUMBER_OF_PREDICTIONS; i++) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            // Set a larger preferred size for the labels
            label.setPreferredSize(new Dimension(200, 50)); // Adjust the width and height as needed
            // Set a larger font size for the labels
            label.setFont(new Font("Serif", Font.BOLD, 20));
            predictionLabels.add(label);
            add(label);
        }
    }

    public void setPredictions(double[] predictions) {
        for (int i = 0; i < predictions.length; i++) {
            double prediction = predictions[i];
            JLabel label = predictionLabels.get(i);
            label.setText(String.format("%s: %.2f%%", LABEL_NAMES[i], prediction));
            label.setBackground(getColorForPercentage(prediction));
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    }

    private Color getColorForPercentage(double percentage) {
        // Normalize the percentage to a range between 0 and 1
        percentage = Math.min(1, Math.max(0, percentage / 100.0));

        // Map the percentage to a hue value. 0 is red, 0.15 is yellow, and 0.3 is green
        float hue = (float) (0.3 * percentage);
        float saturation = 1f;
        float value = 1f;

        // Convert HSV to RGB
        return Color.getHSBColor(hue, saturation, value);
    }

}
