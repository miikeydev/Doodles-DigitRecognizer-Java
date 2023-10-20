import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;


import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class NeuralNetwork {

    private INDArray W1, b1, W2, b2;
    private double learningRate = 0.01;
    private int numInputs = 784;
    private int numHidden = 100;
    private int numOutputs = 10;

    public NeuralNetwork() {
        initParams();
    }

    private void initParams() {
        W1 = Nd4j.rand(numHidden, numInputs).subi(0.5);
        b1 = Nd4j.rand(numHidden, 1).subi(0.5);
        W2 = Nd4j.rand(numOutputs, numHidden).subi(0.5);
        b2 = Nd4j.rand(numOutputs, 1).subi(0.5);

    }

    private INDArray ReLU(INDArray Z) {

        return Transforms.max(Z, 0);
    }

    private INDArray softmax(INDArray Z) {

        return Transforms.exp(Z).div(Transforms.exp(Z).sum(0));
    }

    private INDArray ReLU_deriv(INDArray Z) {
        return Z.gt(0);  // Element-wise check if Z > 0
    }

    private INDArray oneHot(INDArray Y) {
        // Assuming Y is a column vector with integer values
        INDArray oneHot = Nd4j.zeros(Y.length(), numOutputs);
        for (int i = 0; i < Y.length(); i++) {
            oneHot.putScalar(new int[]{i, Y.getInt(i)}, 1);
        }

        return oneHot.transpose();
    }


    public INDArray[] forwardProp(INDArray X) {

        INDArray Z1 = W1.mmul(X).add(b1);
        INDArray A1 = ReLU(Z1);
        INDArray Z2 = W2.mmul(A1).add(b2);
        INDArray A2 = softmax(Z2);

        return new INDArray[]{Z1, A1, Z2, A2};
    }

    public INDArray[] backwardProp(INDArray X, INDArray Y, INDArray[] forwardPropResults) {
        INDArray Z1 = forwardPropResults[0];
        INDArray A1 = forwardPropResults[1];
        INDArray A2 = forwardPropResults[3];

        INDArray oneHotY = oneHot(Y);
        int m = (int) X.size(0);


        INDArray dZ2 = A2.sub(oneHotY);
        INDArray dW2 = dZ2.mmul(A1.transpose()).div(m);
        INDArray db2 = dZ2.sum(1).reshape(new int[]{(int) dZ2.size(0), 1}).div(m);
        INDArray dZ1 = W2.transpose().mmul(dZ2).mul(ReLU_deriv(Z1));
        INDArray dW1 = dZ1.mmul(X.transpose()).div(m);
        INDArray db1 = dZ1.sum(1).reshape(new int[]{(int) dZ1.size(0), 1}).div(m);

        return new INDArray[]{dW1, db1, dW2, db2};
    }



    public void updateParams(INDArray dW1, INDArray db1, INDArray dW2, INDArray db2) {

        W1.subi(dW1.mul(learningRate));
        b1.subi(db1.mul(learningRate));
        W2.subi(dW2.mul(learningRate));
        b2.subi(db2.mul(learningRate));
    }

    public INDArray addNoise(INDArray X, double noiseLevel, double noiseProbability) {
        // X: the original data
        // noiseLevel: the standard deviation of the noise
        // noiseProbability: the probability of a pixel being noisy

        // Creating a mask of values that are either 0 or 1,
        // where 1 indicates that noise should be added to that position
        INDArray mask = Nd4j.rand(X.shape()).lt(noiseProbability);

        // Creating a noise matrix
        INDArray noise = Nd4j.randn(X.shape()).muli(noiseLevel);

        // Applying the mask to the noise matrix: element-wise multiplication
        INDArray maskedNoise = noise.mul(mask);

        // Adding the masked noise to the original data
        INDArray XNoisy = X.add(maskedNoise);

        /* Printing a few examples of the masked noise data
        int numExamplesToPrint = 5;
        System.out.println("A few examples of the masked noise data:");
        for (int i = 0; i < numExamplesToPrint; i++) {
            System.out.println("Example " + i + ": " + maskedNoise.getColumn(i));
        }

        // Printing the maximum value of the masked noise data
        double maxVal = maskedNoise.maxNumber().doubleValue();
        System.out.println("Maximum value in the masked noise data: " + maxVal);

         */

        return XNoisy;
    }


    public void gradientDescent(INDArray X, INDArray Y, double alpha, int iterations) {
        initParams();
        for (int i = 0; i < iterations; i++) {
            //INDArray XNoisy = addNoise(X, 0.05, 0.1);
            INDArray[] forwardPropResults = forwardProp(X);
            INDArray A2 = forwardPropResults[3];
            INDArray[] gradients = backwardProp(X, Y, forwardPropResults);
            updateParams(gradients[0], gradients[1], gradients[2], gradients[3]);

            if (i % 20 == 0) {
                System.out.println("Iteration: " + i);
                int[] predictions = getPredictions(A2);
                System.out.println("Accuracy: " + getAccuracy(predictions, Y) + " %");
            }
        }
    }



    public int[] getPredictions(INDArray A2) {
        // Finding the index of the maximum value in each column
        return Nd4j.argMax(A2, 0).toIntVector();
    }

    public double getAccuracy(int[] predictions, INDArray Y) {
        // Comparing predictions with actual values and calculating accuracy
        int correct = 0;
        for (int i = 0; i < predictions.length; i++) {
            if (predictions[i] == Y.getInt(i)) {
                correct++;
            }
        }
        return 100.0 * correct / predictions.length;
    }


    public void displayPredictions(INDArray X, INDArray Y, int numExamples) {
        INDArray[] forwardPropResults = forwardProp(X);
        INDArray A2 = forwardPropResults[3]; // Extracting A2 from the results
        int[] predictions = getPredictions(A2);

        System.out.println("Displaying " + numExamples + " predictions:");
        for (int i = 0; i < numExamples; i++) {
            int predictedLabel = predictions[i];
            int actualLabel = Y.getInt(i);
            System.out.println("Example " + i + ": Predicted = " + predictedLabel + ", Actual = " + actualLabel);
        }
    }

    public void testPredict(INDArray X){

        INDArray[] forwardPropResults = forwardProp(X);
        INDArray A2 = forwardPropResults[3];
        int[] predictions = getPredictions(A2);
        System.out.println("You have drawn the number : " + predictions[0]);
    }


    public BufferedImage toBufferedImage(INDArray indArray) {
        int width = (int) Math.sqrt(indArray.length());
        int height = width;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayValue = (int) (indArray.getDouble(y * width + x) * 255);  // Scale if necessary
                int rgb = (grayValue << 16) | (grayValue << 8) | grayValue;
                image.setRGB(x, y, rgb);
            }
        }

        return image;
    }

    public void displayImage(BufferedImage img) {
        // Scale the image
        int scaledWidth = img.getWidth() * 5;  // Scale width
        int scaledHeight = img.getHeight() * 5; // Scale height
        Image scaledImage = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);

        ImageIcon icon = new ImageIcon(scaledImage);
        JFrame frame = new JFrame();
        JLabel label = new JLabel(icon);
        frame.setLayout(new FlowLayout());
        frame.setSize(scaledWidth, scaledHeight); // Adjust the frame size to fit the scaled image
        frame.add(label);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void testPrediction(INDArray XT, INDArray Y, int index) {
        // Extracting a column vector representing an image
        INDArray singleSample = XT.getColumn(index).reshape(1, XT.rows());
        INDArray singleSampleT = singleSample.transpose();


        // Making predictions
        INDArray[] forwardPropResults = forwardProp(singleSampleT);
        INDArray A2 = forwardPropResults[3];
        int[] predictions = getPredictions(A2);

        // Extracting the actual label
        int actualLabel = Y.getInt(index);

        // Displaying results
        System.out.println("Prediction: " + predictions[0]);
        System.out.println("Label: " + actualLabel);

        // Reshape INDArray and convert to BufferedImage for display
        BufferedImage img = toBufferedImage(singleSample);
        displayImage(img);
    }








}


