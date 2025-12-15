package NeuralNetwork.activation;

public class ReLU implements Activation{
    @Override
    public double[][] activate(double[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = Math.max(0, input[i][j]);
            }
        }
        return result;
    }

    @Override
    public double[][] derivative(double[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = input[i][j] > 0 ? 1 : 0;
            }
        }
        return result;
    }
}
