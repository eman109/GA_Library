package NeuralNetwork.activation;

public class Tanh implements Activation{

    @Override
    public double[][] activate(double[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = Math.tanh(input[i][j]);
            }
        }
        return result;
    }

    @Override
    public double[][] derivative(double[][] input) {
        double[][] activated = activate(input);
        int rows = activated.length;
        int cols = activated[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = 1 - (activated[i][j] * activated[i][j]);
            }
        }
        return result;
    }
}
