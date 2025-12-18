package NeuralNetwork.initializers;

import java.util.Random;

public class XavierInit implements Initializer {

    @Override
    public double[][] initialize(int rows, int cols) {
        double[][] w = new double[rows][cols];
        Random rand = new Random();
        double bound = Math.sqrt(6.0 / (rows + cols)); // standard Xavier formula
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                w[i][j] = -bound + 2 * bound * rand.nextDouble();
            }
        }
        return w;
    }

    @Override
    public double[] initialize(int size) {
        double[] b = new double[size];
        // Biases are often initialized to 0 in Xavier
        for (int i = 0; i < size; i++) {
            b[i] = 0;
        }
        return b;
    }
}
