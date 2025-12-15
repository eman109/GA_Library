package NeuralNetwork;



import NeuralNetwork.optimizer.Optimizer;

public abstract class Layer {


    public abstract double[][] forward(double[][] input);


    public abstract double[][] backward(double[][] gradOutput, Optimizer optimizer);

}
