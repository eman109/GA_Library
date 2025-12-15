package NeuralNetwork.optimizer;

public class SGD implements Optimizer {

    private double learningRate;

    // Constructor to set learning rate
    public SGD(double learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public void updateWeights(double[][] weights, double[][] gradients, double lr) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                weights[i][j] -= lr * gradients[i][j];
            }
        }
    }
}
