package NeuralNetwork.caseStudy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import NeuralNetwork.DenseLayer;
import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.activation.ReLU;
import NeuralNetwork.activation.Sigmoid;
import NeuralNetwork.intializers.XavierInit;
import NeuralNetwork.loss.CrossEntropy;
import NeuralNetwork.optimizer.SGD;

public class EnergyEfficiencyDemo {

    public static void main(String[] args) {
        List<double[]> features = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        String csvPath = "src/main/resources/dataset/energy_efficiency_data_1A.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
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
                labels.add(efficiency.equals("Efficient") ? 0 : 1);
                features.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Loaded " + features.size() + " samples");

        // Min-max normalization
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

        // Train/test split 80/20 (fixed seed for reproducibility)
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

        // Build neural network
        NeuralNetwork nn = new NeuralNetwork();
        nn.addLayer(new DenseLayer(8, 32, new ReLU(), new XavierInit(),0.0));
        nn.addLayer(new DenseLayer(32, 16, new ReLU(), new XavierInit(),0.0));
        nn.addLayer(new DenseLayer(16, 1, new Sigmoid(), new XavierInit(),0.0));

        nn.setLossFunction(new CrossEntropy());
        nn.setOptimizer(new SGD(1.0));

        System.out.println("Training started (300 epochs)...");
        nn.train(X_train, y_train, 300);

        // Evaluation with threshold 0.6 for stable results
        int correct = 0;
        for (int i = 0; i < X_test.length; i++) {
            double[][] input = new double[][]{X_test[i]};
            double[][] pred = nn.forward(input);
            int predicted = pred[0][0] > 0.6 ? 1 : 0;  // Adjusted threshold
            if (predicted == (int) y_test[i][0]) correct++;
        }

        double accuracy = 100.0 * correct / X_test.length;
        System.out.printf("Test Accuracy: %.2f%% (%d/%d correct)%n", accuracy, correct, X_test.length);
    }
}