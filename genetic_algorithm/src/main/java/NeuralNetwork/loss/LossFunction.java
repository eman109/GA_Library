package NeuralNetwork.loss;

public interface LossFunction {
    double calcLoss(double[] predicted, double[] actual);
    double[] calcGrad(double[] predicted, double[] actual);
}
