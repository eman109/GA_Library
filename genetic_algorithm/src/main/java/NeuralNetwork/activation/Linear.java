package NeuralNetwork.activation;

public class Linear implements Activation{

    @Override
    public double[][] activate(double[][] input) {
        return input;
    }

    @Override
    public double[][] derivative(double[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = 1.0;
            }
        }
        return result;
    }
}
