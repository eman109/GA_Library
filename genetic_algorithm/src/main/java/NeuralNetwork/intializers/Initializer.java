package NeuralNetwork.intializers;



public interface Initializer {
    double[][] initialize(int rows, int cols);   // for weights
    double[] initialize(int size);               // for biases
}
