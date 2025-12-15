package NeuralNetwork;

import NeuralNetwork.activation.Activation;
import NeuralNetwork.intializers.Initializer;
import NeuralNetwork.optimizer.Optimizer;

public class DenseLayer extends Layer {

    private int inputSize;
    private int outputSize;
    private double[][] weights;
    private double[] biases;
    private Activation activation;

    private double[][] inputCache;  // Store input for backprop
    private double learningRate;    // learning rate for weight/bias updates

    public DenseLayer(int inputSize, int outputSize, Activation activation, Initializer initializer, double learningRate) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.activation = activation;
        this.learningRate = learningRate;

        // Initialize weights and biases
        this.weights = initializer.initialize(inputSize, outputSize);
        this.biases = initializer.initialize(outputSize);
    }

    @Override
    public double[][] forward(double[][] input) {
        this.inputCache = input;

        int batchSize = input.length;
        double[][] output = new double[batchSize][outputSize];

        // Linear transformation: input * weights + biases
        for (int i = 0; i < batchSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                output[i][j] = biases[j];
                for (int k = 0; k < inputSize; k++) {
                    output[i][j] += input[i][k] * weights[k][j];
                }
            }
        }

        // Apply activation function
        output = activation.activate(output);

        return output;
    }

    @Override
    public double[][] backward(double[][] gradOutput, Optimizer optimizer) {
        // Gradient of activation (uses cached forward output)
        double[][] gradActivation = activation.derivative(gradOutput);

        // Gradients w.r.t weights and biases
        double[][] gradWeights = new double[inputSize][outputSize];
        double[] gradBiases = new double[outputSize];

        for (int i = 0; i < inputCache.length; i++) {
            for (int j = 0; j < outputSize; j++) {
                gradBiases[j] += gradActivation[i][j];
                for (int k = 0; k < inputSize; k++) {
                    gradWeights[k][j] += inputCache[i][k] * gradActivation[i][j];
                }
            }
        }

        // Update weights using optimizer
        optimizer.updateWeights(weights, gradWeights, learningRate);

        // Update biases manually (simple SGD step)
        for (int j = 0; j < outputSize; j++) {
            biases[j] -= learningRate * gradBiases[j];
        }

        // Compute gradient to pass to previous layer
        double[][] gradInput = new double[inputCache.length][inputSize];
        for (int i = 0; i < inputCache.length; i++) {
            for (int j = 0; j < inputSize; j++) {
                gradInput[i][j] = 0;
                for (int k = 0; k < outputSize; k++) {
                    gradInput[i][j] += gradActivation[i][k] * weights[j][k];
                }
            }
        }

        return gradInput;
    }
}
