package NeuralNetwork;

import NeuralNetwork.activation.Activation;
import NeuralNetwork.activation.Sigmoid;
import NeuralNetwork.initializers.Initializer;
import NeuralNetwork.optimizer.Optimizer;

public class DenseLayer extends Layer {

    private int inputSize;
    private int outputSize;
    private double[][] weights;
    private double[] biases;
    private Activation activation;

    private double[][] inputCache;     // Store input for backprop
    private double[][] outputCache;    // Store activated output for backprop
    private double learningRate;       // learning rate for weight/bias updates
    private boolean isOutputLayer;     // Flag for special handling of output layer
    private double l2Lambda;           // L2 regularization parameter (0 = no regularization)
    private double dropoutRate;        // Dropout rate (0 = no dropout)
    private boolean training;          // Training mode flag
    private double[][] dropoutMask;    // Mask for dropout

    /**
     * Constructor with L2 regularization and dropout
     */
    public DenseLayer(int inputSize, int outputSize, Activation activation,
                      Initializer initializer, double learningRate, double l2Lambda, double dropoutRate) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.activation = activation;
        this.learningRate = learningRate;
        this.isOutputLayer = false;
        this.l2Lambda = l2Lambda;
        this.dropoutRate = dropoutRate;
        this.training = true;

        // Initialize weights and biases
        this.weights = initializer.initialize(inputSize, outputSize);
        this.biases = initializer.initialize(outputSize);
    }

    /**
     * Constructor without regularization (backward compatible)
     */
    public DenseLayer(int inputSize, int outputSize, Activation activation,
                      Initializer initializer, double learningRate) {
        this(inputSize, outputSize, activation, initializer, learningRate, 0.0, 0.0);
    }

    /**
     * Mark this layer as the output layer.
     */
    public void setAsOutputLayer(boolean isOutput) {
        this.isOutputLayer = isOutput;
    }

    /**
     * Set training mode
     */
    public void setTraining(boolean training) {
        this.training = training;
    }

    @Override
    public double[][] forward(double[][] input) {
        this.inputCache = input;
        int batchSize = input.length;

        // Linear transformation: input * weights + biases
        double[][] preActivation = new double[batchSize][outputSize];
        for (int i = 0; i < batchSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                preActivation[i][j] = biases[j];
                for (int k = 0; k < inputSize; k++) {
                    preActivation[i][j] += input[i][k] * weights[k][j];
                }
            }
        }

        // Apply activation function
        this.outputCache = activation.activate(preActivation);

        // Apply dropout during training (not on output layer)
        if (training && dropoutRate > 0 && !isOutputLayer) {
            this.dropoutMask = new double[batchSize][outputSize];
            java.util.Random rand = new java.util.Random();
            double scale = 1.0 / (1.0 - dropoutRate); // Inverted dropout scaling

            for (int i = 0; i < batchSize; i++) {
                for (int j = 0; j < outputSize; j++) {
                    if (rand.nextDouble() > dropoutRate) {
                        dropoutMask[i][j] = scale;
                        outputCache[i][j] *= scale;
                    } else {
                        dropoutMask[i][j] = 0;
                        outputCache[i][j] = 0;
                    }
                }
            }
        }

        return outputCache;
    }

    @Override
    public double[][] backward(double[][] gradOutput, Optimizer optimizer) {
        int batchSize = inputCache.length;
        double[][] gradActivation;

        // Apply dropout mask to gradient if dropout was used
        if (training && dropoutRate > 0 && !isOutputLayer && dropoutMask != null) {
            for (int i = 0; i < batchSize; i++) {
                for (int j = 0; j < outputSize; j++) {
                    gradOutput[i][j] *= dropoutMask[i][j];
                }
            }
        }

        // Special handling for output layer with Sigmoid + CrossEntropy
        if (isOutputLayer && activation instanceof Sigmoid) {
            gradActivation = gradOutput;
        } else {
            // For hidden layers, apply the activation derivative
            double[][] activationDerivative = activation.derivative(outputCache);

            // Element-wise multiplication of gradOutput and activation derivative
            gradActivation = new double[batchSize][outputSize];
            for (int i = 0; i < batchSize; i++) {
                for (int j = 0; j < outputSize; j++) {
                    gradActivation[i][j] = gradOutput[i][j] * activationDerivative[i][j];
                }
            }
        }

        // Gradients w.r.t weights and biases
        double[][] gradWeights = new double[inputSize][outputSize];
        double[] gradBiases = new double[outputSize];

        for (int i = 0; i < batchSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                gradBiases[j] += gradActivation[i][j];
                for (int k = 0; k < inputSize; k++) {
                    gradWeights[k][j] += inputCache[i][k] * gradActivation[i][j];
                }
            }
        }

        // Average gradients over batch and add L2 regularization
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                // Average over batch
                gradWeights[i][j] /= batchSize;

                // Add L2 regularization gradient: lambda * w
                if (l2Lambda > 0) {
                    gradWeights[i][j] += l2Lambda * weights[i][j];
                }
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

        // Compute gradient to pass to previous layer
        double[][] gradInput = new double[batchSize][inputSize];
        for (int i = 0; i < batchSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                gradInput[i][j] = 0;
                for (int k = 0; k < outputSize; k++) {
                    gradInput[i][j] += gradActivation[i][k] * weights[j][k];
                }
            }
        }

        return gradInput;
    }

    /**
     * Calculate L2 regularization loss (to add to total loss)
     */
    public double getL2Loss() {
        if (l2Lambda == 0) return 0.0;

        double l2Loss = 0.0;
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                l2Loss += weights[i][j] * weights[i][j];
            }
        }
        return 0.5 * l2Lambda * l2Loss;
    }

    /**
     * Get weight matrix (for inspection/debugging)
     */
    public double[][] getWeights() {
        return weights;
    }

    /**
     * Get bias vector (for inspection/debugging)
     */
    public double[] getBiases() {
        return biases;
    }
}