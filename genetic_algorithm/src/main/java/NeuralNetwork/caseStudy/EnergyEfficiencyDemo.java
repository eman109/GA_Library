package NeuralNetwork.caseStudy;

import NeuralNetwork.*;
import NeuralNetwork.activation.*;
import NeuralNetwork.initializers.*;
import NeuralNetwork.loss.*;
import NeuralNetwork.optimizer.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EnergyEfficiencyDemo extends JFrame {

    private JTextField layersField, learningRateField, epochsField, batchSizeField;
    private JComboBox<String> activationBox1, activationBox2, activationBox3, optimizerBox, initBox;
    private JButton trainButton;
    private JTextArea outputArea;

    public EnergyEfficiencyDemo() {
        setTitle("Energy Efficiency Neural Network Trainer");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(10, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Hyperparameters"));

        panel.add(new JLabel("Layers (comma-separated, e.g., 8,32,16):"));
        layersField = new JTextField("8,32,16");
        panel.add(layersField);

        panel.add(new JLabel("Activation Layer 1:"));
        activationBox1 = new JComboBox<>(new String[]{"ReLU", "Sigmoid", "Tanh", "Linear"});
        panel.add(activationBox1);

        panel.add(new JLabel("Activation Layer 2:"));
        activationBox2 = new JComboBox<>(new String[]{"ReLU", "Sigmoid", "Tanh", "Linear"});
        panel.add(activationBox2);

        panel.add(new JLabel("Activation Layer 3:"));
        activationBox3 = new JComboBox<>(new String[]{"ReLU", "Sigmoid", "Tanh", "Linear"});
        panel.add(activationBox3);

        panel.add(new JLabel("Optimizer:"));
        optimizerBox = new JComboBox<>(new String[]{"SGD", "Adam"});
        panel.add(optimizerBox);

        panel.add(new JLabel("Learning Rate:"));
        learningRateField = new JTextField("0.005");
        panel.add(learningRateField);

        panel.add(new JLabel("Initializer:"));
        initBox = new JComboBox<>(new String[]{"Heinit", "XavierInit", "RandomInit"});
        panel.add(initBox);

        panel.add(new JLabel("Epochs:"));
        epochsField = new JTextField("300");
        panel.add(epochsField);

        panel.add(new JLabel("Batch Size:"));
        batchSizeField = new JTextField("32");
        panel.add(batchSizeField);

        trainButton = new JButton("Train Network");
        panel.add(trainButton);

        add(panel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        trainButton.addActionListener(e -> trainNetwork());

        setVisible(true);
    }

    private void trainNetwork() {
        outputArea.setText("");
        outputArea.append("Loading dataset...\n");

        List<double[]> features = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("dataset/energy_efficiency_data_1A.csv")));
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(",");
                double[] row = new double[8];
                for (int i = 0; i < 8; i++) row[i] = values[i].isEmpty() ? 0.0 : Double.parseDouble(values[i]);
                labels.add(values[10].trim().equals("Efficient") ? 1 : 0); // 1 = Efficient, 0 = Not Efficient
                features.add(row);
            }
            br.close();
        } catch (Exception ex) {
            outputArea.append("Error loading dataset!\n");
            return;
        }

        double[][] data = features.toArray(new double[0][]);
        data = Utils.handleMissingValues(data);
        data = Utils.normalize(data);

        double[][] X_all = new double[data.length][8];
        double[][] y_all = new double[data.length][1]; // single output
        for (int i = 0; i < data.length; i++) {
            X_all[i] = data[i];
            y_all[i][0] = labels.get(i);
        }
        Utils.shuffle(X_all, y_all);

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

        // Build network
        String[] layersStr = layersField.getText().split(",");
        int[] layers = new int[layersStr.length];
        for (int i = 0; i < layersStr.length; i++) layers[i] = Integer.parseInt(layersStr[i].trim());

        double learningRate = Double.parseDouble(learningRateField.getText());
        int epochs = Integer.parseInt(epochsField.getText());
        int batchSize = Integer.parseInt(batchSizeField.getText());

        NeuralNetwork nn = new NeuralNetwork();

        for (int i = 0; i < layers.length - 1; i++) {
            Activation activation;
            String actName = i == 0 ? (String) activationBox1.getSelectedItem() :
                    i == 1 ? (String) activationBox2.getSelectedItem() :
                            (String) activationBox3.getSelectedItem();
            switch (actName) {
                case "Sigmoid": activation = new Sigmoid(); break;
                case "Tanh": activation = new Tanh(); break;
                case "Linear": activation = new Linear(); break;
                default: activation = new ReLU(); break;
            }

            Initializer init;
            switch ((String) initBox.getSelectedItem()) {
                case "XavierInit": init = new XavierInit(); break;
                case "RandomInit": init = new RandomInit(-0.1, 0.1); break;
                default: init = new Heinit(); break;
            }

            nn.addLayer(new DenseLayer(layers[i], layers[i + 1], activation, init, learningRate));
        }

        // Output layer: 1 neuron Sigmoid (always)
        Initializer outputInit = initBox.getSelectedItem().equals("XavierInit") ? new XavierInit() :
                initBox.getSelectedItem().equals("RandomInit") ? new RandomInit(-0.1, 0.1) : new Heinit();
        nn.addLayer(new DenseLayer(layers[layers.length - 1], 1, new Sigmoid(), outputInit, learningRate));

        nn.setLossFunction(new CrossEntropy());
        nn.setOptimizer(new SGD(learningRate));

        outputArea.append("Training network...\n");
        nn.train(X_train, y_train, epochs, batchSize);

        double trainAcc = calculateAccuracy(nn, X_train, y_train);
        double testAcc = calculateAccuracy(nn, X_test, y_test);

        outputArea.append(String.format("\nTraining Accuracy: %.2f%%\n", trainAcc));
        outputArea.append(String.format("Test Accuracy: %.2f%%\n", testAcc));
    }

    private double calculateAccuracy(NeuralNetwork nn, double[][] X, double[][] y) {
        int correct = 0;
        for (int i = 0; i < X.length; i++) {
            double[][] pred = nn.forward(new double[][]{X[i]});
            int predicted = pred[0][0] > 0.5 ? 1 : 0;
            int actual = (int) y[i][0];
            if (predicted == actual) correct++;
        }
        return correct * 100.0 / X.length;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EnergyEfficiencyDemo::new);
    }
}