package fuzzylogic.variables;

import java.util.LinkedHashMap;
import java.util.Map;

public class FuzzyVariable {
    private final String varName;
    private final double minValue; // min value for x
    private final double maxValue; // max value for x

    private final Map<String, FuzzySet> setsStored; // stores all fuzzy sets

    public FuzzyVariable(String varName, double minValue, double maxValue) {
        this.varName = varName;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setsStored = new LinkedHashMap<>();
    }

    public String getVarName() { return varName; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }

    public void addFuzzySet(FuzzySet set) { setsStored.put(set.getSetName(), set); }

    public FuzzySet getFuzzySet(String setName) {
        FuzzySet set = setsStored.get(setName);
        if (set == null) {
            // Return first set as default if the requested set does not exist
            if (!setsStored.isEmpty()) {
                return setsStored.values().iterator().next();
            } else {
                throw new IllegalArgumentException("No fuzzy sets available in variable " + varName);
            }
        }
        return set;
    }
    public Map<String, FuzzySet> getAllFuzzySets() {
        return setsStored;
    }

    public double getMembershipForASingleSet(String setName, double x) {
        if (x < minValue || x > maxValue) {
            return 0.0; // Return 0 for out-of-range input
        }
        FuzzySet set = getFuzzySet(setName);
        return set.evaluateMembership(x);
    }

    public Map<String, Double> fuzzify(double x) {
        Map<String, Double> memberships = new LinkedHashMap<>();
        if (x < minValue || x > maxValue) {
            // Input is out-of-range, return 0 for all sets
            for (String key : setsStored.keySet()) {
                memberships.put(key, 0.0);
            }
            return memberships;
        }
        for (Map.Entry<String, FuzzySet> entry : setsStored.entrySet()) {
            memberships.put(entry.getKey(), entry.getValue().evaluateMembership(x));
        }
        return memberships;
    }

    @Override
    public String toString() {
        return "FuzzyVariable [varName=" + varName + ", setsStored=" + setsStored + "]";
    }
}
