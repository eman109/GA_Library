package NeuralNetwork.activation;

/**
 Interface for activation functions in a neural network.
 Each activation function must implement:
 activate(): compute output for input x
 derivative(): compute gradient for backpropagation
 Uses double[][] to support batch inputs (rows = samples, cols = features/neurons)
 */

public interface Activation {
    double[][] activate(double[][] x); //To apply activation function
    double[][] derivative(double[][] x);//To compute derivative to help in backpropagation
}
