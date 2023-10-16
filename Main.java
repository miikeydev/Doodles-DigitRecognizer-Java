import data.DataReader;
import data.Image_;
import data.DataConverter;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import org.nd4j.linalg.api.ndarray.INDArray;

import javax.swing.*;


public class Main {

    public static void main(String[] args) {
        List<Image_> images = new DataReader().readData("data/train.csv");
        Collections.shuffle(images);

        List<Image_> devData = new ArrayList<>(images.subList(0, 1000));
        List<Image_> trainData = new ArrayList<>(images.subList(1000, images.size()));



        // Normalize the pixel values in both datasets
        devData.forEach(Image_::normalize);
        trainData.forEach(Image_::normalize);

        INDArray[] data = DataConverter.convertToINDArrays(trainData);
        INDArray[] dataTestPerf = DataConverter.convertToINDArrays(devData);

        INDArray X = data[0]; // Image data
        INDArray XT = X.transpose();
        INDArray Y = data[1]; // Labels

        INDArray XDev = dataTestPerf[0]; // Image data
        INDArray XDevT = XDev.transpose();
        INDArray YDev = dataTestPerf[1];



        NeuralNetwork nn = new NeuralNetwork();

        // Set the hyperparameters
        double learningRate = 0.1;
        int numIterations = 300;

        // Train the neural network
        nn.gradientDescent(XT, Y, learningRate, numIterations);

        nn.testPrediction(XDevT, YDev, 5);
        nn.testPrediction(XDevT, YDev, 6);
        nn.testPrediction(XDevT, YDev, 7);
        nn.testPrediction(XDevT, YDev, 8);
        nn.testPrediction(XDevT, YDev, 9);


        DrawingFrame frame = new DrawingFrame();

        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
        });

        while (DrawingFrame.data_created == null) {
            try {
                Thread.sleep(100); // Wait for 100ms before checking again
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }





    }
}



