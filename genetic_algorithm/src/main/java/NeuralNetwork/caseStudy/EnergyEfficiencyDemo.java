package NeuralNetwork.caseStudy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import NeuralNetwork.DenseLayer;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.activation.ReLU;
import NeuralNetwork.activation.Sigmoid;
import NeuralNetwork.initializers.XavierInit;
import NeuralNetwork.loss.CrossEntropy;
import NeuralNetwork.optimizer.SGD;

public class EnergyEfficiencyDemo {

    public static void main(String[] args) {
        List<double[]> features = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        InputStream is = EnergyEfficiencyDemo.class
                .getClassLoader()
                .getResourceAsStream("dataset/energy_efficiency_data_1A.csv");

        if (is == null) {
            throw new RuntimeException("Dataset not found in resources!");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",");
                double[] row = new double[8];

                for (int i = 0; i < 8; i++) {
                    String val = values[i].trim();
                    row[i] = val.isEmpty() ? 0.0 : Double.parseDouble(val);
                }

                String efficiency = values[10].trim();
                labels.add(efficiency.equalsIgnoreCase("Efficient") ? 0 : 1);
                features.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Loaded " + features.size() + " samples");

        // ===== Normalization =====
        double[][] data = features.toArray(new double[0][]);
        for (int col = 0; col < 8; col++) {
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (double[] row : data) {
                min = Math.min(min, row[col]);
                max = Math.max(max, row[col]);
            }
            double range = max - min == 0 ? 1 : max - min;
            for (double[] row : data) {
                row[col] = (row[col] - min) / range;
            }
        }

        // ===== Train/Test Split =====
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < data.length; i++) indices.add(i);
        Collections.shuffle(indices, new java.util.Random(42));

        int trainSize = (int) (data.length * 0.8);

        double[][] X_train = new double[trainSize][8];
        double[][] X_test = new double[data.length - trainSize][8];
        double[][] y_train = new double[trainSize][1];
        double[][] y_test = new double[data.length - trainSize][1];

        for (int i = 0; i < trainSize; i++) {
            int idx = indices.get(i);
            X_train[i] = data[idx];
            y_train[i][0] = labels.get(idx);
        }
        for (int i = trainSize; i < data.length; i++) {
            int idx = indices.get(i);
            X_test[i - trainSize] = data[idx];
            y_test[i - trainSize][0] = labels.get(idx);
        }

        // ===== Hyperparameters =====
        double lr = 0.01;
        double l2Lambda = 0.001;   // L2 regularization (try 0, 0.0001, 0.001, 0.01)
        double dropoutRate = 0.2;  // Dropout rate (try 0, 0.1, 0.2, 0.3)
        int hiddenSize1 = 16;      // Reduced from 32
        int hiddenSize2 = 8;       // Reduced from 16

        System.out.println("=== Configuration ===");
        System.out.println("Architecture: 8 → " + hiddenSize1 + " → " + hiddenSize2 + " → 1");
        System.out.println("Learning Rate: " + lr);
        System.out.println("L2 Lambda: " + l2Lambda);
        System.out.println("Dropout Rate: " + dropoutRate);
        System.out.println();

        // ===== Neural Network with Regularization =====
        NeuralNetwork nn = new NeuralNetwork();

        DenseLayer layer1 = new DenseLayer(8, hiddenSize1, new ReLU(),
                new XavierInit(), lr, l2Lambda, dropoutRate);
        DenseLayer layer2 = new DenseLayer(hiddenSize1, hiddenSize2, new ReLU(),
                new XavierInit(), lr, l2Lambda, dropoutRate);
        DenseLayer layer3 = new DenseLayer(hiddenSize2, 1, new Sigmoid(),
                new XavierInit(), lr, 0.0, 0.0); // No dropout on output

        nn.addLayer(layer1);
        nn.addLayer(layer2);
        nn.addLayer(layer3);
        layer3.setAsOutputLayer(true);

        nn.setLossFunction(new CrossEntropy());
        nn.setOptimizer(new SGD(lr));

        System.out.println("Training started (300 epochs)...");
        System.out.println("Epoch | Train Loss | Train Acc | Test Acc | Gap");
        System.out.println("------|------------|-----------|----------|-----");

