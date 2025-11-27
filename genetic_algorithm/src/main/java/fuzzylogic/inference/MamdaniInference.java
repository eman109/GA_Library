package fuzzylogic.inference;

import fuzzylogic.rules.FuzzyRule;
import fuzzylogic.operators.TNorm;
import fuzzylogic.operators.SNorm;
import fuzzylogic.operators.FuzzyOperators;
import fuzzylogic.operators.Implication;
import fuzzylogic.variables.FuzzySet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MamdaniInference {

    public static List<FuzzySet> infer(
            List<FuzzyRule> rules,
            Map<String, Map<String, Double>> fuzzifiedInputs,
            Map<String, FuzzySet> outputSets,
            TNorm andOp,
            SNorm orOp) {

        List<FuzzySet> result = new ArrayList<>();

        for (FuzzyRule rule : rules) {
            if (!rule.isEnabled()) continue;

            double andValue = 1.0; // identity for min
            double orValue  = 0.0; // identity for max

            // Compute AND antecedents
            if (rule.getAndAntecedents() != null && !rule.getAndAntecedents().isEmpty()) {
                for (var a : rule.getAndAntecedents().entrySet()) {
                    double mu = getMu(fuzzifiedInputs, a.getKey(), a.getValue());
                    andValue = FuzzyOperators.and(andValue, mu, andOp);
                }
            }

            // Compute OR antecedents
            if (rule.getOrAntecedents() != null && !rule.getOrAntecedents().isEmpty()) {
                for (var a : rule.getOrAntecedents().entrySet()) {
                    double mu = getMu(fuzzifiedInputs, a.getKey(), a.getValue());
                    orValue = FuzzyOperators.or(orValue, mu, orOp);
                }
            }

            // Determine firing strength
            double firing;
            boolean hasAnd = rule.getAndAntecedents() != null && !rule.getAndAntecedents().isEmpty();
            boolean hasOr  = rule.getOrAntecedents()  != null && !rule.getOrAntecedents().isEmpty();

            if (hasAnd && !hasOr) {
                // AND-only rule
                firing = andValue;
            } else if (!hasAnd && hasOr) {
                // OR-only rule
                firing = orValue;
            } else {
                // Mixed AND + OR
                String mid = rule.getMiddleOperator();
                if (mid.equalsIgnoreCase("AND")) {
                    firing = Math.min(andValue, orValue);
                } else {
                    // default OR
                    firing = Math.max(andValue, orValue);
                }
            }

            // Apply rule weight
            firing *= rule.getWeight();

            if (firing <= 0) continue;

            // Apply implication to the consequent
            var cons = rule.getConsequent();
            FuzzySet outSet = outputSets.get(cons.getValue());
            if (outSet == null) continue;

            result.add(Implication.clipImplication(outSet, firing));
        }

        return result;
    }

    private static double getMu(Map<String, Map<String, Double>> inputs, String var, String set) {
        if (inputs == null) return 0;
        var m = inputs.get(var);
        if (m == null) return 0;
        return m.getOrDefault(set, 0.0);
    }
}
