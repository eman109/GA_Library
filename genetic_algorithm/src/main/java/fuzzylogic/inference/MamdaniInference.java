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

            double firing = Math.max(andValue, orValue) * rule.getWeight();
            if (firing <= 0) continue;

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
