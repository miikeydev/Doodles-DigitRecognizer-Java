package DrawingApp;

import NeuralNetwork.NeuralNetworkBoosted;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.awt.*;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import javax.swing.JTextArea;

public class PredictionHandler {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private JTextArea predictionDisplay = new JTextArea(20, 20);

    public void predict(Image image, int width, int height, PredictionPanel predictionPanel, ChoicePanel choicePanel) throws IOException {
        int[] pixelData = new int[width * height];
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, width, height, pixelData, 0, width);
        try {
            pixelGrabber.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("Interrupted waiting for pixels!");
            return;
        }

        int[][] colorData = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // Convert 2D coordinates (row, col) to 1D index in array representing the image
                int pixelValue = pixelData[row * width + col];
                int red = (pixelValue >> 16) & 0xff;
                int green = (pixelValue >> 8) & 0xff;
                int blue = pixelValue & 0xff;

                if (red < 128 && green < 128 && blue < 128) {
                    colorData[row][col] = 255;
                } else {
                    colorData[row][col] = 0;
                }
            }
        }

        int[][] normalizedData = normalisation(colorData);

        int totalSize = normalizedData.length * normalizedData[0].length;
        int[] oneDArray = new int[totalSize];
        int index = 0;
        for (int i = 0; i < normalizedData.length; i++) {
            for (int j = 0; j < normalizedData[0].length; j++) {
                oneDArray[index++] = normalizedData[i][j];
            }
        }

        INDArray input = Nd4j.createFromArray(oneDArray).reshape(1, 784);
        input.divi(255.0);

        NeuralNetworkBoosted boostedNetwork = new NeuralNetworkBoosted(784, 10, 0.001);
        boostedNetwork.model = NeuralNetworkBoosted.loadModel("savedmodel/doodlesBias.model");
        INDArray predictions = boostedNetwork.labelAndPercentage(input); // predictions is an 2D IND array with label and predictions
        double[] predictionsPercentages = extractPercentagesFromPredictions(predictions);
        predictionPanel.setPredictions(predictionsPercentages);
        System.out.println(predictionsPercentages[0]);
        ChoicePanel.updateInstructionPanel(predictionsPercentages);

    }


    public int[][] normalisation(int[][] BWData) {

        Mat mat = new Mat(BWData.length, BWData[0].length, CvType.CV_8U);
        for (int row = 0; row < BWData.length; row++) {
            for (int col = 0; col < BWData[0].length; col++) {
                mat.put(row, col, BWData[row][col]);
            }
        }

        Mat resizedMat = new Mat();
        Imgproc.resize(mat, resizedMat, new Size(28, 28), 0, 0, Imgproc.INTER_AREA);

        Mat binaryMat = new Mat();
        Imgproc.threshold(resizedMat, binaryMat, 128, 255, Imgproc.THRESH_BINARY);

        int[][] resizedData = new int[28][28];
        for (int row = 0; row < 28; row++) {
            for (int col = 0; col < 28; col++) {
                resizedData[row][col] = (int) binaryMat.get(row, col)[0];
            }
        }

        return resizedData;
    }

    private double[] extractPercentagesFromPredictions(INDArray predictions) {

        double[] percentages = new double[10];
        for (int i = 0; i < 10; i++) {
            percentages[i] = predictions.getDouble(i, 1); // Assuming the second column contains the prediction percentages
        }

        return percentages;
    }


}