        double bestTestAcc = 0;
        int patience = 0;
        int maxPatience = 50;

        // Training with early stopping check
        for (int epoch = 0; epoch < 300; epoch++) {
            // Set training mode
            layer1.setTraining(true);
            layer2.setTraining(true);

            double[][] predictions = nn.forward(X_train);
            nn.backward(predictions, y_train);

            // Check every 10 epochs
            if ((epoch + 1) % 10 == 0 || epoch == 0) {
                // Calculate train loss (including L2 regularization)
                double totalLoss = 0;
                for (int i = 0; i < X_train.length; i++) {
                    totalLoss += nn.getLossFunction().calcLoss(predictions[i], y_train[i]);
                }
                // Add L2 loss from all layers
                totalLoss += layer1.getL2Loss() + layer2.getL2Loss();
                double avgLoss = totalLoss / X_train.length;

                // Calculate train accuracy
                int trainCorrect = 0;
                for (int i = 0; i < X_train.length; i++) {
                    int predicted = predictions[i][0] > 0.5 ? 1 : 0;
                    if (predicted == (int) y_train[i][0]) trainCorrect++;
                }
                double trainAcc = 100.0 * trainCorrect / X_train.length;

                // Calculate test accuracy (disable dropout for testing)
                layer1.setTraining(false);
                layer2.setTraining(false);

                int testCorrect = 0;
                for (int i = 0; i < X_test.length; i++) {
                    double[][] pred = nn.forward(new double[][]{X_test[i]});
                    int predicted = pred[0][0] > 0.5 ? 1 : 0;
                    if (predicted == (int) y_test[i][0]) testCorrect++;
                }
                double testAcc = 100.0 * testCorrect / X_test.length;

                double gap = trainAcc - testAcc;
                System.out.printf("%5d | %10.4f | %8.2f%% | %7.2f%% | %4.1f%%",
                        (epoch + 1), avgLoss, trainAcc, testAcc, gap);

                // Check for overfitting
                if (gap > 10) {
                    System.out.print(" ⚠");
                }
                System.out.println();

                // Early stopping check
                if (testAcc > bestTestAcc) {
                    bestTestAcc = testAcc;
                    patience = 0;
                } else {
                    patience++;
                }

                if (patience >= maxPatience / 10) {
                    System.out.println("Early stopping at epoch " + (epoch + 1));
                    break;
                }
            }
        }

        // ===== Final Evaluation =====
        layer1.setTraining(false);
        layer2.setTraining(false);

        System.out.println("\n=== Final Results ===");

        // Train accuracy
        double[][] trainPreds = nn.forward(X_train);
        int trainCorrect = 0;
        for (int i = 0; i < X_train.length; i++) {
            int predicted = trainPreds[i][0] > 0.5 ? 1 : 0;
            if (predicted == (int) y_train[i][0]) trainCorrect++;
        }
        double trainAcc = 100.0 * trainCorrect / X_train.length;

        // Test accuracy
        int testCorrect = 0;
        for (int i = 0; i < X_test.length; i++) {
            double[][] pred = nn.forward(new double[][]{X_test[i]});
            int predicted = pred[0][0] > 0.5 ? 1 : 0;
            if (predicted == (int) y_test[i][0]) testCorrect++;
        }
        double testAcc = 100.0 * testCorrect / X_test.length;

        System.out.printf("Training Accuracy: %.2f%% (%d/%d correct)%n",
                trainAcc, trainCorrect, X_train.length);
        System.out.printf("Test Accuracy: %.2f%% (%d/%d correct)%n",
                testAcc, testCorrect, X_test.length);
        System.out.printf("Gap: %.2f%%\n", trainAcc - testAcc);

        if (trainAcc - testAcc > 5) {
            System.out.println("\n⚠️  WARNING: Model is likely OVERFITTING!");
            System.out.println("   Train accuracy is significantly higher than test accuracy.");
            System.out.println("   Try: increase L2 lambda, increase dropout, reduce model size.");
        } else {
            System.out.println("\n✓ Model appears to be generalizing well.");
        }
    }
}