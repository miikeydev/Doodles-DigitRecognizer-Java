import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import com.opencsv.exceptions.CsvValidationException;



public class NeuralNetwork {

    private int inputSize;
    private int hiddenSize;
    private int outputSize;



    public static void main(String[] args) {
        // 1. Load and Preprocess Data
        DataLoader dataLoader = new DataLoader("src/main/ressources/train.csv");
        try {
            dataLoader.loadData();  // Load data from the CSV file

            double[][] X_train = dataLoader.getTrainDataArray();
            X_train = transpose(X_train);

            int[] Y_train = dataLoader.getTrainLabels();
            double[][] X_dev = dataLoader.getDevDataArray();
            int[] Y_dev = dataLoader.getDevLabels();


            // Example usage:
            NeuralNetwork nn = new NeuralNetwork(784, 10, 10);
            double alpha = 0.01;  // Learning rate
            int iterations = 1000;  // Number of iterations
            nn.gradientDescent(X_train, Y_train, alpha, iterations);




        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

// ---------------------------- Maths methods -------------------------------------

    private double[][] generateRandomMatrix(int rows, int cols) {
        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = Math.random() - 0.01;
            }
        }
        return matrix;
    }

    private double[] generateRandomArray(int size) {
        double[] array = new double[size];
        for (int i = 0; i < size; i++) {
            array[i] = Math.random() - 0.01;
        }
        return array;
    }


    public double[][] addMatrices(double[][] A, double[] B) {
        int rows = A.length;
        int cols = A[0].length;

        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = A[i][j] + B[i];
            }
        }

        return result;
    }

    public double[][] matrixSub(double[][] A, double[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int rowsB = B.length;
        int colsB = B[0].length;

        if(rowsA != rowsB || colsA != colsB) {
            throw new IllegalArgumentException("The dimensions of matrices A and B do not match.");
        }

        double[][] result = new double[rowsA][colsA];
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsA; j++) {
                result[i][j] = A[i][j] - B[i][j];
            }
        }
        return result;
    }


    public double[] vectorSub(double[] A, double[] B) {
        int size = A.length;
        double[] result = new double[size];

        for (int i = 0; i < size; i++) {
            result[i] = A[i] - B[i];
        }

        return result;
    }


    public double[][] matrixMultiply(double[][] A, double[][] B) {
        int ARows = A.length;
        int ACols = A[0].length;
        int BCols = B[0].length;

        double[][] result = new double[ARows][BCols];

        for (int i = 0; i < ARows; i++) {
            for (int j = 0; j < BCols; j++) {
                result[i][j] = 0.0;
                for (int k = 0; k < ACols; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return result;
    }


    //it is for the backpropagation step
    public double[][] elementWiseMul(double[][] A, double[][] B) {
        int rows = A.length;
        int cols = A[0].length;

        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = A[i][j] * B[i][j];
            }
        }

        return result;
    }

    public static double[][] transpose(double[][] A) {
        int rows = A.length;
        int cols = A[0].length;

        double[][] result = new double[cols][rows];

        for (int i = 0; i < rows; i++) {
             for (int j = 0; j < cols; j++) {
                result[j][i] = A[i][j];
            }
        }

        return result;
    }

    public double[] sum(double[][] A, int axis) {
        if (axis == 1) {
            double[] result = new double[A.length];
            for (int i = 0; i < A.length; i++) {
                double sum = 0;
                for (int j = 0; j < A[0].length; j++) {
                    sum += A[i][j];
                }
                result[i] = sum;
            }
            return result;
        } else {
            // Handle other axis or throw an exception
            throw new IllegalArgumentException("Invalid axis value.");
        }
    }

    public double[][] scalarMultiply(double[][] matrix, double scalar) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix[i][j] * scalar;
            }
        }

        return result;
    }

    public double[] scalarMultiply(double[] vector, double scalar) {
        int size = vector.length;
        double[] result = new double[size];

        for (int i = 0; i < size; i++) {
            result[i] = vector[i] * scalar;
        }

        return result;
    }

    public double[][] oneHotEncoding(int[] Y) {
        int numberOfSamples = Y.length;
        int numberOfClasses = 10;  // For digits 0-9

        double[][] oneHotY = new double[numberOfSamples][numberOfClasses];

        for (int i = 0; i < numberOfSamples; i++) {
            oneHotY[i][Y[i]] = 1.0;
        }

        return oneHotY;
    }


    private double[][] ReLU(double[][] Z) {
        int rows = Z.length;
        int cols = Z[0].length;
        double[][] A = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                A[i][j] = Math.max(0, Z[i][j]);
            }
        }
        return A;
    }

    private double[][] softmax(double[][] Z) {
        int rows = Z.length;
        int cols = Z[0].length;
        double[][] A = new double[rows][cols];
        for (int j = 0; j < cols; j++) {
            double colSum = 0;
            for (int i = 0; i < rows; i++) {
                colSum += Math.exp(Z[i][j]);
            }
            for (int i = 0; i < rows; i++) {
                A[i][j] = Math.exp(Z[i][j]) / colSum;
            }
        }
        return A;
    }

    private double[][] ReLU_D(double[][] Z) {
        int rows = Z.length;
        int cols = Z[0].length;
        double[][] D = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                D[i][j] = Z[i][j] > 0 ? 1 : 0;
            }
        }
        return D;
    }

