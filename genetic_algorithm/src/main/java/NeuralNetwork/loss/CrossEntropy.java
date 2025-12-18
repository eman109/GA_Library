package NeuralNetwork.loss;

public class CrossEntropy implements LossFunction {

    private static final double EPSILON = 1e-15; // Prevent log(0)

    @Override
    public double calcLoss(double[] predicted, double[] actual) {
        double loss = 0.0;
        for (int i = 0; i < predicted.length; i++) {
            // Clip predictions to prevent log(0)
            double p = Math.max(EPSILON, Math.min(1 - EPSILON, predicted[i]));

            // Binary cross-entropy: -[y*log(p) + (1-y)*log(1-p)]
            loss += -(actual[i] * Math.log(p) + (1 - actual[i]) * Math.log(1 - p));
        }
        return loss;
    }

    @Override
    public double[] calcGrad(double[] predicted, double[] actual) {
        double[] grad = new double[predicted.length];
        for (int i = 0; i < predicted.length; i++) {
            // Clip predictions to prevent division by zero
            double p = Math.max(EPSILON, Math.min(1 - EPSILON, predicted[i]));

            // For sigmoid + cross-entropy, the combined gradient simplifies to: (p - y)
            // This is mathematically correct when using sigmoid as final activation
            grad[i] = p - actual[i];
        }
        return grad;
    }
}