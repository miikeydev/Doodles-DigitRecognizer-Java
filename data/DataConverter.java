package data;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


import java.util.List;

public class DataConverter {

    public static INDArray[] convertToINDArrays(List<Image_> images) {
        int numImages = images.size();
        int numRows = images.get(0).getData().length;
        int numCols = images.get(0).getData()[0].length;

        // Initialize INDArrays
        INDArray X = Nd4j.zeros(numImages, numRows * numCols);
        INDArray Y = Nd4j.zeros(numImages, 1);

        // Populate INDArrays
        for (int i = 0; i < numImages; i++) {
            Image_ img = images.get(i);
            double[][] data = img.getData();
            int label = img.getLabel();

            // Flatten and assign image data to X
            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numCols; c++) {
                    X.putScalar(new int[]{i, r * numCols + c}, data[r][c]);
                }
            }

            // Assign label data to Y
            Y.putScalar(new int[]{i, 0}, label);
        }

        return new INDArray[]{X, Y};
    }
}
