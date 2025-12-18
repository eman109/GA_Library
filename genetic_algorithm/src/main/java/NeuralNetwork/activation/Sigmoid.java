package NeuralNetwork.activation;

public class Sigmoid implements Activation {

    @Override
    public double[][] activate(double[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = 1.0 / (1.0 + Math.exp(-input[i][j]));
            }
        }
        return result;
    }

    @Override
    public double[][] derivative(double[][] activatedOutput) {
        // Input should be the ALREADY ACTIVATED output, not raw input
        int rows = activatedOutput.length;
        int cols = activatedOutput[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // sigmoid'(x) = sigmoid(x) * (1 - sigmoid(x))
                // activatedOutput is already sigmoid(x)
                result[i][j] = activatedOutput[i][j] * (1.0 - activatedOutput[i][j]);
            }
        }
        return result;
    }
}