//-------------------- Method for NN -------------------------

    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
    }


    public Map<String, Object> gradientDescent(double[][] X, int[] Y, double alpha, int iterations) {
        // Initialize parameters
        double[][] W1 = generateRandomMatrix(hiddenSize, inputSize);
        double[] b1 = generateRandomArray(hiddenSize);
        double[][] W2 = generateRandomMatrix(outputSize, hiddenSize);
        double[] b2 = generateRandomArray(outputSize);

        for (int i = 0; i < iterations; i++) {
            Map<String, double[][]> forwardResults = forwardPropagation(W1, b1, W2, b2, X);
            double[][] Z1 = forwardResults.get("Z1");
            double[][] A1 = forwardResults.get("A1");
            double[][] Z2 = forwardResults.get("Z2");
            double[][] A2 = forwardResults.get("A2");

            Map<String, Object> gradients = backwardPropagation(Z1, A1, Z2, A2, W1, W2, X, Y);
            double[][] dW1 = (double[][]) gradients.get("dW1");
            double[] db1 = (double[]) gradients.get("db1");
            double[][] dW2 = (double[][]) gradients.get("dW2");
            double[] db2 = (double[]) gradients.get("db2");

            Map<String, Object> updatedParams = updateParameters(W1, b1, W2, b2, dW1, db1, dW2, db2, alpha);
            W1 = (double[][]) updatedParams.get("W1");
            b1 = (double[]) updatedParams.get("b1");
            W2 = (double[][]) updatedParams.get("W2");
            b2 = (double[]) updatedParams.get("b2");

            if (i % 1 == 0) {
                System.out.println("Iteration: " + i);
                int[] predictions = getPredictions(A2);
                System.out.println("Accuracy: " + getAccuracy(predictions, Y) + " %");
            }
        }

        Map<String, Object> finalParams = new HashMap<>();
        finalParams.put("W1", W1);
        finalParams.put("b1", b1);
        finalParams.put("W2", W2);
        finalParams.put("b2", b2);

        return finalParams;
    }

    public Map<String, Object> updateParameters(double[][] W1, double[] b1, double[][] W2, double[] b2,
                                                double[][] dW1, double[] db1, double[][] dW2, double[] db2,
                                                double alpha){

        W1 = matrixSub(W1, scalarMultiply(dW1, alpha));
        b1 = vectorSub(b1, scalarMultiply(db1, alpha));
        W2 = matrixSub(W2, scalarMultiply(dW2, alpha));
        b2 = vectorSub(b2, scalarMultiply(db2, alpha));

        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("W1", W1);
        updateParams.put("b1", b1);
        updateParams.put("W2", W2);
        updateParams.put("b2", b2);

        return updateParams;
    }


    public Map<String, double[][]> forwardPropagation(double[][] W1, double[] b1, double[][] W2, double[] b2, double[][] X) {
        double[][] Z1 = addMatrices(matrixMultiply(W1, X), b1);
        double[][] A1 = ReLU(Z1);
        double[][] Z2 = addMatrices(matrixMultiply(W2, A1), b2);
        double[][] A2 = softmax(Z2);

        Map<String, double[][]> results = new HashMap<>();
        results.put("Z1", Z1);
        results.put("A1", A1);
        results.put("Z2", Z2);
        results.put("A2", A2);

        return results;
    }




    public Map<String, Object> backwardPropagation(double[][] Z1, double[][] A1, double[][] Z2, double[][] A2,
                                                   double[][] W1, double[][] W2, double[][] X, int[] Y) {

        int m = Y.length;
        double[][] oneHotY = oneHotEncoding(Y);
        oneHotY = transpose(oneHotY);

        double[][] dZ2 = matrixSub(A2, oneHotY);
        double[][] dW2 = scalarMultiply(matrixMultiply(dZ2, transpose(A1)), 1.0 / m);
        double[] db2 = scalarMultiply(sum(dZ2,1),1.0 / m);
        double[][] dZ1 = elementWiseMul(matrixMultiply(transpose(W2), dZ2), ReLU_D(Z1));
        double[][] dW1 = scalarMultiply(matrixMultiply(dZ1, transpose(X)), 1.0 / m);
        double[] db1 = scalarMultiply(sum(dZ1,1),1.0/m);

        Map<String, Object> gradients = new HashMap<>();
        gradients.put("dW1", dW1);
        gradients.put("db1", db1);
        gradients.put("dW2", dW2);
        gradients.put("db2", db2);

        return gradients;
    }

    public int[] getPredictions(double[][] A2) {
        int samples = A2[0].length;
        int[] predictions = new int[samples];

        for (int i = 0; i < samples; i++) {
            double maxVal = A2[0][i];
            int maxIndex = 0;

            for (int j = 1; j < A2.length; j++) {
                if (A2[j][i] > maxVal) {
                    maxVal = A2[j][i];
                    maxIndex = j;
                }
            }
            predictions[i] = maxIndex;
        }

        return predictions;
    }

    public double getAccuracy(int[] predictions, int[] Y) {
        int correct = 0;
        for (int i = 0; i < predictions.length; i++) {
            if (predictions[i] == Y[i]) {
                correct++;
            }
        }
        return (double) correct / predictions.length * 100.0;  // Return accuracy in percentage
    }



}
