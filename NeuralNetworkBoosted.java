import data.DataConverter;
import data.DataCreator;
import data.DataReader;
import data.Image_;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.learning.config.Sgd;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NeuralNetworkBoosted {
    private MultiLayerNetwork model;

    public NeuralNetworkBoosted(int numInputs, int numOutputs, double learningRate) {
        // Define your neural network architecture here.
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Sgd(learningRate))
                .seed(123)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(numInputs)
                        .nOut(400) // Example hidden layer size
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(400)
                        .nOut(numOutputs)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .build();

        model = new MultiLayerNetwork(conf);
        model.init();

        // Listener to print the score every iteration
        model.setListeners(new ScoreIterationListener(1));


    }


    public void train(INDArray input, INDArray labels, int epochs, double learningRate, int displayInterval) {
        DataSet dataSet = new DataSet(input, labels);

        for (int epoch = 0; epoch < epochs; epoch++) {
            model.fit(dataSet);

            if (epoch % displayInterval == 0 || epoch == epochs - 1) {
                double accuracy = evaluateAccuracy(input, labels);
                System.out.println("Epoch " + epoch + " Accuracy: " + accuracy + "%");
            }
        }
    }


    public double evaluateAccuracy(INDArray input, INDArray labels) {
        INDArray predictedOutput = model.output(input);

        INDArray actualsMax = Nd4j.argMax(labels, 1);
        INDArray predictionsMax = Nd4j.argMax(predictedOutput, 1);

        INDArray comparison = actualsMax.eq(predictionsMax).castTo(DataType.INT);
        int nCorrect = comparison.sumNumber().intValue();

        return 100.0 * nCorrect / input.rows();
    }




    public static INDArray oneHotEncode(INDArray labels, int numClasses) {
        INDArray encoded = Nd4j.zeros(labels.rows(), numClasses);

        for (int i = 0; i < labels.rows(); i++) {
            int idx = labels.getInt(i, 0);
            encoded.putScalar(new int[]{i, idx}, 1.0);
        }

        return encoded;
    }


    public INDArray predict(INDArray input) {
        return model.output(input);
    }




    public static void main(String[] args) {

        List<Image_> images = new DataReader().readData("data/train.csv");
        Collections.shuffle(images);

        List<Image_> trainData = new ArrayList<>(images.subList(1000, images.size()));


        trainData.forEach(Image_::normalize);

        INDArray[] data = DataConverter.convertToINDArrays(trainData);


        INDArray X = data[0];
        INDArray augmentedX = DataCreator.augmentData(X);


        INDArray XT = X.transpose();
        INDArray augmentedXT = augmentedX.transpose();



        INDArray Y = data[1];
        Y = oneHotEncode(Y, 10);

        System.out.println("Shape of XT: " + java.util.Arrays.toString(XT.shape()));
        System.out.println("Shape of Y: " + java.util.Arrays.toString(Y.shape()));

        int epochs = 200;
        int numInputs = 784;
        int numOutputs = 10;
        double learningRate = 0.65;
        int displayInterval = 20;


        NeuralNetworkBoosted neuralNetwork = new NeuralNetworkBoosted(numInputs, numOutputs, learningRate);

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