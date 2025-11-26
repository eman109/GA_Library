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

    // ------------------------ NORMAL ENDPOINT (unchanged) ------------------------
    @GetMapping("/run")
    public Map<String, Object> runInferenceApi(@RequestParam double temp, @RequestParam double heartRate) {
        return runInference(temp, heartRate);
    }


    // ------------------------ ORIGINAL INFERENCE (UNCHANGED) ------------------------
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

        // 3️⃣ Rules
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
                    TNorm.MIN,   // normal → MIN
                    SNorm.MAX    // normal → MAX
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
        }

        // 5️⃣ Sugeno inference
        if (!sugenoRules.isEmpty()) {
            double sugenoCrisp = SugenoInference.infer(
                    sugenoRules,
                    fuzzifiedInputs,
                    TNorm.MIN,   // normal → MIN
                    SNorm.MAX    // normal → MAX
            );
            System.out.println("Sugeno Crisp Output = " + sugenoCrisp);
            result.put("sugenoCrisp", sugenoCrisp);
        }

        System.out.println("\nDone.");
        return result;
    }


    // ------------------------ ADVANCED INFERENCE (new, uses PRODUCT/PROBOR) ------------------------
    private Map<String, Object> runInferenceAdvanced(
            double tempInput,
            double heartRateInput,
            TNorm tNorm,
            SNorm sNorm
    ) {
        Map<String, Object> result = new HashMap<>();

        System.out.println("\n===============================");
        System.out.println(" ADVANCED FUZZY LOGIC INFERENCE ");
        System.out.println("===============================\n");

        System.out.println("Using T-Norm = " + tNorm);
        System.out.println("Using S-Norm = " + sNorm);

        // Reuse same logic EXACTLY as original but swap MIN/MAX → tNorm/sNorm
        // ------------------------------------------

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

        Map<String, FuzzySet> severitySets = new HashMap<>();
        severitySets.put("Mild", new FuzzySet("Mild", new TriangularMF(0, 0, 5)));
        severitySets.put("Moderate", new FuzzySet("Moderate", new TriangularMF(2, 5, 7)));
        severitySets.put("Severe", new FuzzySet("Severe", new TriangularMF(5, 10, 10)));

        List<FuzzyRule> allRules = ruleManager.getRules();
        List<FuzzyRule> mamdaniRules = new ArrayList<>();
        List<FuzzyRule> sugenoRules = new ArrayList<>();

        for (FuzzyRule rule : allRules) {
            if (!rule.isEnabled()) continue;

            if (isNumeric(rule.getConsequent().getValue()))
                sugenoRules.add(rule);
            else
                mamdaniRules.add(rule);
        }

        // ---- Mamdani using PRODUCT/PROBOR ----
        if (!mamdaniRules.isEmpty()) {
            List<FuzzySet> clippedOutputs = MamdaniInference.infer(
                    mamdaniRules,
                    fuzzifiedInputs,
                    severitySets,
                    tNorm,    // uses PRODUCT if chosen
                    sNorm     // uses SUM (PROBOR) if chosen
            );

            System.out.println("\n--- Clipped Outputs per Rule (ADVANCED) ---");
            for (FuzzySet clipped : clippedOutputs) {
                System.out.println("Rule consequent: " + clipped.getSetName());
                for (double x = 0; x <= 10; x += 0.5) {
                    double mu = clipped.evaluateMembership(x);
                    System.out.printf("x=%.1f -> μ=%.3f%n", x, mu);
                }
                System.out.println();
            }

            FuzzySet aggregated = Aggregation.maxAggregate("IllnessSeverity", clippedOutputs);
            System.out.println("\n--- Aggregated Output (ADVANCED) ---");
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

            result.put("mamdaniCentroid", centroid);
            result.put("mamdaniMaxMembership", maxMembership);
        }

        // ---- Sugeno using PRODUCT/PROBOR ----
        if (!sugenoRules.isEmpty()) {
            double sugenoCrisp = SugenoInference.infer(
                    sugenoRules,
                    fuzzifiedInputs,
                    tNorm,
                    sNorm
            );
            result.put("sugenoCrisp", sugenoCrisp);
        }

        return result;
    }


    // ------------------------ ADVANCED ENDPOINT ------------------------
    @GetMapping("/runAdvanced")
    public Map<String, Object> runAdvancedInferenceApi(
            @RequestParam double temp,
            @RequestParam double heartRate,
            @RequestParam(defaultValue = "MIN") String andOp,
            @RequestParam(defaultValue = "MAX") String orOp,
            @RequestParam(defaultValue = "CENTROID") String defuzz
    ) {
        TNorm tNorm = "PRODUCT".equalsIgnoreCase(andOp) ? TNorm.PRODUCT : TNorm.MIN;
        SNorm sNorm = "PROBOR".equalsIgnoreCase(orOp) ? SNorm.SUM : SNorm.MAX;

        Map<String, Object> result = runInferenceAdvanced(temp, heartRate, tNorm, sNorm);

        if (result.containsKey("mamdaniCentroid") && result.containsKey("mamdaniMaxMembership")) {
            double c = (double) result.get("mamdaniCentroid");
            double m = (double) result.get("mamdaniMaxMembership");

            result.put("mamdaniOutputAdvanced",
                    "MAXMEMBERSHIP".equalsIgnoreCase(defuzz) ? m : c
            );
        }

        result.put("TNormUsed", tNorm.name());
        result.put("SNormUsed", sNorm.name());
        result.put("DefuzzMethodUsed", defuzz);

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
