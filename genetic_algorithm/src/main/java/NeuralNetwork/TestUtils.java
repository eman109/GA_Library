package NeuralNetwork;

public class TestUtils {

    public static void main(String[] args) {

        System.out.println("=== Testing Utils ===\n");

        // Test 1: Train-Test Split
        System.out.println("Test 1: Train-Test Split");
        double[][] data = {
                {1, 2}, {3, 4}, {5, 6}, {7, 8}, {9, 10}
        };

        double[][][] split = Utils.trainTestSplit(data, 0.8);
        double[][] train = split[0];
        double[][] test = split[1];

        System.out.println("Train set (" + train.length + " samples):");
        printData(train);
        System.out.println("Test set (" + test.length + " samples):");
        printData(test);
        System.out.println();

        // Test 2: Normalization
        System.out.println("Test 2: Normalization");
        double[][] rawData = {
                {10, 200},
                {20, 400},
                {30, 600}
        };

        System.out.println("Before normalization:");
        printData(rawData);

        double[][] normalized = Utils.normalize(rawData);
        System.out.println("After normalization:");
        printData(normalized);
        System.out.println();

        // Test 3: Handle Missing Values
        System.out.println("Test 3: Handle Missing Values");
        double[][] dataWithNaN = {
                {1, Double.NaN, 3},
                {4, 5, Double.NaN},
                {7, 8, 9}
        };

        System.out.println("Before handling missing values:");
        printData(dataWithNaN);

        double[][] filled = Utils.handleMissingValues(dataWithNaN);
        System.out.println("After handling missing values:");
        printData(filled);
        System.out.println();

        // Test 4: Shuffle
        System.out.println("Test 4: Shuffle");
        double[][] X = {{1, 2}, {3, 4}, {5, 6}};
        double[][] Y = {{10, 20}, {30, 40}, {50, 60}};

        System.out.println("Before shuffle:");
        System.out.println("X:");
        printData(X);
        System.out.println("Y:");
        printData(Y);

        Utils.shuffle(X, Y);

        System.out.println("After shuffle:");
        System.out.println("X:");
        printData(X);
        System.out.println("Y:");
        printData(Y);

        System.out.println("\nAll Utils tests completed!");
    }

    private static void printData(double[][] data) {
        for (double[] row : data) {
            System.out.print("  [");
            for (int i = 0; i < row.length; i++) {
                System.out.printf("%.2f", row[i]);
                if (i < row.length - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }
}