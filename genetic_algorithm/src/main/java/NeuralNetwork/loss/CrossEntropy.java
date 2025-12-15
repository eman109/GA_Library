package NeuralNetwork.loss;

public class CrossEntropy implements LossFunction {

    @Override
    public double calcLoss(double[] predicted, double[] actual) {
        double loss = 0.0;
        for (int i = 0; i < predicted.length; i++) {
            loss -= actual[i] * Math.log(predicted[i] + 1e-9);
        }
        return loss / predicted.length;
    }

    @Override
    public double[] calcGrad(double[] predicted, double[] actual) {
        double[] grad = new double[predicted.length];
        for (int i = 0; i < predicted.length; i++) {
            grad[i] = -actual[i] / (predicted[i] + 1e-9);
        }
        return grad;
    }
}
