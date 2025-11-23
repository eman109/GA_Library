package fuzzylogic.operators;


public class FuzzyOperators {

    // AND operator (t-norm)
    public static double and(double a, double b, TNorm tNorm) {
        switch (tNorm) {
            case MIN: return Math.min(a, b);
            case PRODUCT: return a * b;
            default: return Math.min(a, b);
        }
    }

    // OR operator (s-norm)
    public static double or(double a, double b, SNorm sNorm) {
        switch (sNorm) {
            case MAX: return Math.max(a, b);
            case SUM: return Math.min(1.0, a + b); // bounded sum
            default: return Math.max(a, b);
        }
    }
}

