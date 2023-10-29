package data;

import java.util.Random;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;


public class DataCreator {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public static INDArray augmentData(INDArray originalImageData) {
        INDArray augmentedImages = Nd4j.zeros(originalImageData.shape());

        for (int i = 0; i < originalImageData.rows(); i++) {
            INDArray originalImage = originalImageData.getRow(i);
            INDArray augmentedImage = applyTransformations(originalImage);
            augmentedImages.putRow(i, augmentedImage);
        }

        return augmentedImages;
    }

    private static INDArray applyTransformations(INDArray Image) {
        Image = Image.reshape(28, 28); // Ensure the image is 28x28


        //Image = zoom(Image);
        Image = translate(Image);
        Image = rotate(Image);
        Image = blur(Image);
        Image = addNoise(Image, 0.10);




        return Image.reshape(1, 784);
    }


    //-------------------------ZOOM---------------------------------------------------------------


    private static double bilinearInterpolateZ(INDArray image, double x, double y) {
        int x1 = (int) Math.floor(x);
        int x2 = x1 + 1;
        int y1 = (int) Math.floor(y);
        int y2 = y1 + 1;

        double q11 = getPixel(image, x1, y1);
        double q12 = getPixel(image, x1, y2);
        double q21 = getPixel(image, x2, y1);
        double q22 = getPixel(image, x2, y2);

        return interpolateZ(interpolateZ(q11, q21, x - x1), interpolateZ(q12, q22, x - x1), y - y1);
    }

    private static double getPixel(INDArray image, int x, int y) {
        if (x >= 0 && x < 28 && y >= 0 && y < 28) {
            return image.getDouble(x, y);
        } else {
            return 0;
        }
    }

    private static double interpolateZ(double a, double b, double t) {
        return a + t * (b - a);
    }


    private static INDArray zoom(INDArray image) {
        Random rand = new Random();
        double scale = 0.4 + (rand.nextDouble() * 1.0);

        INDArray zoomed = Nd4j.zeros(28, 28);
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                double srcX = (i - 14) / scale + 14;
                double srcY = (j - 14) / scale + 14;
                zoomed.putScalar(i, j, bilinearInterpolateZ(image, srcX, srcY));
            }
        }
        return zoomed;
    }

    //-------------------------TRANSLATION---------------------------------------------------------------


    private static INDArray translate(INDArray image) {
        Random random = new Random();

        // Generate a random translation between 0 and 7 for x and y directions
        int translationX = random.nextInt(5);
        int translationY = random.nextInt(5);

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


    //-------------------------ROTATION---------------------------------------------------------------

    private static double interpolateR(double val1, double val2, double fraction) {
        return val1 * (1 - fraction) + val2 * fraction;
    }

    private static double bilinearInterpolateR(INDArray image, double x, double y) {
        int x1 = (int) x;
        int y1 = (int) y;
        int x2 = (int) Math.min(x1 + 1, image.shape()[0] - 1);
        int y2 = (int) Math.min(y1 + 1, image.shape()[1] - 1);

        double r1 = interpolateR(image.getDouble(x1, y1), image.getDouble(x1, y2), y - y1);
        double r2 = interpolateR(image.getDouble(x2, y1), image.getDouble(x2, y2), y - y1);

        return interpolateR(r1, r2, x - x1);
    }

    private static INDArray rotate(INDArray image) {

        Random rand = new Random();

        double angleInDegrees = 1 + rand.nextDouble() * 38;
        double angleInRadians = Math.toRadians(angleInDegrees);

        int width = (int) image.shape()[0];
        int height = (int) image.shape()[1];

        INDArray output = Nd4j.zeros(width, height);

        int centerX = width / 2;
        int centerY = height / 2;

        double cosAngle = Math.cos(angleInRadians);
        double sinAngle = Math.sin(angleInRadians);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sourceX = (x - centerX) * cosAngle + (y - centerY) * sinAngle + centerX;
                double sourceY = (y - centerY) * cosAngle - (x - centerX) * sinAngle + centerY;

                if (sourceX >= 0 && sourceX < width - 1 && sourceY >= 0 && sourceY < height - 1) {
                    output.putScalar(x, y, bilinearInterpolateR(image, sourceX, sourceY));
                } else {
                    output.putScalar(x, y, 0);  // or any other default value
                }
            }
        }

        return output;
    }

    //----------------------- NOISE ---------------------------------------------


    private static INDArray addNoise(INDArray image, double rate) {
        int rows = 28;
        int cols = 28;
        Random rand = new Random();

        // Count the number of zero pixels
        int zeroCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (image.getDouble(i, j) == 0) {
                    zeroCount++;
                }
            }
        }

        // Calculate how many zero pixels to fill based on the given rate
        int pixelsToFill = (int) (rate * zeroCount);

        while (pixelsToFill > 0) {
            int randomRow = rand.nextInt(rows);
            int randomCol = rand.nextInt(cols);
            if (image.getDouble(randomRow, randomCol) == 0) {
                image.putScalar(new int[]{randomRow, randomCol}, rand.nextDouble());
                pixelsToFill--;
            }
        }

        return image;
    }


    //--------------------------- BLUR ------------------------------------------

    public static INDArray blur(INDArray input) {
        // Convertir INDArray en Mat
        Mat mat = new Mat(28, 28, CvType.CV_8U);
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                mat.put(i, j, input.getDouble(i, j));
            }
        }

        // Agrandir l'image à 56x56 avec INTER_LINEAR (ou INTER_CUBIC)
        Mat enlargedMat = new Mat();
        Imgproc.resize(mat, enlargedMat, new Size(600, 600), 0, 0, Imgproc.INTER_LINEAR);

        // Réduire l'image à 28x28 avec INTER_AREA
        Mat reducedMat = new Mat();
        Imgproc.resize(enlargedMat, reducedMat, new Size(28, 28), 0, 0, Imgproc.INTER_AREA);

        // Convertir Mat en INDArray
        INDArray output = Nd4j.create(28, 28);
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                output.putScalar(new int[]{i, j}, reducedMat.get(i, j)[0]);
            }
        }

        return output;

    }
}