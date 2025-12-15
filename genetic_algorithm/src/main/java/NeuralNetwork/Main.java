package NeuralNetwork;

import NeuralNetwork.activation.Sigmoid;
import NeuralNetwork.intializers.RandomInit;
import NeuralNetwork.loss.CrossEntropy;
import NeuralNetwork.loss.LossFunction;
import NeuralNetwork.loss.MSE;
import NeuralNetwork.optimizer.SGD;
import NeuralNetwork.optimizer.Optimizer;

public class Main {
    public static void main(String[] args) {

        // -------------------
        // Test Loss Functions
        // -------------------
        double[] pred = {0.2, 0.8};
        double[] actual = {0, 1};

        LossFunction mse = new MSE();
        LossFunction ce = new CrossEntropy();

        System.out.println("MSE Loss: " + mse.calcLoss(pred, actual));
        System.out.println("CrossEntropy Loss: " + ce.calcLoss(pred, actual));

        // -------------------
        // Test Optimizer
        // -------------------
        double[][] weights = {{0.5, -0.3}, {0.2, 0.1}};
        double[][] grads = {{0.1, -0.2}, {0.05, 0.05}};
        double learningRate = 0.01;

        Optimizer sgd = new SGD(learningRate);
        sgd.updateWeights(weights, grads, learningRate);

        System.out.println("Updated Weights:");
        for (double[] row : weights) {
            for (double w : row) {
                System.out.print(w + " ");
            }
            System.out.println();
        }

        // -------------------
        // Build a Small Neural Network
        // -------------------
        NeuralNetwork nn = new NeuralNetwork();
        nn.addLayer(new DenseLayer(2, 3, new Sigmoid(), new RandomInit(-1, 1), 0.1));
        nn.addLayer(new DenseLayer(3, 2, new Sigmoid(), new RandomInit(-1, 1), 0.1));

        nn.setLossFunction(new MSE());
        nn.setOptimizer(new SGD(0.1));

        // Toy dataset: 4 samples, 2 inputs, 2 outputs
        double[][] X = {
                {0, 0},
                {0, 1},
                {1, 0},
                {1, 1}
        };
        double[][] Y = {
                {0, 1},
                {1, 0},
                {1, 0},
                {0, 1}
        };

        // Train for 10 epochs
        nn.train(X, Y, 10);

        // Predict
        double[][] predictions = nn.predict(X);
        System.out.println("Predictions:");
        for (double[] p : predictions) {
            for (double v : p) {
                System.out.printf("%.4f ", v);
            }
            System.out.println();
        }
    }
}
