package fuzzylogic.operators;

import fuzzylogic.variables.FuzzySet;
import fuzzylogic.membership.MembershipFunction;

import java.util.List;

public class Aggregation {


    public static FuzzySet maxAggregate(String name, List<FuzzySet> clippedSets) {
        // Create a FuzzySet where evaluateMembership(x) returns the max μ(x) across all sets
        return new FuzzySet(name, new MembershipFunction() {
            @Override
            public double evaluate(double x) {
                double maxMu = 0.0;
                for (FuzzySet set : clippedSets) {
                    double mu = set.evaluateMembership(x);
                    if (mu > maxMu) maxMu = mu;
                }
                return maxMu;
            }

            @Override
            public String toString() {
                return "Aggregated MF of " + name;
            }
        });
    }
}
