package fuzzylogic;

import fuzzylogic.Defuzzification.CentroidDefuzzificationSimple;
import fuzzylogic.Defuzzification.MaxMembershipDefuzz;
import fuzzylogic.inference.MamdaniInference;
import fuzzylogic.inference.SugenoInference;
import fuzzylogic.membership.TriangularMF;
import fuzzylogic.variables.FuzzySet;
import fuzzylogic.variables.FuzzyVariable;
import fuzzylogic.operators.*;
import fuzzylogic.rules.FuzzyRule;
import fuzzylogic.rules.RuleManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class FuzzyLogicApplication {

    @Autowired
    private RuleManager ruleManager;

    public static void main(String[] args) {
        SpringApplication.run(FuzzyLogicApplication.class, args);
    }

    // ------------------------ API Endpoint ------------------------
    @GetMapping("/run")
    public Map<String, Object> runInferenceApi(@RequestParam double temp, @RequestParam double heartRate) {
        return runInference(temp, heartRate);
    }


    // ------------------------ Inference Logic ------------------------
    public Map<String, Object> runInference(double tempInput, double heartRateInput) {
        Map<String, Object> result = new HashMap<>();

        System.out.println("\n===============================");
        System.out.println(" FUZZY LOGIC: MEDICAL DIAGNOSIS ");
        System.out.println("===============================\n");

        System.out.println("Inputs: Temperature=" + tempInput + "°C, Heart Rate=" + heartRateInput + " bpm");

        // 1️⃣ Input Variables
        FuzzyVariable temperature = new FuzzyVariable("Temperature", 35, 42);
        temperature.addFuzzySet(new FuzzySet("Low", new TriangularMF(35, 35, 37)));
        temperature.addFuzzySet(new FuzzySet("Normal", new TriangularMF(36, 37, 38.5)));
        temperature.addFuzzySet(new FuzzySet("High", new TriangularMF(38, 42, 42)));

        FuzzyVariable heartRate = new FuzzyVariable("HeartRate", 50, 150);
        heartRate.addFuzzySet(new FuzzySet("Slow", new TriangularMF(50, 50, 70)));
        heartRate.addFuzzySet(new FuzzySet("Normal", new TriangularMF(60, 80, 100)));
        heartRate.addFuzzySet(new FuzzySet("Fast", new TriangularMF(90, 120, 150)));

        Map<String, Map<String, Double>> fuzzifiedInputs = Map.of(
                "Temperature", temperature.fuzzify(tempInput),
                "HeartRate", heartRate.fuzzify(heartRateInput)
        );

        System.out.println("\nFuzzified Inputs:");
        System.out.println(fuzzifiedInputs);
        result.put("fuzzifiedInputs", fuzzifiedInputs);

        // 2️⃣ Output Variables
        Map<String, FuzzySet> severitySets = new HashMap<>();
        severitySets.put("Mild", new FuzzySet("Mild", new TriangularMF(0, 0, 5)));
        severitySets.put("Moderate", new FuzzySet("Moderate", new TriangularMF(2, 5, 7)));
        severitySets.put("Severe", new FuzzySet("Severe", new TriangularMF(5, 10, 10)));

        // 3️⃣ Fetch rules dynamically
        List<FuzzyRule> allRules = ruleManager.getRules();
        List<FuzzyRule> mamdaniRules = new ArrayList<>();
        List<FuzzyRule> sugenoRules = new ArrayList<>();

        for (FuzzyRule rule : allRules) {
            if (!rule.isEnabled()) continue;

            String consequentValue = rule.getConsequent().getValue();
            if (isNumeric(consequentValue)) {
                sugenoRules.add(rule);
            } else {
                mamdaniRules.add(rule);
            }
        }

        // 4️⃣ Mamdani inference
        if (!mamdaniRules.isEmpty()) {
            List<FuzzySet> clippedOutputs = MamdaniInference.infer(
                    mamdaniRules,
                    fuzzifiedInputs,
                    severitySets,
                    TNorm.MIN,
                    SNorm.MAX
            );
            System.out.println("\n--- Clipped Outputs per Rule ---");
            for (FuzzySet clipped : clippedOutputs) {
                System.out.println("Rule consequent: " + clipped.getSetName());
                for (double x = 0; x <= 10; x += 0.5) {
                    double mu = clipped.evaluateMembership(x);
                    System.out.printf("x=%.1f -> μ=%.3f%n", x, mu);
                }
                System.out.println();
            }

            FuzzySet aggregated = Aggregation.maxAggregate("IllnessSeverity", clippedOutputs);
            System.out.println("\n--- Aggregated Output ---");
            List<Double> zVals = new ArrayList<>();
            List<Double> muVals = new ArrayList<>();
            for (double x = 0; x <= 10; x += 0.5) {
                double mu = aggregated.evaluateMembership(x);
                zVals.add(x);
                muVals.add(mu);
                System.out.printf("x=%.1f -> μ=%.3f%n", x, mu);
            }


            double centroid = new CentroidDefuzzificationSimple().defuzzify(zVals, muVals);
            double maxMembership = new MaxMembershipDefuzz().defuzzify(zVals, muVals);

            System.out.println("\nMamdani Centroid Defuzzified Output = " + centroid);
            System.out.println("Mamdani Max-Membership Output       = " + maxMembership);

            result.put("mamdaniCentroid", centroid);
            result.put("mamdaniMaxMembership", maxMembership);
        } else {
            System.out.println("\nNo Mamdani rules to fire.");
        }

        // 5️⃣ Sugeno inference
        if (!sugenoRules.isEmpty()) {
            double sugenoCrisp = SugenoInference.infer(
                    sugenoRules,
                    fuzzifiedInputs,
                    TNorm.MIN,
                    SNorm.MAX
            );
            System.out.println("Sugeno Crisp Output = " + sugenoCrisp);
            result.put("sugenoCrisp", sugenoCrisp);
        } else {
            System.out.println("No Sugeno rules to fire.");
        }

        System.out.println("\nDone.");
        return result;
    }

    // ------------------------ Helper ------------------------
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
