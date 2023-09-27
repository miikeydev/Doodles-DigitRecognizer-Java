public class NeuralNetwork {

    private int inputSize;
    private int hiddenSize;
    private int outputSize;
    private double[][] W1;
    private double[][] W2;
    private double[] b1;
    private double[] b2;

    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        initParams();
    }

    private void initParams() {
        W1 = generateRandomMatrix(hiddenSize, inputSize);
        b1 = generateRandomArray(hiddenSize);
        W2 = generateRandomMatrix(outputSize, hiddenSize);
        b2 = generateRandomArray(outputSize);
    }

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

    // The remaining methods will be added here ...

    public static void main(String[] args) {
        // Example usage:
        NeuralNetwork nn = new NeuralNetwork(784, 10, 10);
        // You can now use the initialized W1, b1, W2, b2 for further operations.


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

    public void forwardProp(double[][] X) {
        double[][] Z1 = addMatrices(matrixMultiply(W1, X), b1);
        double[][] A1 = ReLU(Z1);
        double[][] Z2 = addMatrices(matrixMultiply(W2, A1), b2);
        double[][] A2 = softmax(Z2);
        // Store Z1, A1, Z2, A2 for later use in backpropagation
    }

    private double[][] ReLU_deriv(double[][] Z) {
        int rows = Z.length;
        int cols = Z[0].length;
        double[][] deriv = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                deriv[i][j] = Z[i][j] > 0 ? 1 : 0;
            }
        }
        return deriv;
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
        int rows = A.length;
        int cols = A[0].length;

        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = A[i][j] - B[i][j];
            }
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

    public double[][] transpose(double[][] A) {
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
        int maxVal = 0;
        for (int i = 0; i < Y.length; i++) { // Corrected typo: 'lenght' to 'length'
            if (Y[i] > maxVal) {
                maxVal = Y[i];
            }
        }

        double[][] oneHotY = new double[Y.length][maxVal + 1]; // Transposed the dimensions
        for (int i = 0; i < Y.length; i++) {
            oneHotY[i][Y[i]] = 1.0; // Changed the value to 1.0 (double) and corrected indexing
        }
        return oneHotY;
    }


    public Map<String, Object> backwardPropagation(double[][] Z1, double[][] A1, double[][] Z2, double[][] A2,
        double[][] W1, double[][] W2, double[][] X, int[] Y) {

            int m = Y.length;
            double[][] oneHotY = oneHotEncoding(Y);
            double[][] dZ2 = matrixSub(A2, oneHotY);
            double[][] dW2 = scalarMultiply(matrixMultiply(dZ2, transpose(A1)), 1.0 / m);
            double[] db2 = scalarMultiply(sum(dZ2,1),1.0 / m);
            double[][] dZ1 = elementWiseMul(matrixMultiply(transpose(W2), dZ2), ReLU_deriv(Z1));
            double[][] dW1 = scalarMultiply(matrixMultiply(dZ1, transpose(X)), 1.0 / m);
            double[] db1 = scalarMultiply(sum(dZ1,1),1.0/m);

            Map<String, Object> gradients = new Hashmap<>();
            gradients.put("dW1", dW1);
            gradients.put("db1", db1);
            gradients.put("dW2", dW2);
            gradients.put("db2", db2);

            return gradients;
    }
}
