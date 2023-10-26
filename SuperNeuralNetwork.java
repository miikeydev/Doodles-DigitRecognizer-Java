import data.Image_;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.nd4j.linalg.learning.config.Adam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;

import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import data.DataReader;
import data.DataConverter;
import data.DataCreator;

import javax.swing.*;


public class SuperNeuralNetwork {
    private static MultiLayerNetwork network;

    public SuperNeuralNetwork(int numInputs, int numOutputs, double learningRate) {
        int seed = 123;
        int numHidden = 100; // You can adjust this based on your needs

        // Create a configuration for the neural network
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .updater(new Adam(learningRate))
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(numInputs)
                        .nOut(numHidden)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(numHidden)
                        .nOut(numOutputs)
                        .activation(Activation.SOFTMAX)
                        .build())
                .build();

        network = new MultiLayerNetwork(conf);
        network.init();

        // Listener to print the score every iteration
        network.setListeners(new ScoreIterationListener(1));
    }


    public void train(INDArray input, INDArray labels, int epochs, double learningRate, int displayInterval) {
        DataSet dataSet = new DataSet(input, labels);

        for (int epoch = 0; epoch < epochs; epoch++) {
            network.fit(dataSet);

            if (epoch % displayInterval == 0 || epoch == epochs - 1) {
                double accuracy = evaluateAccuracy(input, labels);
                System.out.println("Epoch " + epoch + " Accuracy: " + accuracy + "%");
            }
    }}

    public double evaluateAccuracy(INDArray X, INDArray Y) {
        int totalExamples = X.rows();
        int correctPredictions = 0;

        for (int i = 0; i < totalExamples; i++) {
            INDArray input = X.getRow(i, true);
            INDArray label = Y.getRow(i, true);

            INDArray predicted = network.output(input, false);

            int actualLabel = label.argMax(1).getInt(0);
            int predictedLabel = predicted.argMax(1).getInt(0);

            if (actualLabel == predictedLabel) {
                correctPredictions++;
            }
        }

        double accuracy = (double) correctPredictions / totalExamples;
        return accuracy;
    }



    public static INDArray oneHotEncoding(INDArray Y, int numClasses) {
        int totalExamples = Y.rows();
        INDArray oneHot = Nd4j.zeros(totalExamples, numClasses);

        for (int i = 0; i < totalExamples; i++) {
            int label = Y.getInt(i);

            // Set the corresponding element to 1
            oneHot.putScalar(new int[]{i, label}, 1.0);
        }

        return oneHot;
    }

    public INDArray predict(INDArray input) {
        return network.output(input);
    }

    // Create a DataSetIterator from your data

    public static void main(String[] args) {

        List<Image_> images = new DataReader().readData("src/resources/train.csv");
        Collections.shuffle(images);

        List<Image_> trainData = new ArrayList<>(images.subList(1000, images.size()));


        trainData.forEach(Image_::normalize);

        INDArray[] data = DataConverter.convertToINDArrays(trainData);


        INDArray X = data[0];
        INDArray augmentedX = DataCreator.augmentData(X);


        INDArray XT = X.transpose();
        INDArray augmentedXT = augmentedX.transpose();



        INDArray Y = data[1];
        Y = oneHotEncoding(Y, 10);

        System.out.println("Shape of XT: " + java.util.Arrays.toString(XT.shape()));
        System.out.println("Shape of Y: " + java.util.Arrays.toString(Y.shape()));

        int epochs = 200;
        int numInputs = 784;
        int numOutputs = 10;
        double learningRate = 0.65;
        int displayInterval = 20;


        SuperNeuralNetwork neuralNetwork = new SuperNeuralNetwork(numInputs, numOutputs, learningRate);

        neuralNetwork.train(augmentedX, Y, epochs, learningRate, displayInterval);

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


