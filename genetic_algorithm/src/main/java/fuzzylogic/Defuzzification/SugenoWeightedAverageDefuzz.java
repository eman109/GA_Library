package fuzzylogic.Defuzzification;

import java.util.List;

public class SugenoWeightedAverageDefuzz {

    /**
     * Defuzzifies a Sugeno-style output using weighted average.
     * @param ruleOutputs List of rule outputs (y values)
     * @param ruleWeights List of rule firing strengths
     * @return crisp output
     */
    public double defuzzify(List<Double> ruleOutputs, List<Double> ruleWeights) {
        if (ruleOutputs == null || ruleWeights == null ||
                ruleOutputs.isEmpty() || ruleWeights.isEmpty() ||
                ruleOutputs.size() != ruleWeights.size()) {
            return 0.0;
        }

        double weightedSum = 0.0;
        double sumOfWeights = 0.0;

        for (int i = 0; i < ruleOutputs.size(); i++) {
            double y = ruleOutputs.get(i);
            double w = ruleWeights.get(i);

            weightedSum += w * y;
            sumOfWeights += w;
        }

        if (sumOfWeights == 0) return 0.0;

        return weightedSum / sumOfWeights;
    }
}
