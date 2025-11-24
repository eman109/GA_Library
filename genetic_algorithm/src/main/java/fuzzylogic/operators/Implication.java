package fuzzylogic.operators;

import fuzzylogic.membership.*;
import fuzzylogic.variables.FuzzySet;

public class Implication {

    /**
     * Clip a FuzzySet (Mamdani implication)
     * Works for TriangularMF, TrapezoidalMF, GaussianMF
     */
    public static FuzzySet clipImplication(FuzzySet set, double firingStrength) {
        MembershipFunction originalMF = set.getMembershipF();

        // If firingStrength is 0, return a zero membership function
        if (firingStrength <= 0) {
            return new FuzzySet(set.getSetName(), x -> 0.0);
        }

        // If firingStrength >= 1, no clipping needed
        if (firingStrength >= 1) {
            return set;
        }

        // Wrap the original MF with vertical clipping
        MembershipFunction clippedMF = new MembershipFunction() {
            @Override
            public double evaluate(double x) {
                return Math.min(originalMF.evaluate(x), firingStrength);
            }

            @Override
            public String toString() {
                return "ClippedMF(" + set.getSetName() + ", alpha=" + firingStrength + ")";
            }
        };

        return new FuzzySet(set.getSetName(), clippedMF);
    }
}
