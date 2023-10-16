package data;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.util.Arrays;


public class DataCreator {

    public static INDArray augmentData(INDArray originalImageData) {
        INDArray augmentedImages = Nd4j.zeros(originalImageData.shape());

        for (int i = 0; i < originalImageData.rows(); i++) {
            INDArray originalImage = originalImageData.getRow(i);
            INDArray augmentedImage = applyTransformations(originalImage);
            augmentedImages.putRow(i, augmentedImage);
        }

        return augmentedImages;
    }

    private static INDArray applyTransformations(INDArray originalImage) {
        originalImage = originalImage.reshape(28, 28); // Ensure the image is 28x28

        // Sequentially apply transformations
        INDArray zoomedImage = zoom(originalImage);
        INDArray translatedImage = translate(zoomedImage);
        INDArray rotatedImage = rotate(translatedImage, 25); // Here, I'm assuming an angle of 10 degrees for rotation. Adjust as needed.

        // Reshape the final transformed image back to 1x784 before returning
        return rotatedImage.reshape(1, 784);
    }


    private static INDArray zoom(INDArray image) {
        // Crop to 24x24
        INDArray cropped = image.get(NDArrayIndex.interval(2, 26), NDArrayIndex.interval(2, 26));
        // Simple interpolation to resize back to 28x28
        INDArray zoomed = Nd4j.zeros(28, 28);
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                int srcX = i * 24 / 28;
                int srcY = j * 24 / 28;
                zoomed.putScalar(i, j, cropped.getDouble(srcX, srcY));
            }
        }
        return zoomed;
    }

    private static INDArray translate(INDArray image) {
        // Example: Move the image 2 pixels to the right

        long[] shape = image.shape();
        System.out.println("Shape of image: " + Arrays.toString(shape));

        INDArray translated = Nd4j.zeros(28, 28);

        translated.put(new INDArrayIndex[]{NDArrayIndex.all(), NDArrayIndex.interval(2, 28)}, image.get(NDArrayIndex.all(), NDArrayIndex.interval(0, 26)));
        return translated;
    }

    private static INDArray rotate(INDArray image, double angle) {
        double radians = Math.toRadians(angle);
        double cosTheta = Math.cos(radians);
        double sinTheta = Math.sin(radians);
        INDArray rotated = Nd4j.zeros(28, 28);
        int centerX = 14;
        int centerY = 14;

        for (int x = 0; x < 28; x++) {
            for (int y = 0; y < 28; y++) {
                int newX = (int) ((x - centerX) * cosTheta - (y - centerY) * sinTheta + centerX);
                int newY = (int) ((x - centerX) * sinTheta + (y - centerY) * cosTheta + centerY);
                if (newX >= 0 && newX < 28 && newY >= 0 && newY < 28) {
                    rotated.putScalar(x, y, image.getDouble(newX, newY));
                }
            }
        }

        return rotated;
    }
}