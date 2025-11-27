package fuzzylogic.inference;

import fuzzylogic.rules.FuzzyRule;
import fuzzylogic.operators.TNorm;
import fuzzylogic.operators.SNorm;
import fuzzylogic.operators.FuzzyOperators;
import fuzzylogic.Defuzzification.SugenoWeightedAverageDefuzz;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class SugenoInference {


    public static double infer(
            List<FuzzyRule> rules,
            Map<String, Map<String, Double>> fuzzifiedInputs,
            TNorm andOp,
            SNorm orOp) {

        // Lists to store rule outputs and their firing strengths
        List<Double> outputs = new ArrayList<>();
        List<Double> weights = new ArrayList<>();

        for (FuzzyRule rule : rules) {
            if (!rule.isEnabled()) continue;

            // Compute AND and OR antecedent membership values
            double andValue = 1.0;
            double orValue = 0.0;

            if (rule.getAndAntecedents() != null) {
                for (var a : rule.getAndAntecedents().entrySet()) {
                    double mu = getMu(fuzzifiedInputs, a.getKey(), a.getValue());
                    andValue = FuzzyOperators.and(andValue, mu, andOp);
                }
            }

            if (rule.getOrAntecedents() != null) {
                for (var a : rule.getOrAntecedents().entrySet()) {
                    double mu = getMu(fuzzifiedInputs, a.getKey(), a.getValue());
                    orValue = FuzzyOperators.or(orValue, mu, orOp);
                }
            }

            // Compute firing strength (weight) of the rule
            boolean hasAnd = rule.getAndAntecedents() != null && !rule.getAndAntecedents().isEmpty();
            boolean hasOr  = rule.getOrAntecedents()  != null && !rule.getOrAntecedents().isEmpty();

            double firing;
            if (hasAnd && !hasOr) {
                firing = andValue;
            } else if (!hasAnd && hasOr) {
                firing = orValue;
            } else {
                // Mixed AND + OR
                String mid = rule.getMiddleOperator();
                if (mid != null && mid.equalsIgnoreCase("AND")) {
                    firing = Math.min(andValue, orValue);
                } else {
                    firing = Math.max(andValue, orValue);
                }
            }

            firing *= rule.getWeight();

            if (firing <= 0) continue;

            // Get the Sugeno output (usually constant)
            double y;
            try {
                y = Double.parseDouble(rule.getConsequent().getValue());
            } catch (NumberFormatException e) {
                y = 0.0; // fallback if parsing fails
            }

            outputs.add(y);
            weights.add(firing);
        }

        // Use the Sugeno weighted average defuzzifier
        SugenoWeightedAverageDefuzz defuzz = new SugenoWeightedAverageDefuzz();
        return defuzz.defuzzify(outputs, weights);
    }

    /**
     * Helper method to get membership value for a given input variable and fuzzy set.
     */
    private static double getMu(Map<String, Map<String, Double>> inputs, String var, String set) {
        if (inputs == null) return 0.0;
        var m = inputs.get(var);
        if (m == null) return 0.0;
        return m.getOrDefault(set, 0.0);
    }
}
