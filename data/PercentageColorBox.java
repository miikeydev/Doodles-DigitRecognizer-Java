package data;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.swing.*;
import java.awt.*;

public class PercentageColorBox extends JPanel {

    private INDArray percentages;

    public PercentageColorBox(INDArray percentages) {
        this.percentages = percentages;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        initializeBoxes();
    }

    private void initializeBoxes() {
        double min = percentages.minNumber().doubleValue();
        double max = percentages.maxNumber().doubleValue();

        for (int i = 0; i < percentages.rows(); i++) {
            double percentage = percentages.getDouble(i, 1);
            JPanel box = new JPanel();
            box.setBackground(getColorForPercentage(percentage, min, max));
            box.setPreferredSize(new Dimension(50, 50)); // Set your desired size
            box.add(new JLabel(String.format("%d: %.2f%%", (int)percentages.getDouble(i, 0), percentage)));
            this.add(box);
        }
    }

    private Color getColorForPercentage(double percentage, double min, double max) {
        float ratio = (float) (percentage - min) / (float) (max - min);
        int red = (int) ((1 - ratio) * 255);
        int green = (int) (ratio * 255);
        return new Color(red, green, 0);
    }

    public static void createAndShowGui(INDArray percentages) {
        JFrame frame = new JFrame("Percentage Colors");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new PercentageColorBox(percentages));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Assume that 'yourPercentages' is an INDArray containing the data
        // It should be retrieved or computed before calling the GUI method
        INDArray yourPercentages = Nd4j.create(new double[][]{
                {0, 75.0}, // Label 0, 75%
                {1, 50.0}, // Label 1, 50%
                {2, 25.0},  // Label 2, 25%
                {3, 24.0},  // Label 2, 25%
                {4, 21.0}, // Label 2, 25%
                {5, 18.0},  // Label 2, 25%
        });

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui(yourPercentages);
            }
        });
    }
}

