package NeuralNetwork.initializers;

import java.util.Random;

public class RandomInit implements Initializer {

    private double min;
    private double max;

    public RandomInit(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public double[][] initialize(int rows, int cols) {
        double[][] w = new double[rows][cols];
        Random rand = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                w[i][j] = min + (max - min) * rand.nextDouble();
            }
        }
        return w;
    }

    @Override
    public double[] initialize(int size) {
        double[] b = new double[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            b[i] = min + (max - min) * rand.nextDouble();
        }
        return b;
    }
}
