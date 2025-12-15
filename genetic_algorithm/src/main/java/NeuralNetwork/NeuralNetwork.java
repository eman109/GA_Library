package NeuralNetwork;

import NeuralNetwork.loss.LossFunction;
import NeuralNetwork.optimizer.Optimizer; // Class name should be PascalCase
import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

    private List<Layer> layers;
    private LossFunction lossFunction;
    private Optimizer optimizer;

    public NeuralNetwork() {
        this.layers = new ArrayList<>();
    }

    /**
     * Add a layer to the network
     * @param layer Layer object (DenseLayer, etc.)
     */
    public void addLayer(Layer layer) {
        layers.add(layer);
    }

    /**
     * Set the loss function for training
     * @param lossFunction LossFunction implementation
     */
    public void setLossFunction(LossFunction lossFunction) {
        this.lossFunction = lossFunction;
    }

    /**
     * Set the optimizer for training
     * @param optimizer Optimizer implementation
     */
    public void setOptimizer(Optimizer optimizer) {
        this.optimizer = optimizer;
    }

    /**
     * Forward pass through the network
     * @param input Input batch (2D array: samples x features)
     * @return Output predictions
     */
    public double[][] forward(double[][] input) {
        double[][] output = input;
        for (Layer layer : layers) {
            output = layer.forward(output);
        }
        return output;
    }

    /**
     * Backward pass through the network
     * @param predicted Predictions from forward pass
     * @param actual True labels
     */
    public void backward(double[][] predicted, double[][] actual) {
        double[][] grad = new double[predicted.length][predicted[0].length];

        // Compute gradients for each sample
        for (int i = 0; i < predicted.length; i++) {
            grad[i] = lossFunction.calcGrad(predicted[i], actual[i]);
        }

        // Backpropagate through layers
        for (int i = layers.size() - 1; i >= 0; i--) {
            grad = layers.get(i).backward(grad, optimizer);
        }
    }

    /**
     * Train the network for given epochs
     * @param X Training data (samples x features)
     * @param y Training labels
     * @param epochs Number of epochs
     */
    public void train(double[][] X, double[][] y, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double[][] predictions = forward(X);
            backward(predictions, y);

            // Compute average loss over batch
            double totalLoss = 0;
            for (int i = 0; i < X.length; i++) {
                totalLoss += lossFunction.calcLoss(predictions[i], y[i]);
            }

            System.out.println("Epoch " + (epoch + 1) + " Loss: " + (totalLoss / X.length));
        }
    }

    /**
     * Predict using the trained network
     * @param X Input data
     * @return Predictions
     */
    public double[][] predict(double[][] X) {
        return forward(X);
    }
}
