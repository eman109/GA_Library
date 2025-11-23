package fuzzylogic.operators;


import fuzzylogic.operators.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operators")
public class OperatorTestController {

    // -------------------------
    // Test AND operator
    // -------------------------
    @PostMapping("/and")
    public double testAnd(
            @RequestParam double a,
            @RequestParam double b,
            @RequestParam TNorm type
    ) {
        return FuzzyOperators.and(a, b, type);
    }

    // -------------------------
    // Test OR operator
    // -------------------------
    @PostMapping("/or")
    public double testOr(
            @RequestParam double a,
            @RequestParam double b,
            @RequestParam SNorm type
    ) {
        return FuzzyOperators.or(a, b, type);
    }

    // -------------------------
    // Test implication (clipping)
    // -------------------------
    @PostMapping("/implication")
    public double[] testImplication(
            @RequestParam double firingStrength,
            @RequestBody double[] outputMF
    ) {
        return Implication.clipImplication(firingStrength, outputMF);
    }

    // -------------------------
    // Test aggregation
    // -------------------------
    @PostMapping("/aggregate")
    public double[] testAggregation(
            @RequestBody List<double[]> clippedMfs
    ) {
        return Aggregation.sumAggregate(clippedMfs);
    }
}
