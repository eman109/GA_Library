package NeuralNetwork.loss;

public class MSE implements LossFunction {

    @Override
    public double calcLoss(double[] predicted, double[] actual) {
        double sum = 0;
        for (int i = 0; i < predicted.length; i++) {
            sum += Math.pow(predicted[i] - actual[i], 2);
        }
        return sum / predicted.length;
    }

    @Override
    public double[] calcGrad(double[] predicted, double[] actual) {
        double[] grad = new double[predicted.length];
        for (int i = 0; i < predicted.length; i++) {
            grad[i] = 2 * (predicted[i] - actual[i]) / predicted.length;
        }
        return grad;
    }
}
