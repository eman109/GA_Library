package NeuralNetwork;

import NeuralNetwork.activation.*;
import NeuralNetwork.intializers.*;
import NeuralNetwork.loss.*;
import NeuralNetwork.optimizer.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NeuralNetworkGUI extends JFrame {

    private JTextField inputSizeField;
    private JTextField hiddenSizeField;
    private JTextField outputSizeField;
    private JTextField learningRateField;
    private JTextField epochsField;
    private JComboBox<String> activationBox;
    private JComboBox<String> lossBox;
    private JTextArea outputArea;
    private JButton trainButton;

    private NeuralNetwork network;

    public NeuralNetworkGUI() {
        setTitle("Neural Network Trainer");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Create panels
        JPanel configPanel = createConfigPanel();
        JPanel buttonPanel = createButtonPanel();
        JPanel outputPanel = createOutputPanel();

        add(configPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(outputPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Network Configuration"));

        // Input size
        panel.add(new JLabel("Input Size:"));
        inputSizeField = new JTextField("2");
        panel.add(inputSizeField);

        // Hidden layer size
        panel.add(new JLabel("Hidden Layer Size:"));
        hiddenSizeField = new JTextField("3");
        panel.add(hiddenSizeField);

        // Output size
        panel.add(new JLabel("Output Size:"));
        outputSizeField = new JTextField("2");
        panel.add(outputSizeField);

        // Learning rate
        panel.add(new JLabel("Learning Rate:"));
        learningRateField = new JTextField("0.1");
        panel.add(learningRateField);

        // Epochs
        panel.add(new JLabel("Epochs:"));
        epochsField = new JTextField("100");
        panel.add(epochsField);

        // Activation function
        panel.add(new JLabel("Activation:"));
        activationBox = new JComboBox<>(new String[]{"Sigmoid", "ReLU", "Tanh", "Linear"});
        panel.add(activationBox);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        trainButton = new JButton("Train Network (XOR Problem)");
        trainButton.addActionListener(e -> trainNetwork());

        JButton clearButton = new JButton("Clear Output");
        clearButton.addActionListener(e -> outputArea.setText(""));

        panel.add(trainButton);
        panel.add(clearButton);

        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Training Output"));

        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void trainNetwork() {
        try {
            // Get parameters
            int inputSize = Integer.parseInt(inputSizeField.getText());
            int hiddenSize = Integer.parseInt(hiddenSizeField.getText());
            int outputSize = Integer.parseInt(outputSizeField.getText());
            double learningRate = Double.parseDouble(learningRateField.getText());
            int epochs = Integer.parseInt(epochsField.getText());

            // Get activation function
            Activation activation = getActivation();

            // Build network
            network = new NeuralNetwork();
            network.addLayer(new DenseLayer(inputSize, hiddenSize, activation,
                    new RandomInit(-1, 1), learningRate));
            network.addLayer(new DenseLayer(hiddenSize, outputSize, new Sigmoid(),
                    new RandomInit(-1, 1), learningRate));

            network.setLossFunction(new MSE());
            network.setOptimizer(new SGD(learningRate));

            // XOR dataset
            double[][] X = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
            double[][] Y = {{0, 1}, {1, 0}, {1, 0}, {0, 1}};

            outputArea.append("=== Training Started ===\n");
            outputArea.append("Input Size: " + inputSize + "\n");
            outputArea.append("Hidden Size: " + hiddenSize + "\n");
            outputArea.append("Output Size: " + outputSize + "\n");
            outputArea.append("Learning Rate: " + learningRate + "\n");
            outputArea.append("Epochs: " + epochs + "\n\n");

            // Train
            for (int epoch = 0; epoch < epochs; epoch++) {
                double[][] predictions = network.forward(X);
                network.backward(predictions, Y);

                // Calculate loss
                double totalLoss = 0;
                for (int i = 0; i < X.length; i++) {
                    totalLoss += network.getLossFunction().calcLoss(predictions[i], Y[i]);
                }
                double avgLoss = totalLoss / X.length;

                // Print every 10 epochs
                if ((epoch + 1) % 10 == 0) {
                    outputArea.append("Epoch " + (epoch + 1) + " Loss: " +
                            String.format("%.6f", avgLoss) + "\n");
                }
            }

            // Show predictions
            double[][] finalPredictions = network.predict(X);
            outputArea.append("\n=== Final Predictions ===\n");
            for (int i = 0; i < X.length; i++) {
                outputArea.append("Input: [" + X[i][0] + ", " + X[i][1] + "] -> ");
                outputArea.append("Predicted: [" +
                        String.format("%.4f", finalPredictions[i][0]) + ", " +
                        String.format("%.4f", finalPredictions[i][1]) + "]\n");
            }
            outputArea.append("\n");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Training Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Activation getActivation() {
        String selected = (String) activationBox.getSelectedItem();
        switch (selected) {
            case "ReLU": return new ReLU();
            case "Tanh": return new Tanh();
            case "Linear": return new Linear();
            default: return new Sigmoid();
        }
    }

    // Add getter for loss function (needed in NeuralNetwork.java)
    private LossFunction getLossFunction() {
        return network.getLossFunction();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NeuralNetworkGUI());
    }
}