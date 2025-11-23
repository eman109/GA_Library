package fuzzylogic.operators;

import java.util.List;

public class Aggregation {

    public static double[] sumAggregate(List<double[]> clippedOutputs) {
        if (clippedOutputs.isEmpty()) return new double[0];

        int length = clippedOutputs.get(0).length;
        double[] aggregated = new double[length];

        for (double[] mf : clippedOutputs) {
            for (int i = 0; i < length; i++) {
                aggregated[i] += mf[i];
                // Optional: cap at 1.0
                if (aggregated[i] > 1.0) aggregated[i] = 1.0;
            }
        }
        return aggregated;
    }
}
