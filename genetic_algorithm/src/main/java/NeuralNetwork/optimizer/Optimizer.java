package NeuralNetwork.optimizer;

public interface Optimizer {
    void updateWeights(double[][] weights, double[][] gradients, double learningRate);
}


