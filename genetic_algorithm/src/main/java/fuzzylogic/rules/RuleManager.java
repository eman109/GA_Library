package fuzzylogic.rules;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RuleManager {

    private final List<FuzzyRule> rules = new ArrayList<>();

    public List<FuzzyRule> getRules() {
        return rules;
    }

    public void addRule(FuzzyRule rule) {
        rules.add(rule);
    }

    public void removeRule(FuzzyRule rule) {
        rules.remove(rule);
    }

    public void enableRule(FuzzyRule rule) {
        rule.setEnabled(true);
    }

    public void disableRule(FuzzyRule rule) {
        rule.setEnabled(false);
    }

    public void setWeight(FuzzyRule rule, double weight) {
        rule.setWeight(weight);
    }
}
