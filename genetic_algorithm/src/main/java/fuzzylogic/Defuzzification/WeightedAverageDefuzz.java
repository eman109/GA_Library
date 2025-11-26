package fuzzylogic.Defuzzification;

import java.util.List;


public class WeightedAverageDefuzz {


    public double defuzzify(List<Double> firingStrengths, List<Double> centroids) {
        if (firingStrengths.size() != centroids.size()) {
            throw new IllegalArgumentException("Firing strengths and centroids must have same size");
        }

        double numerator = 0.0;
        double denominator = 0.0;

        for (int i = 0; i < centroids.size(); i++) {
            numerator += centroids.get(i) * firingStrengths.get(i);
            denominator += firingStrengths.get(i);
        }

        if (denominator == 0.0) return 0.0;

        return numerator / denominator;
    }

    /**
     * Compute centroid of a triangle given its three points (a, b, c)
     */
    public static double computeCentroid(List<Double> points) {
        // Triangle points: (a, b, c)
        double sum = 0.0;
        for (double x : points) sum += x;
        return sum / points.size();
    }
}
