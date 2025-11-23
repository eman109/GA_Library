package fuzzylogic;

import fuzzylogic.operators.*;
import fuzzylogic.rules.FuzzyRule;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class FuzzyLogicApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(FuzzyLogicApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Running Fuzzy Logic Demo ===");

        // --- 1. Crisp inputs ---
        double temp = 25;     // Temperature in °C
        double weather = 80;  // Weather index (0-100)

        // --- 2. Fuzzification ---
        Map<String, Double> tempMF = new HashMap<>();
        tempMF.put("Low", triangular(temp, 0, 20, 40));
        tempMF.put("Medium", triangular(temp, 30, 50, 70));
        tempMF.put("High", triangular(temp, 60, 80, 100));

        Map<String, Double> weatherMF = new HashMap<>();
        weatherMF.put("Low", triangular(weather, 0, 20, 40));
        weatherMF.put("Medium", triangular(weather, 30, 50, 70));
        weatherMF.put("High", triangular(weather, 60, 80, 100));

        System.out.println("Temperature membership: " + tempMF);
        System.out.println("Weather membership: " + weatherMF);

        // --- 3. Output MF for GoOut ---
        Map<String, double[]> goOutMF = new HashMap<>();
        goOutMF.put("Low", new double[]{0, 0.3, 0.6, 0.3, 0});
        goOutMF.put("Medium", new double[]{0, 0.5, 1, 0.5, 0});
        goOutMF.put("High", new double[]{0.5, 0.7, 1, 1, 0.8});

        // --- 4. Define rules ---
        // Rule 1: IF Temp is Low AND Weather is High THEN GoOut is Medium
        Map<String, String> rule1Ant = Map.of("Temp", "Low", "Weather", "High");
        Map.Entry<String, String> rule1Cons = new AbstractMap.SimpleEntry<>("GoOut", "Medium");

        // Rule 2: IF Temp is High AND Weather is High THEN GoOut is High
        Map<String, String> rule2Ant = Map.of("Temp", "High", "Weather", "High");
        Map.Entry<String, String> rule2Cons = new AbstractMap.SimpleEntry<>("GoOut", "High");

        List<FuzzyRule> rules = new ArrayList<>();
        rules.add(new FuzzyRule(rule1Ant, rule1Cons));
        rules.add(new FuzzyRule(rule2Ant, rule2Cons));

        // --- 5. Evaluate rules ---
        List<double[]> clippedOutputs = new ArrayList<>();

        for (FuzzyRule rule : rules) {
            double firingStrength;

            // AND antecedent using MIN
            double tempVal = tempMF.get(rule.getAndAntecedents().get("Temp"));
            double weatherVal = weatherMF.get(rule.getAndAntecedents().get("Weather"));
            firingStrength = FuzzyOperators.and(tempVal, weatherVal, TNorm.MIN);

            System.out.println("Rule: " + rule);
            System.out.println("Firing strength: " + firingStrength);

            // Get output MF for this rule
            double[] outputMF = goOutMF.get(rule.getConsequent().getValue());

            // Clip output MF by firing strength
            double[] clipped = Implication.clipImplication(firingStrength, outputMF);
            System.out.println("Clipped output MF: " + Arrays.toString(clipped));
            clippedOutputs.add(clipped);
        }

        // --- 6. Aggregate rule outputs ---
        double[] finalAggregated = Aggregation.sumAggregate(clippedOutputs);
        System.out.println("Final aggregated GoOut MF: " + Arrays.toString(finalAggregated));
    }

    // --- Triangular membership function ---
    public static double triangular(double x, double a, double b, double c) {
        if (x <= a || x >= c) return 0;
        if (x == b) return 1;
        if (x < b) return (x - a) / (b - a);
        return (c - x) / (c - b);
    }
}
