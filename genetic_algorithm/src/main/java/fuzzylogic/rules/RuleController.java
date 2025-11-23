package fuzzylogic.rules;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class RuleController {

    @Autowired
    private RuleManager ruleManager;

    // Get all rules
    @GetMapping
    public List<FuzzyRule> getAllRules() {
        return ruleManager.getRules();
    }

    // Add a new rule
    @PostMapping
    public String addRule(@RequestBody FuzzyRule rule) {
        ruleManager.addRule(rule);
        return "Rule added!";
    }

    // Remove a rule (by index for simplicity)
    @DeleteMapping("/{index}")
    public String removeRule(@PathVariable int index) {
        List<FuzzyRule> rules = ruleManager.getRules();
        if (index >= 0 && index < rules.size()) {
            ruleManager.removeRule(rules.get(index));
            return "Rule removed!";
        }
        return "Invalid index";
    }

    // Enable a rule
    @PostMapping("/{index}/enable")
    public String enableRule(@PathVariable int index) {
        List<FuzzyRule> rules = ruleManager.getRules();
        if (index >= 0 && index < rules.size()) {
            ruleManager.enableRule(rules.get(index));
            return "Rule enabled!";
        }
        return "Invalid index";
    }

    // Disable a rule
    @PostMapping("/{index}/disable")
    public String disableRule(@PathVariable int index) {
        List<FuzzyRule> rules = ruleManager.getRules();
        if (index >= 0 && index < rules.size()) {
            ruleManager.disableRule(rules.get(index));
            return "Rule disabled!";
        }
        return "Invalid index";
    }

    // Set rule weight
    @PostMapping("/{index}/weight/{weight}")
    public String setWeight(@PathVariable int index, @PathVariable double weight) {
        List<FuzzyRule> rules = ruleManager.getRules();
        if (index >= 0 && index < rules.size()) {
            ruleManager.setWeight(rules.get(index), weight);
            return "Weight updated!";
        }
        return "Invalid index";
    }
}
