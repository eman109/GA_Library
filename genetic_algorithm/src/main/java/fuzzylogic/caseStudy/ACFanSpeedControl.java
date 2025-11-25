package fuzzylogic.caseStudy;

import fuzzylogic.membership.TriangularMF;
import fuzzylogic.rules.FuzzyRule;
import fuzzylogic.rules.RuleManager;
import fuzzylogic.system.FuzzySystem;
import fuzzylogic.variables.FuzzySet;
import fuzzylogic.variables.FuzzyVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

@Component
public class ACFanSpeedControl implements CommandLineRunner {

    @Autowired private FuzzySystem fuzzySystem;
    @Autowired private RuleManager ruleManager;

    @Override
    public void run(String... args) {
        defineVariables();
        defineRulesUsingRealFuzzyRuleClass();
        demo();
    }

    private void defineVariables() {
        // Temperature
        FuzzyVariable temp = new FuzzyVariable("Temperature", 0, 50);
        temp.addFuzzySet(new FuzzySet("Cold", new TriangularMF(0, 0, 25)));
        temp.addFuzzySet(new FuzzySet("Warm", new TriangularMF(15, 25, 35)));
        temp.addFuzzySet(new FuzzySet("Hot", new TriangularMF(30, 50, 50)));
        fuzzySystem.addInputVariable(temp);

        // Humidity
        FuzzyVariable hum = new FuzzyVariable("Humidity", 0, 100);
        hum.addFuzzySet(new FuzzySet("Dry", new TriangularMF(0, 0, 50)));
        hum.addFuzzySet(new FuzzySet("Normal", new TriangularMF(30, 60, 80)));
        hum.addFuzzySet(new FuzzySet("Humid", new TriangularMF(70, 100, 100)));
        fuzzySystem.addInputVariable(hum);

        // FanSpeed
        FuzzyVariable fan = new FuzzyVariable("FanSpeed", 0, 100);
        fan.addFuzzySet(new FuzzySet("VerySlow", new TriangularMF(0, 0, 25)));
        fan.addFuzzySet(new FuzzySet("Slow", new TriangularMF(15, 30, 45)));
        fan.addFuzzySet(new FuzzySet("Medium", new TriangularMF(35, 50, 65)));
        fan.addFuzzySet(new FuzzySet("Fast", new TriangularMF(55, 70, 85)));
        fan.addFuzzySet(new FuzzySet("VeryFast", new TriangularMF(75, 100, 100)));
        fuzzySystem.addOutputVariable(fan);
    }

    private void defineRulesUsingRealFuzzyRuleClass() {
        // This matches your exact FuzzyRule constructor and fields
        ruleManager.addRule(new FuzzyRule(
                Map.of("Temperature", "Cold"),
                new SimpleEntry<>("FanSpeed", "VerySlow")
        ));

        ruleManager.addRule(new FuzzyRule(
                Map.of("Temperature", "Warm", "Humidity", "Dry"),
                new SimpleEntry<>("FanSpeed", "Slow")
        ));

        ruleManager.addRule(new FuzzyRule(
                Map.of("Temperature", "Warm", "Humidity", "Normal"),
                new SimpleEntry<>("FanSpeed", "Medium")
        ));

        ruleManager.addRule(new FuzzyRule(
                Map.of("Temperature", "Warm", "Humidity", "Humid"),
                new SimpleEntry<>("FanSpeed", "Fast")
        ));

        ruleManager.addRule(new FuzzyRule(
                Map.of("Temperature", "Hot"),
                new SimpleEntry<>("FanSpeed", "VeryFast")
        ));
    }

    private void demo() {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("   AC FAN SPEED CONTROL");
        System.out.println("════════════════════════════════════════\n");

        double[][] tests = {{12, 30}, {25, 45}, {26, 70}, {29, 90}, {42, 80}};

        for (double[] t : tests) {
            double speed = fuzzySystem.evaluate(
                    Map.of("Temperature", t[0], "Humidity", t[1]), "FanSpeed"
            );
            System.out.printf("Temp: %.1f°C | Humidity: %.0f%% → Fan Speed: %.1f%%\n", t[0], t[1], speed);
        }
    }
}