package fuzzylogic;

import fuzzylogic.Defuzzification.CentroidDefuzzificationSimple;
import fuzzylogic.Defuzzification.MaxMembershipDefuzz;
import fuzzylogic.membership.*;
import fuzzylogic.variables.*;
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
        System.out.println("=== Running Fuzzy Logic Demo with Proper FuzzySets ===");

        // --- 1. Crisp inputs ---
        double tempInput = 45;
        double weatherInput = 60;

        // --- 2. Define input fuzzy variables ---
        FuzzyVariable temp = new FuzzyVariable("Temp", 0, 100);
        temp.addFuzzySet(new FuzzySet("Low", new TriangularMF(0, 0, 50)));
        temp.addFuzzySet(new FuzzySet("Medium", new TriangularMF(30, 50, 70)));
        temp.addFuzzySet(new FuzzySet("High", new TriangularMF(50, 100, 100)));

        FuzzyVariable weather = new FuzzyVariable("Weather", 0, 100);
        weather.addFuzzySet(new FuzzySet("Low", new TriangularMF(0, 0, 50)));
        weather.addFuzzySet(new FuzzySet("Medium", new TriangularMF(30, 50, 100)));
        weather.addFuzzySet(new FuzzySet("High", new TriangularMF(50, 100, 100)));

        // --- 3. Fuzzify inputs ---
        Map<String, Double> tempMF = temp.fuzzify(tempInput);
        Map<String, Double> weatherMF = weather.fuzzify(weatherInput);

        System.out.println("Temp MF: " + tempMF);
        System.out.println("Weather MF: " + weatherMF);

        // --- 4. Define output fuzzy sets ---
        FuzzySet goOutLow = new FuzzySet("Low", new TriangularMF(0, 0, 20));
        FuzzySet goOutMedium = new FuzzySet("Medium", new TriangularMF(10, 30, 50));
        FuzzySet goOutHigh = new FuzzySet("High", new TriangularMF(40, 50, 60));

        Map<String, FuzzySet> goOutSets = Map.of(
                "Low", goOutLow,
                "Medium", goOutMedium,
                "High", goOutHigh
        );

        // --- 5. Define rules ---
        List<FuzzyRule> rules = new ArrayList<>();
        rules.add(new FuzzyRule(Map.of("Temp", "Low", "Weather", "High"), Map.entry("GoOut", "Medium")));
        rules.add(new FuzzyRule(Map.of("Temp", "High", "Weather", "High"), Map.entry("GoOut", "High")));
        rules.add(new FuzzyRule(Map.of("Temp", "Medium", "Weather", "Medium"), Map.entry("GoOut", "Low")));

        // --- 6. Evaluate rules and clip outputs ---
        List<FuzzySet> clippedOutputs = new ArrayList<>();
        for (FuzzyRule rule : rules) {
            double tempVal = tempMF.get(rule.getAndAntecedents().get("Temp"));
            double weatherVal = weatherMF.get(rule.getAndAntecedents().get("Weather"));
            double firingStrength = FuzzyOperators.and(tempVal, weatherVal, TNorm.MIN);

            System.out.println("Rule: " + rule);
            System.out.println("Firing strength: " + firingStrength);

            FuzzySet outputSet = goOutSets.get(rule.getConsequent().getValue());
            FuzzySet clipped = Implication.clipImplication(outputSet, firingStrength);
            System.out.println("Clipped output set: " + clipped);
            clippedOutputs.add(clipped);
        }

        // --- 7. Aggregate clipped outputs ---
        FuzzySet aggregated = Aggregation.maxAggregate("GoOut", clippedOutputs);

        // --- 8. Show some example μ(x) for aggregated set ---
        System.out.println("Aggregated GoOut memberships:");
        for (double x = 0; x <= 60; x += 10) {
            System.out.printf("x=%.1f -> μ=%.2f%n", x, aggregated.evaluateMembership(x));
        }


        // --- Prepare lists for centroid defuzz ---
        List<Double> zValues = new ArrayList<>();
        List<Double> muValues = new ArrayList<>();

        for (double x = 0; x <= 60; x += 10) { // sample points
            zValues.add(x);
            muValues.add(aggregated.evaluateMembership(x));
        }

// --- Defuzzify ---
        CentroidDefuzzificationSimple defuzz = new CentroidDefuzzificationSimple();
        double crispGoOut = defuzz.defuzzify(zValues, muValues);

        System.out.println("Crisp GoOut value: " + crispGoOut);


        List<Double> kValues = new ArrayList<>();
        List<Double> mValues = new ArrayList<>();

        for (double x = 0; x <= 60; x += 10) { // sampled points
            kValues.add(x);
            mValues.add(aggregated.evaluateMembership(x));
        }

        MaxMembershipDefuzz maxDefuzz = new MaxMembershipDefuzz();
        double crispGoOu = maxDefuzz.defuzzify(kValues, mValues);

        System.out.println("Crisp GoOut (max membership) value: " + crispGoOu);


    }

}
