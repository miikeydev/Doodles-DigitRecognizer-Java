package DrawingApp;

import NeuralNetwork.NeuralNetworkBoosted;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;

public class ImageProcessor {

    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) throws IOException {
        // Step 2: Read the image using OpenCV
        Mat image = Imgcodecs.imread("savedImages/Image1.png", Imgcodecs.IMREAD_GRAYSCALE);

        // Ensure the image is 28x28 pixels
        if (image.rows() != 28 || image.cols() != 28) {
            System.err.println("The image is not 28x28 pixels.");
            return;
        }

        // Step 3: Convert the Image to ND4J INDArray
        INDArray indArray = Nd4j.create(image.rows(), image.cols());
        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                double[] pixel = image.get(i, j);
                indArray.putScalar(new int[]{i, j}, pixel[0]);
            }
        }

        // Normalize the INDArray by dividing each value by 255
        indArray.divi(255);

        // Reshape the INDArray to have a shape of 1x784
        INDArray reshapedArray = indArray.reshape(1, 784);

        // Print the shape of the reshaped INDArray
        long[] reshapedShape = reshapedArray.shape();
        System.out.println("Reshaped Shape: " + java.util.Arrays.toString(reshapedShape));

        NeuralNetworkBoosted boostedNetwork = new NeuralNetworkBoosted(784, 10, 0.7);
        boostedNetwork.model = NeuralNetworkBoosted.loadModel("savedmodel/savedmodel1.model");
        String predictions = boostedNetwork.predictWithLabels(reshapedArray);
        System.out.println(predictions);
    }
}
