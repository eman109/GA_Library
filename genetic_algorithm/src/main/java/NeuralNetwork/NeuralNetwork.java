package NeuralNetwork;

import NeuralNetwork.loss.LossFunction;
import NeuralNetwork.optimizer.Optimizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeuralNetwork {

    private List<Layer> layers;
    private LossFunction lossFunction;
    private Optimizer optimizer;
    private List<Double> trainingLossHistory;
    private List<Double> validationLossHistory;

    public NeuralNetwork() {
        this.layers = new ArrayList<>();
        this.trainingLossHistory = new ArrayList<>();
        this.validationLossHistory = new ArrayList<>();
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
    }

    public void setLossFunction(LossFunction lossFunction) {
        this.lossFunction = lossFunction;
    }

    public void setOptimizer(Optimizer optimizer) {
        this.optimizer = optimizer;
    }

    public double[][] forward(double[][] input) {
        double[][] output = input;
        for (Layer layer : layers) {
            output = layer.forward(output);
        }
        return output;
    }

    public void backward(double[][] predicted, double[][] actual) {
        double[][] grad = new double[predicted.length][predicted[0].length];

        for (int i = 0; i < predicted.length; i++) {
            grad[i] = lossFunction.calcGrad(predicted[i], actual[i]);
        }

        for (int i = layers.size() - 1; i >= 0; i--) {
            grad = layers.get(i).backward(grad, optimizer);
        }
    }

    // Simple train without batch size (backward compatibility)
    public void train(double[][] X, double[][] y, int epochs) {
        train(X, y, epochs, X.length); // Use full batch
    }

    // Train with mini-batches
    public void train(double[][] X, double[][] y, int epochs, int batchSize) {
        int numSamples = X.length;
        trainingLossHistory.clear();

        for (int epoch = 0; epoch < epochs; epoch++) {
            int[] indices = shuffleIndices(numSamples);
            double totalLoss = 0.0;

            for (int i = 0; i < numSamples; i += batchSize) {
                int currentBatchSize = Math.min(batchSize, numSamples - i);

                double[][] batchX = new double[currentBatchSize][];
                double[][] batchY = new double[currentBatchSize][];

                for (int j = 0; j < currentBatchSize; j++) {
                    batchX[j] = X[indices[i + j]];
                    batchY[j] = y[indices[i + j]];
                }

                double[][] predictions = forward(batchX);
                backward(predictions, batchY);

                for (int j = 0; j < currentBatchSize; j++) {
                    totalLoss += lossFunction.calcLoss(predictions[j], batchY[j]);
                }
            }

            double avgLoss = totalLoss / numSamples;
            trainingLossHistory.add(avgLoss);

            if ((epoch + 1) % 50 == 0 || epoch == 0) {
                System.out.println("Epoch " + (epoch + 1) + "/" + epochs +
                        " - Loss: " + String.format("%.6f", avgLoss));
            }
        }
    }

    // Train with validation
    public void train(double[][] X, double[][] y, double[][] valX, double[][] valY,
                      int epochs, int batchSize) {
        int numSamples = X.length;
        trainingLossHistory.clear();
        validationLossHistory.clear();

        for (int epoch = 0; epoch < epochs; epoch++) {
            int[] indices = shuffleIndices(numSamples);
            double totalLoss = 0.0;

            for (int i = 0; i < numSamples; i += batchSize) {
                int currentBatchSize = Math.min(batchSize, numSamples - i);

                double[][] batchX = new double[currentBatchSize][];
                double[][] batchY = new double[currentBatchSize][];

                for (int j = 0; j < currentBatchSize; j++) {
                    batchX[j] = X[indices[i + j]];
                    batchY[j] = y[indices[i + j]];
                }

                double[][] predictions = forward(batchX);
                backward(predictions, batchY);

                for (int j = 0; j < currentBatchSize; j++) {
                    totalLoss += lossFunction.calcLoss(predictions[j], batchY[j]);
                }
            }

            double avgTrainLoss = totalLoss / numSamples;
            trainingLossHistory.add(avgTrainLoss);

            double[][] valPredictions = predict(valX);
            double valLoss = 0.0;
            for (int i = 0; i < valX.length; i++) {
                valLoss += lossFunction.calcLoss(valPredictions[i], valY[i]);
            }
            valLoss /= valX.length;
            validationLossHistory.add(valLoss);

            if ((epoch + 1) % 50 == 0 || epoch == 0) {
                System.out.println("Epoch " + (epoch + 1) + "/" + epochs +
                        " - Train Loss: " + String.format("%.6f", avgTrainLoss) +
                        " - Val Loss: " + String.format("%.6f", valLoss));
            }
        }
    }

    public double[][] predict(double[][] X) {
        return forward(X);
    }

    public double evaluate(double[][] X, double[][] y) {
        double[][] predictions = predict(X);
        double totalLoss = 0.0;

        for (int i = 0; i < X.length; i++) {
            totalLoss += lossFunction.calcLoss(predictions[i], y[i]);
        }

        return totalLoss / X.length;
    }

    public double accuracy(double[][] X, double[][] y) {
        double[][] predictions = predict(X);
        int correct = 0;

        for (int i = 0; i < X.length; i++) {
            int predClass = argmax(predictions[i]);
            int trueClass = argmax(y[i]);
            if (predClass == trueClass) {
                correct++;
            }
        }

        return (double) correct / X.length;
    }

    private int[] shuffleIndices(int size) {
        int[] indices = new int[size];
        for (int i = 0; i < size; i++) {
            indices[i] = i;
        }

        Random rand = new Random();
        for (int i = size - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = indices[i];
            indices[i] = indices[j];
            indices[j] = temp;
        }

        return indices;
    }

    private int argmax(double[] arr) {
        int maxIdx = 0;
        double maxVal = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > maxVal) {
                maxVal = arr[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }

    public List<Double> getTrainingLossHistory() {
        return trainingLossHistory;
    }

    public List<Double> getValidationLossHistory() {
        return validationLossHistory;
    }

    public LossFunction getLossFunction() {
        return lossFunction;
    }
}