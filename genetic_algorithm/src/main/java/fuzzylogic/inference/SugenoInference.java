package fuzzylogic.inference;

import fuzzylogic.rules.FuzzyRule;
import fuzzylogic.operators.TNorm;
import fuzzylogic.operators.SNorm;
import fuzzylogic.operators.FuzzyOperators;

import java.util.List;
import java.util.Map;

public class SugenoInference {

    public static double infer(
            List<FuzzyRule> rules,
            Map<String, Map<String, Double>> fuzzifiedInputs,
            TNorm andOp,
            SNorm orOp) {

        double num = 0.0;
        double den = 0.0;

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

            //zero o/p
            double y = Double.parseDouble(rule.getConsequent().getValue());

            num += firing * y;
            den += firing;
        }

        return (den == 0) ? 0.0 : num / den;
    }

    private static double getMu(Map<String, Map<String, Double>> inputs, String var, String set) {
        if (inputs == null) return 0;
        var m = inputs.get(var);
        if (m == null) return 0;
        return m.getOrDefault(set, 0.0);
    }
}
