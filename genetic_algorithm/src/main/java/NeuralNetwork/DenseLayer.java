package NeuralNetwork;

import NeuralNetwork.activation.Activation;
import NeuralNetwork.intializers.Initializer;
import NeuralNetwork.optimizer.Optimizer;


import NeuralNetwork.activation.Activation;
import NeuralNetwork.intializers.Initializer;
import NeuralNetwork.optimizer.Optimizer;

public class DenseLayer extends Layer {

    private int inputSize;
    private int outputSize;
    private double[][] weights;
    private double[] biases;
    private Activation activation;

    private double[][] inputCache;      // Store input for backprop
    private double[][] preActivation;   // Store Z = W*X + b (CRITICAL!)
    private double[][] postActivation;  // Store activated output
    private double learningRate;

    public DenseLayer(int inputSize, int outputSize, Activation activation,
                      Initializer initializer, double learningRate) {
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

        // Pre-activation: Z = X * W + b
        this.preActivation = new double[batchSize][outputSize];
        for (int i = 0; i < batchSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                preActivation[i][j] = biases[j];
                for (int k = 0; k < inputSize; k++) {
                    preActivation[i][j] += input[i][k] * weights[k][j];
                }
            }
        }

        // Post-activation: A = activation(Z)
        this.postActivation = activation.activate(preActivation);
        return postActivation;
    }

    @Override
    public double[][] backward(double[][] gradOutput, Optimizer optimizer) {
        int batchSize = inputCache.length;

        // ═══════════════════════════════════════════════════════════
        // CRITICAL FIX: Use PRE-ACTIVATION values for derivative!
        // ═══════════════════════════════════════════════════════════
        double[][] activationGrad = activation.derivative(preActivation);
        double[][] gradZ = new double[batchSize][outputSize];

        // Element-wise multiplication: dL/dZ = dL/dA ⊙ dA/dZ
        for (int i = 0; i < batchSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                gradZ[i][j] = gradOutput[i][j] * activationGrad[i][j];
            }
        }

        // Gradient w.r.t weights: dL/dW = X^T * dL/dZ
        double[][] gradWeights = new double[inputSize][outputSize];
        for (int i = 0; i < batchSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                for (int k = 0; k < inputSize; k++) {
                    gradWeights[k][j] += inputCache[i][k] * gradZ[i][j];
                }
            }
        }

        // Average gradients over batch
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                gradWeights[i][j] /= batchSize;
            }
        }

        // Gradient w.r.t biases: dL/db = sum(dL/dZ)
        double[] gradBiases = new double[outputSize];
        for (int i = 0; i < batchSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                gradBiases[j] += gradZ[i][j];
            }
        }

        // Average bias gradients
        for (int j = 0; j < outputSize; j++) {
            gradBiases[j] /= batchSize;
        }

        // Update weights using optimizer
        optimizer.updateWeights(weights, gradWeights, learningRate);

        // Update biases
        for (int j = 0; j < outputSize; j++) {
            biases[j] -= learningRate * gradBiases[j];
        }

        // Gradient w.r.t input: dL/dX = dL/dZ * W^T
        double[][] gradInput = new double[batchSize][inputSize];
        for (int i = 0; i < batchSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                gradInput[i][j] = 0;
                for (int k = 0; k < outputSize; k++) {
                    gradInput[i][j] += gradZ[i][k] * weights[j][k];
                }
            }
        }

        return gradInput;
    }

    // Getters for debugging
    public double[][] getWeights() {
        return weights;
    }

    public double[] getBiases() {
        return biases;
    }
}