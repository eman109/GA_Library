package NeuralNetwork;

import java.util.Arrays;

public class Utils {

     //Split data into training and testing sets

    public static double[][][] trainTestSplit(double[][] data, double trainRatio) {
        int totalSamples = data.length;
        int trainSize = (int) (totalSamples * trainRatio);

        double[][] trainData = Arrays.copyOfRange(data, 0, trainSize);
        double[][] testData = Arrays.copyOfRange(data, trainSize, totalSamples);

        return new double[][][] {trainData, testData};
    }

     //Normalize data to range [0, 1] using min-max normalization

    public static double[][] normalize(double[][] data) {
        if (data.length == 0) return data;

        int rows = data.length;
        int cols = data[0].length;
        double[][] normalized = new double[rows][cols];

        // Find min and max for each column
        for (int j = 0; j < cols; j++) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            for (int i = 0; i < rows; i++) {
                if (data[i][j] < min) min = data[i][j];
                if (data[i][j] > max) max = data[i][j];
            }

            // Normalize column
            for (int i = 0; i < rows; i++) {
                if (max - min == 0) {
                    normalized[i][j] = 0;
                } else {
                    normalized[i][j] = (data[i][j] - min) / (max - min);
                }
            }
        }

        return normalized;
    }

     //Handle missing values by replacing them with column mean

    public static double[][] handleMissingValues(double[][] data) {
        if (data.length == 0) return data;

        int rows = data.length;
        int cols = data[0].length;
        double[][] filled = new double[rows][cols];

        // Copy data
        for (int i = 0; i < rows; i++) {
            filled[i] = Arrays.copyOf(data[i], cols);
        }

        // Fill missing values with column mean
        for (int j = 0; j < cols; j++) {
            double sum = 0;
            int count = 0;

            // Calculate mean
            for (int i = 0; i < rows; i++) {
                if (!Double.isNaN(data[i][j])) {
                    sum += data[i][j];
                    count++;
                }
            }

            double mean = count > 0 ? sum / count : 0;

            // Fill missing values
            for (int i = 0; i < rows; i++) {
                if (Double.isNaN(filled[i][j])) {
                    filled[i][j] = mean;
                }
            }
        }

        return filled;
    }

     //Shuffle data randomly (useful for training)

    public static void shuffle(double[][] X, double[][] Y) {
        java.util.Random rand = new java.util.Random();
        for (int i = X.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);

            //swap X
            double[] tempX = X[i];
            X[i] = X[j];
            X[j] = tempX;

            //swap Y
            double[] tempY = Y[i];
            Y[i] = Y[j];
            Y[j] = tempY;
        }
    }
}