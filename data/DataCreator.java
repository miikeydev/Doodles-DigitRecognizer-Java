package data;

import java.util.Random;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;


import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;


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
        Image = toWhite(Image);
        //Image = addNoise(Image, 0.10);




        return Image.reshape(1, 784);
    }


    //--------------------------HELPER METHOD ------------------------------------


    private static Mat indArrayToMat(INDArray input) {
        Mat mat = new Mat(28, 28, CvType.CV_32F);
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                mat.put(i, j, input.getDouble(i, j));
            }
        }
        return mat;
    }

    private static INDArray matToIndArray(Mat mat) {
        INDArray output = Nd4j.create(28, 28);
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                output.putScalar(i, j, mat.get(i, j)[0]);
            }
        }
        return output;
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

    private static INDArray rotate(INDArray image) {
        Mat mat = indArrayToMat(image);

        Random rand = new Random();

        // Rotate between 0 and 35 degrees.
        double angleInDegrees = rand.nextDouble() * 35;

        // Calculate rotation matrix.
        Point center = new Point(mat.cols() / 2.0, mat.rows() / 2.0);
        Mat rotMatrix = Imgproc.getRotationMatrix2D(center, angleInDegrees, 1);

        // Apply the rotation.
        Mat rotatedMat = new Mat();
        Imgproc.warpAffine(mat, rotatedMat, rotMatrix, mat.size());

        return matToIndArray(rotatedMat);
    }



    //----------------------- NOISE ---------------------------------------------


    private static INDArray addNoise(INDArray image, double rate) {
        int rows = 28;
        int cols = 28;
        Random rand = new Random();


        int zeroCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (image.getDouble(i, j) == 0) {
                    zeroCount++;
                }
            }
        }


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

    public static INDArray toWhite(INDArray input) {
        // Convertir INDArray en Mat
        Mat mat = new Mat(28, 28, CvType.CV_8U);
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                mat.put(i, j, input.getDouble(i, j));
            }
        }

        // Scale up the image to 56x56 with INTER_LINEAR
        Mat enlargedMat = new Mat();
        Imgproc.resize(mat, enlargedMat, new Size(600, 600), 0, 0, Imgproc.INTER_LINEAR);

        // Scale down the image to 28x28 with INTER_AREA
        Mat reducedMat = new Mat();
        Imgproc.resize(enlargedMat, reducedMat, new Size(28, 28), 0, 0, Imgproc.INTER_AREA);

        // Convert Mat to INDArray
        INDArray output = Nd4j.create(28, 28);
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                output.putScalar(new int[]{i, j}, reducedMat.get(i, j)[0]);
            }
        }

        return output;

    }
}