package fuzzylogic.variables;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//represent variables like fever, temperature
//Can contain multiple fuzzy sets
public class FuzzyVariable {
    private final String varName;
    private final double minValue; //min value for x
    private final double maxValue; // max value for x

    private final Map<String, FuzzySet> setsStored; //stores all fuzzy sets of the variable key -> the name of set value -> Fuzzy set

    public FuzzyVariable(String varName, double minValue, double maxValue) {
        this.varName = varName;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setsStored = new LinkedHashMap<>();
    }

    public String getVarName() { //getter to get the variable name
        return varName;
    }

    public double getMinValue() { //getter to get the variable min range
        return minValue;
    }

    public double getMaxValue() { //getter to get the variable max range
        return maxValue;
    }


    public void addFuzzySet(FuzzySet set) { //add a set to the variable
        setsStored.put(set.getSetName(),set);
    }

    public FuzzySet getFuzzySet(String setName) { //gets a single set
        return setsStored.get(setName);
    }

    public Map<String, FuzzySet> getAllFuzzySets() { //gets all sets
        return setsStored;
    }

    public double getMembershipForASingleSet(String setName , double x) { //Computes μ(x) for a specific set
        FuzzySet set = setsStored.get(setName);
        if (set == null) {
            throw new IllegalArgumentException("Set " + setName + " not found"); //Throws an error if the set does not exist
        }
        return set.evaluateMembership(x);
    }

    public Map<String, Double> fuzzify(double x){ //compute all memberships at once
        Map<String, Double> finalFuzzySets = new LinkedHashMap<>();
        for (Map.Entry<String, FuzzySet> entry : setsStored.entrySet()) {
            finalFuzzySets.put(entry.getKey(), entry.getValue().evaluateMembership(x));
        }
        return finalFuzzySets;
    }

    @Override
    public String toString() {
        return "FuzzyVariable [varName=" + varName + ", setsStored=" + setsStored + "]";
    }
}
