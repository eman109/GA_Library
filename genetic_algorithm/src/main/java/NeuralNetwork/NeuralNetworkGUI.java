package NeuralNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import NeuralNetwork.*;
import NeuralNetwork.activation.*;
import NeuralNetwork.intializers.*;
import NeuralNetwork.loss.*;
import NeuralNetwork.optimizer.*;

public class NeuralNetworkGUI {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║ ENERGY EFFICIENCY - SINGLE OUTPUT VERSION           ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        // Step 1: Load data
        System.out.println("Step 1: Loading data...");
        List<double[]> features = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        String[] possiblePaths = {
                "src/main/resources/dataset/energy_efficiency_data_1A.csv",
                "resources/dataset/energy_efficiency_data_1A.csv",
                "C:/Users/Basant/Desktop/energy_efficiency_data_1A.csv",
                "energy_efficiency_data_1A.csv"
        };

        BufferedReader br = null;
        boolean fileFound = false;
        for (String path : possiblePaths) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    System.out.println("Found file at: " + path);
                    br = new BufferedReader(new FileReader(file));
                    fileFound = true;
                    break;
                }
            } catch (Exception e) {}
        }

        if (!fileFound) {
            try {
                InputStream is = NeuralNetworkGUI.class.getClassLoader()
                        .getResourceAsStream("dataset/energy_efficiency_data_1A.csv");
                if (is != null) {
                    System.out.println("Found file in resources");
                    br = new BufferedReader(new InputStreamReader(is));
                    fileFound = true;
                }
            } catch (Exception e) {}
        }

        if (!fileFound || br == null) {
            System.err.println("❌ ERROR: Could not find CSV file");
            return;
        }

        try {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(",");
                double[] row = new double[8];
                for (int i = 0; i < 8; i++) {
                    row[i] = values[i].trim().isEmpty() ? 0.0 : Double.parseDouble(values[i].trim());
                }
                labels.add(values[10].trim().equals("Efficient") ? 1 : 0); // 1 = Efficient, 0 = Not Efficient
                features.add(row);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("✓ Loaded " + features.size() + " samples");

        int efficient = 0, inefficient = 0;
        for (int label : labels) {
            if (label == 1) efficient++;
            else inefficient++;
        }
        System.out.println("  - Efficient: " + efficient);
        System.out.println("  - Inefficient: " + inefficient);

        // Step 2: Preprocess
        System.out.println("\nStep 2: Preprocessing data...");
        double[][] data = features.toArray(new double[0][]);
        data = Utils.handleMissingValues(data);
        data = Utils.normalize(data);
        System.out.println("✓ Data preprocessed");

        // Step 3: Prepare X and y arrays
        double[][] X_all = new double[data.length][8];
        double[][] y_all = new double[data.length][1]; // SINGLE output neuron
        for (int i = 0; i < data.length; i++) {
            X_all[i] = data[i];
            y_all[i][0] = labels.get(i); // 1 = Efficient, 0 = Not Efficient
        }

        Utils.shuffle(X_all, y_all);
        System.out.println("✓ Data shuffled");

        // Step 4: Train/Test split 80/20
        int trainSize = (int) (0.8 * data.length);
        int testSize = data.length - trainSize;

        double[][] X_train = new double[trainSize][8];
        double[][] y_train = new double[trainSize][1];
        double[][] X_test = new double[testSize][8];
        double[][] y_test = new double[testSize][1];

        System.arraycopy(X_all, 0, X_train, 0, trainSize);
        System.arraycopy(y_all, 0, y_train, 0, trainSize);
        System.arraycopy(X_all, trainSize, X_test, 0, testSize);
        System.arraycopy(y_all, trainSize, y_test, 0, testSize);

        System.out.println("\n✓ Training samples: " + trainSize);
        System.out.println("✓ Test samples: " + testSize);

        // Step 5: Build network (single output)
        System.out.println("\nStep 5: Building network...");
        NeuralNetwork nn = new NeuralNetwork();
        double learningRate = 0.005;

        nn.addLayer(new DenseLayer(8, 32, new ReLU(), new Heinit(), learningRate));
        nn.addLayer(new DenseLayer(32, 16, new ReLU(), new Heinit(), learningRate));
        nn.addLayer(new DenseLayer(16, 1, new Sigmoid(), new Heinit(), learningRate)); // SINGLE OUTPUT

        nn.setLossFunction(new CrossEntropy());
        nn.setOptimizer(new SGD(learningRate));

        System.out.println("✓ Network built");

        // Step 6: Train
        System.out.println("\nStep 6: Training...");
        nn.train(X_train, y_train, 300, 32);

        // Step 7: Evaluate
        double trainAcc = calculateAccuracy(nn, X_train, y_train);
        double testAcc = calculateAccuracy(nn, X_test, y_test);

        System.out.println("\n" + "=".repeat(40));
        System.out.println("RESULTS");
        System.out.println("=".repeat(40));
        System.out.println("Training Accuracy: " + String.format("%.2f%%", trainAcc));
        System.out.println("Test Accuracy:     " + String.format("%.2f%%", testAcc));
        System.out.println("=".repeat(40));
    }

    private static double calculateAccuracy(NeuralNetwork nn, double[][] X, double[][] y) {
        int correct = 0;
        for (int i = 0; i < X.length; i++) {
            double[][] pred = nn.forward(new double[][]{X[i]});
            int predicted = pred[0][0] > 0.5 ? 1 : 0;
            int actual = (int) y[i][0];
            if (predicted == actual) correct++;
        }
        return correct * 100.0 / X.length;
    }
}
