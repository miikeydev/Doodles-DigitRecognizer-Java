package NeuralNetwork;

import data.DataConverter;
import data.DataCreator;
import data.DataReader;
import data.Image_;
import java.io.File;
import org.nd4j.linalg.learning.config.Adam;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NeuralNetworkBoosted {
    public MultiLayerNetwork model;

    public NeuralNetworkBoosted(int numInputs, int numOutputs, double learningRate) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam(learningRate))
                .seed(123)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(numInputs)
                        .nOut(400)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(1, new DenseLayer.Builder()
                        .nIn(400)
                        .nOut(300)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(2, new DenseLayer.Builder()
                        .nIn(300)
                        .nOut(200)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(3, new DenseLayer.Builder()
                        .nIn(200)
                        .nOut(100)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(4, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(100)
                        .nOut(numOutputs)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .build();

        model = new MultiLayerNetwork(conf);
        model.init();
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


    public String predictWithLabels(INDArray input) {
        INDArray probabilities = model.output(input);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < probabilities.columns(); i++) {
            result.append(String.format("Label %d: %.2f%%\n\n", i, probabilities.getDouble(i) * 100));
        }

        return result.toString();
    }

    public INDArray labelAndPercentage(INDArray input) {
        INDArray probabilities = model.output(input);
        INDArray result = Nd4j.create(probabilities.columns(), 2);

        for (int i = 0; i < probabilities.columns(); i++) {
            result.putScalar(new int[]{i, 0}, i);
            result.putScalar(new int[]{i, 1}, probabilities.getDouble(i) * 100);
        }

        return result;
    }

    public void saveModel(String path) throws IOException {
        File modelSaveLocation = new File(path);
        model.save(modelSaveLocation);
    }

    public static MultiLayerNetwork loadModel(String path) throws IOException {
        File modelSaveLocation = new File(path);
        return MultiLayerNetwork.load(modelSaveLocation, true);
    }

    public static void main(String[] args) throws IOException {
        List<Image_> images = new DataReader().readData("data/doodles1.csv");
        Collections.shuffle(images);

        List<Image_> devData = new ArrayList<>(images.subList(0, 1000));
        List<Image_> trainData = new ArrayList<>(images.subList(1000, images.size()));

        devData.forEach(Image_::normalize);
        trainData.forEach(Image_::normalize);

        INDArray[] data = DataConverter.convertToINDArrays(trainData);
        INDArray[] dataTestPerf = DataConverter.convertToINDArrays(devData);

        INDArray XTestPerf = dataTestPerf[0];
        INDArray YTestPerf = dataTestPerf[1];

        INDArray X = data[0];
        INDArray augmentedX = DataCreator.augmentData(X);

        INDArray Y = data[1];
        Y = oneHotEncode(Y, 10);


        // HYPERPARAMETERS
        int epochs = 70;
        int numInputs = 784;
        int numOutputs = 10;
        double learningRate = 0.001;
        int displayInterval = 10;

        NeuralNetworkBoosted neuralNetwork = new NeuralNetworkBoosted(numInputs, numOutputs, learningRate);
        neuralNetwork.model = NeuralNetworkBoosted.loadModel("savedmodel/doodlesBias.model");
        neuralNetwork.model.setListeners(new ScoreIterationListener(1));

        neuralNetwork.train(augmentedX, Y, epochs, learningRate, displayInterval);

        neuralNetwork.saveModel("savedmodel/doodlesBias.model");

        for (int i = 0; i < 10; i++) {
            INDArray sample = XTestPerf.getRow(i).reshape(1, -1);
            String prediction = neuralNetwork.predictWithLabels(sample);

            int trueLabelIndex = YTestPerf.getInt(i);

            System.out.println("Sample " + (i+1) + ":");
            System.out.println("Predicted: ");
            System.out.println(prediction);
            System.out.println("True Label: Label " + trueLabelIndex);
            System.out.println("--------------");
        }
    }
}
