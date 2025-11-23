package fuzzylogic.rules;

import fuzzylogic.operators.TNorm;
import fuzzylogic.operators.SNorm;

import java.util.Map;

public class FuzzyRule {


    private Map<String, String> andAntecedents;


    private Map<String, String> orAntecedents;

    private Map.Entry<String, String> consequent;


    private TNorm andOperator = TNorm.MIN;
    private SNorm orOperator = SNorm.MAX;

    private double weight = 1.0;
    private boolean enabled = true;

    public FuzzyRule() {} // For JSON

    public FuzzyRule(Map<String, String> andAntecedents,
                     Map.Entry<String, String> consequent) {
        this.andAntecedents = andAntecedents;
        this.consequent = consequent;
    }

    // --- Getters & Setters ---

    public Map<String, String> getAndAntecedents() { return andAntecedents; }
    public void setAndAntecedents(Map<String, String> andAntecedents) { this.andAntecedents = andAntecedents; }

    public Map<String, String> getOrAntecedents() { return orAntecedents; }
    public void setOrAntecedents(Map<String, String> orAntecedents) { this.orAntecedents = orAntecedents; }

    public Map.Entry<String, String> getConsequent() { return consequent; }
    public void setConsequent(Map.Entry<String, String> consequent) { this.consequent = consequent; }

    public TNorm getAndOperator() { return andOperator; }
    public void setAndOperator(TNorm andOperator) { this.andOperator = andOperator; }

    public SNorm getOrOperator() { return orOperator; }
    public void setOrOperator(SNorm orOperator) { this.orOperator = orOperator; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public String toString() {
        return "IF AND=" + andAntecedents +
                " OR=" + orAntecedents +
                " THEN " + consequent.getKey() + " IS " + consequent.getValue() +
                " [AND=" + andOperator + ", OR=" + orOperator + ", weight=" + weight + "]";
    }
}
