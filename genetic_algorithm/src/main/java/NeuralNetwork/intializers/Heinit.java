package NeuralNetwork.intializers;



import java.util.Random;

/**
 * He initialization - optimal for ReLU activation functions
 */
public class Heinit implements Initializer {

    @Override
    public double[][] initialize(int rows, int cols) {
        double[][] w = new double[rows][cols];
        Random rand = new Random();
        double std = Math.sqrt(2.0 / rows);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                w[i][j] = rand.nextGaussian() * std;
            }
        }
        return w;
    }

    @Override
    public double[] initialize(int size) {
        double[] b = new double[size];
        for (int i = 0; i < size; i++) {
            b[i] = 0.01;
        }
        return b;
    }
}
