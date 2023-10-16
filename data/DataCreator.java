package data;

import java.util.Random;
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
        INDArray rotatedImage = rotate(translatedImage);

        // Reshape the final transformed image back to 1x784 before returning
        return rotatedImage.reshape(1, 784);
    }


    private static INDArray zoom(INDArray image) {
        Random rand = new Random();

        // Randomly select crop values between 0 and 10 for zooming in or out
        int cropX = rand.nextInt(5); // this will give values between 0 (inclusive) and 11 (exclusive)
        int cropY = cropX;  // Assuming square images, but you can generate a separate random value for Y if needed

        // Adjust crop margins based on random values
        INDArray cropped = image.get(NDArrayIndex.interval(cropX, 28 - cropX), NDArrayIndex.interval(cropY, 28 - cropY));

        // Calculate new dimensions
        int newWidth = 28 - 2 * cropX;
        int newHeight = 28 - 2 * cropY;

        // Simple interpolation to resize back to 28x28
        INDArray zoomed = Nd4j.zeros(28, 28);
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                int srcX = i * newWidth / 28;
                int srcY = j * newHeight / 28;
                zoomed.putScalar(i, j, cropped.getDouble(srcX, srcY));
            }
        }

        return zoomed;
    }

    private static INDArray translate(INDArray image) {
        Random random = new Random();

        // Generate a random translation between 0 and 7 for x and y directions
        int translationX = random.nextInt(8);
        int translationY = random.nextInt(8);

        // Randomly decide if the translation should be positive or negative
        translationX = random.nextBoolean() ? translationX : -translationX;
        translationY = random.nextBoolean() ? translationY : -translationY;

        // Adjust the translation to avoid out-of-bounds issues
        int startX = Math.max(0, translationX);
        int endX = 28 + Math.min(0, translationX);
        int startY = Math.max(0, translationY);
        int endY = 28 + Math.min(0, translationY);

        INDArray translated = Nd4j.zeros(28, 28);
        translated.put(new INDArrayIndex[]{NDArrayIndex.interval(startX, endX), NDArrayIndex.interval(startY, endY)},
                image.get(NDArrayIndex.interval(-Math.min(0, translationX), 28 - Math.max(0, translationX)),
                        NDArrayIndex.interval(-Math.min(0, translationY), 28 - Math.max(0, translationY))));
        return translated;
    }

    private static INDArray rotate(INDArray image) {
        Random rand = new Random();

        // Generate a random angle between 0 and 45 degrees
        double angle = rand.nextDouble() * 45;

        // Randomly choose the rotation direction (trigonometric or anti-trigonometric)
        if (rand.nextBoolean()) {
            angle = -angle; // This makes the rotation go clockwise
        }

        double radians = Math.toRadians(angle);
        double cosTheta = Math.cos(radians);
        double sinTheta = Math.sin(radians);
        INDArray rotated = Nd4j.zeros(28, 28);
        int centerX = 14;
        int centerY = 14;

        for (int x = 0; x < 28; x++) {
            for (int j = 0; j < 28; j++) {
                int newX = (int) ((x - centerX) * cosTheta - (j - centerY) * sinTheta + centerX);
                int newY = (int) ((x - centerX) * sinTheta + (j - centerY) * cosTheta + centerY);
                if (newX >= 0 && newX < 28 && newY >= 0 && newY < 28) {
                    rotated.putScalar(x, j, image.getDouble(newX, newY));
                }
            }
        }

        return rotated;
    }
}