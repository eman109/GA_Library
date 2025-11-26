package fuzzylogic.system;

import fuzzylogic.Defuzzification.CentroidDefuzzificationSimple;
import fuzzylogic.inference.MamdaniInference;
import fuzzylogic.rules.RuleManager;
import fuzzylogic.variables.FuzzySet;
import fuzzylogic.variables.FuzzyVariable;
import fuzzylogic.membership.MembershipFunction;
import fuzzylogic.operators.TNorm;
import fuzzylogic.operators.SNorm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
@Component
public class FuzzySystem {

    @Autowired
    private RuleManager ruleManager;

    // Using their class directly — no autowire issues
    private final CentroidDefuzzificationSimple centroid = new CentroidDefuzzificationSimple();

    private final Map<String, FuzzyVariable> inputs = new HashMap<>();
    private final Map<String, FuzzyVariable> outputs = new HashMap<>();

    public void addInputVariable(FuzzyVariable var) {
        inputs.put(var.getVarName(), var);
    }

    public void addOutputVariable(FuzzyVariable var) {
        outputs.put(var.getVarName(), var);
    }

    public double evaluate(Map<String, Double> crispInputs, String outputVarName) {

        // 1. Fuzzification
        Map<String, Map<String, Double>> fuzzified = new HashMap<>();
        crispInputs.forEach((name, value) -> {
            FuzzyVariable v = inputs.get(name);
            if (v != null) fuzzified.put(name, v.fuzzify(value));
        });

        // 2. Output sets
        FuzzyVariable outVar = outputs.get(outputVarName);
        if (outVar == null) throw new IllegalArgumentException("Output not found: " + outputVarName);

        Map<String, FuzzySet> outputSets = new HashMap<>();
        outVar.getAllFuzzySets().forEach((label, set) -> outputSets.put(label, set));

        // 3. Inference
        List<FuzzySet> clipped = MamdaniInference.infer(
                ruleManager.getRules(),
                fuzzified,
                outputSets,
                TNorm.MIN,
                SNorm.MAX
        );

        // 4. Aggregation
        MembershipFunction aggregatedMF = x -> clipped.stream()
                .mapToDouble(fs -> fs.evaluateMembership(x))
                .max().orElse(0.0);

        FuzzySet aggregated = new FuzzySet("Aggregated", aggregatedMF);

        // 5. Defuzzification using their class
        List<Double> z = new ArrayList<>();
        List<Double> mu = new ArrayList<>();
        for (double x = 0; x <= 100; x += 0.1) {
            z.add(x);
            mu.add(aggregated.evaluateMembership(x));
        }

        return centroid.defuzzify(z, mu);
    }
}

